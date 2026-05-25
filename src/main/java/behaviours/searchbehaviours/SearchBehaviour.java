package behaviours.searchbehaviours;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import utils.IngredientTranslator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

/**
 * Busca recetas en Spoonacular usando /findByIngredients (ranking=1).
 *
 * Recibe del InterfaceAgent un mensaje multi-línea con el formato:
 *   ingredients=tomato,chicken,rice
 *   userQuantities=chicken:300,rice:200
 *   persons=2
 *   maxTime=30
 *   restrictions=vegetarian, gluten free
 *   preferences=quick, healthy
 *   mealType=dinner
 *
 * Los filtros de dieta, tiempo y tipo de comida no los aplica este endpoint;
 * se gestionan en RecommendationBehaviour sin coste extra de API.
 */
public class SearchBehaviour extends AchieveREResponder {

    private static final String SPOONACULAR_BASE =
            "https://api.spoonacular.com/recipes/findByIngredients";

    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiKey;

    public SearchBehaviour(Agent agent, MessageTemplate template,
                           HttpClient httpClient, Gson gson, String apiKey) {
        super(agent, template);
        this.httpClient = httpClient;
        this.gson       = gson;
        this.apiKey     = apiKey;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) {
        System.out.println("RecipeSearchAgent: solicitud recibida de "
                + request.getSender().getLocalName());
        ACLMessage agree = request.createReply();
        agree.setPerformative(ACLMessage.AGREE);
        return agree;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        ACLMessage reply = request.createReply();

        String rawContent    = request.getContent().trim();
        String ingredients   = "";
        String userQuantities = "";
        int    maxTime       = -1;
        String restrictions  = "";
        String preferences   = "";
        int    persons       = -1;
        String mealType      = "";

        for (String line : rawContent.split("\n")) {
            if      (line.startsWith("ingredients="))    ingredients    = line.substring("ingredients=".length()).trim();
            else if (line.startsWith("userQuantities=")) userQuantities = line.substring("userQuantities=".length()).trim();
            else if (line.startsWith("maxTime="))        { try { maxTime = Integer.parseInt(line.substring("maxTime=".length()).trim()); } catch (NumberFormatException ignored) {} }
            else if (line.startsWith("persons="))        { try { persons = Integer.parseInt(line.substring("persons=".length()).trim());  } catch (NumberFormatException ignored) {} }
            else if (line.startsWith("restrictions="))   restrictions   = line.substring("restrictions=".length()).trim();
            else if (line.startsWith("preferences="))    preferences    = line.substring("preferences=".length()).trim();
            else if (line.startsWith("mealType="))       mealType       = line.substring("mealType=".length()).trim();
        }
        if (ingredients.isEmpty()) ingredients = rawContent;

        String ingredientsEn = IngredientTranslator.translateIngredients(ingredients);
        if (!ingredientsEn.equals(ingredients)) {
            System.out.println("RecipeSearchAgent: traducción ES→EN: '" + ingredients
                    + "' → '" + ingredientsEn + "'");
        }
        ingredients = ingredientsEn;

        System.out.println("RecipeSearchAgent: buscando '" + ingredients + "'"
                + (mealType.isEmpty() || mealType.equals("any") ? "" : " tipo=" + mealType)
                + (maxTime > 0 ? " maxTime=" + maxTime : "")
                + (restrictions.isEmpty() ? "" : " restrict=" + restrictions));

        try {
            String url = buildComplexSearchUrl(ingredients, restrictions, mealType, maxTime);
            System.out.println("RecipeSearchAgent: URL = " + url);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url)).GET().build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                JsonArray meals = gson.fromJson(httpResponse.body(), JsonArray.class);

                JsonArray recipesArray = new JsonArray();
                for (JsonElement el : meals) {
                    JsonObject meal = el.getAsJsonObject();
                    JsonObject rec  = new JsonObject();
                    rec.addProperty("id",   meal.get("id").getAsInt());
                    rec.addProperty("name", meal.get("title").getAsString());
                    recipesArray.add(rec);
                }

                JsonObject result = new JsonObject();
                result.addProperty("userIngredients", ingredients);
                result.add("recipes", recipesArray);
                if (!userQuantities.isEmpty()) result.addProperty("userQuantities", userQuantities);
                if (maxTime > 0)               result.addProperty("maxTime",        maxTime);
                if (persons > 0)               result.addProperty("persons",        persons);
                if (!restrictions.isEmpty())   result.addProperty("restrictions",   restrictions);
                if (!preferences.isEmpty())    result.addProperty("preferences",    preferences);

                ACLMessage forward = new ACLMessage(ACLMessage.INFORM);
                forward.addReceiver(new AID("TextMiningAgent", AID.ISLOCALNAME));
                forward.setConversationId("RECIPE_SEARCH_RESULT");
                forward.setContent(result.toString());
                myAgent.send(forward);

                System.out.println("RecipeSearchAgent: " + recipesArray.size()
                        + " recetas encontradas, enviadas a TextMiningAgent.");

                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Búsqueda completada: " + recipesArray.size() + " recetas.");

            } else {
                System.err.println("RecipeSearchAgent: Spoonacular error " + httpResponse.statusCode()
                        + " — " + httpResponse.body());
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("{\"error\": \"Spoonacular respondió con estado: "
                        + httpResponse.statusCode() + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("{\"error\": \"Error comunicando con Spoonacular.\"}");
        }

        return reply;
    }

    // ranking=1 prioriza recetas que usan el mayor número de ingredientes del usuario.
    // ignorePantry evita que sal, agua y otros básicos inflen el score artificialmente.
    private String buildComplexSearchUrl(String ingredients, String restrictions,
                                         String mealType, int maxTime) {
        StringBuilder sb = new StringBuilder(SPOONACULAR_BASE)
                .append("?number=3")
                .append("&ranking=1")
                .append("&ignorePantry=true");

        if (!ingredients.isEmpty()) {
            sb.append("&ingredients=")
              .append(URLEncoder.encode(ingredients, StandardCharsets.UTF_8));
        }

        sb.append("&apiKey=").append(apiKey);
        return sb.toString();
    }
}
