package behaviours.nutritionbehaviours;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Sirve datos nutricionales a RecommendationAgent.
 *
 * Escucha dos tipos de mensaje:
 *   - NUTRITION_PREFETCH: TextMiningAgent rellena la caché con datos ya
 *     extraídos de /information?includeNutrition=true (sin coste extra de API).
 *   - NUTRITION_RESULT_REQUEST: RecommendationAgent pide la nutrición de una
 *     receta. Se responde desde caché; solo se llama a /nutritionWidget.json
 *     si la caché está vacía (caso excepcional).
 *
 * Entrada:  JSON {"id": <recipeId>, "name": "<nombre>"}
 * Salida:   JSON {"recipe":"...", "calories":"...", "carbs":"...", "fat":"...", "protein":"..."}
 */
public class NutritionBehaviour extends CyclicBehaviour {

    private static final String CONV_IN      = "NUTRITION_RESULT_REQUEST";
    private static final String CONV_PREFETCH = "NUTRITION_PREFETCH";
    private static final String CONV_OUT     = "NUTRITION_RESULT";

    private final HttpClient              httpClient;
    private final Gson                    gson;
    private final String                  apiKey;
    // Rellena TextMiningAgent antes de que lleguen las peticiones de RecommendationAgent
    private final Map<Integer, JsonObject> nutritionCache = new HashMap<>();

    public NutritionBehaviour(Agent a, HttpClient httpClient, Gson gson, String apiKey) {
        super(a);
        this.httpClient = httpClient;
        this.gson       = gson;
        this.apiKey     = apiKey;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.or(
                        MessageTemplate.MatchConversationId(CONV_IN),
                        MessageTemplate.MatchConversationId(CONV_PREFETCH)
                )
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg == null) {
            block();
            return;
        }

        if (CONV_PREFETCH.equals(msg.getConversationId())) {
            // TextMiningAgent nos envía la nutrición ya extraída → guardamos en caché
            try {
                JsonObject data = gson.fromJson(msg.getContent(), JsonObject.class);
                int id = data.get("id").getAsInt();
                nutritionCache.put(id, data);
                System.out.println("NutritionAgent: nutrición cacheada para id=" + id
                        + " (" + safeString(data, "name") + ")");
            } catch (Exception e) {
                System.err.println("NutritionAgent: error cacheando prefetch: " + e.getMessage());
            }
            return;
        }

        System.out.println("NutritionAgent: solicitud recibida: " + msg.getContent());

        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(msg.getSender());
        reply.setConversationId(CONV_OUT);
        if (msg.getReplyWith() != null && !msg.getReplyWith().isEmpty()) {
            reply.setInReplyTo(msg.getReplyWith());
        }

        try {
            JsonObject requestData = gson.fromJson(msg.getContent(), JsonObject.class);
            int    recipeId   = requestData.get("id").getAsInt();
            String recipeName = requestData.get("name").getAsString();

            // La caché suele estar rellena; el fallback a API es excepcional
            JsonObject cached = nutritionCache.get(recipeId);
            if (cached != null) {
                JsonObject result = new JsonObject();
                result.addProperty("recipe",   recipeName);
                result.addProperty("calories", safeString(cached, "calories"));
                result.addProperty("carbs",    safeString(cached, "carbs"));
                result.addProperty("fat",      safeString(cached, "fat"));
                result.addProperty("protein",  safeString(cached, "protein"));
                reply.setContent(result.toString());
                System.out.println("NutritionAgent: nutrición desde caché para '" + recipeName + "'");
                myAgent.send(reply);
                return;
            }

            System.out.println("NutritionAgent: caché vacío, consultando API para id=" + recipeId);
            String url = "https://api.spoonacular.com/recipes/" + recipeId
                    + "/nutritionWidget.json?apiKey=" + apiKey;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url)).GET().build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                JsonObject nutritionData = gson.fromJson(httpResponse.body(), JsonObject.class);
                JsonObject result = new JsonObject();
                result.addProperty("recipe",   recipeName);
                result.addProperty("calories", safeString(nutritionData, "calories"));
                result.addProperty("carbs",    safeString(nutritionData, "carbs"));
                result.addProperty("fat",      safeString(nutritionData, "fat"));
                result.addProperty("protein",  safeString(nutritionData, "protein"));
                reply.setContent(result.toString());
                System.out.println("NutritionAgent: datos obtenidos de API para '" + recipeName + "'");
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("{\"error\": \"Spoonacular devolvió " + httpResponse.statusCode() + "\"}");
                System.err.println("NutritionAgent: Spoonacular error "
                        + httpResponse.statusCode() + " para id=" + recipeId);
            }

        } catch (Exception e) {
            System.err.println("NutritionAgent: error: " + e.getMessage());
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("{\"error\": \"" + e.getMessage() + "\"}");
        }

        myAgent.send(reply);
    }

    private String safeString(JsonObject obj, String key) {
        return (obj.has(key) && !obj.get(key).isJsonNull())
                ? obj.get(key).getAsString()
                : "?";
    }
}
