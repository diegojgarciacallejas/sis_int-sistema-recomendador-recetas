package behaviours.miningbehaviours;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Comportamiento cíclico que obtiene detalles de recetas desde Spoonacular,
 * ejecuta similitud TF-IDF contra los ingredientes del usuario y reenvía
 * los resultados al OntologyAgent.
 *
 * RecipeFetcher es una interfaz funcional que puede intercambiarse en pruebas.
 */
public class TextMiningBehaviour extends CyclicBehaviour {

    private static final String API_KEY        = "74e8728ac10847199e9b7db0f0d97a4e";
    private static final String CONV_IN        = "RECIPE_SEARCH_RESULT";
    private static final String CONV_OUT       = "TEXT_MINING_RESULT";
    private static final String ONTOLOGY_AGENT = "OntologyAgent";

    /** IDF precomputado sobre el corpus RecipeNLG, cargado desde el classpath. */
    private static final String IDF_RESOURCE = "/idf_corpus.json";

    /** Verbos de instrucciones de cocina — Spoonacular ocasionalmente filtra texto de pasos en extendedIngredients. */
    private static final Set<String> INSTRUCTION_VERBS = new HashSet<>(Arrays.asList(
            "add", "heat", "cook", "stir", "mix", "put", "place", "pour",
            "boil", "fry", "bake", "roast", "grill", "simmer", "saute",
            "chop", "slice", "dice", "combine", "whisk", "blend", "drain",
            "peel", "cut", "remove", "serve", "bring", "season", "toss"
    ));

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "in", "on", "at", "to", "for",
            "of", "with", "by", "from", "is", "are", "was", "were", "be", "been",
            "have", "has", "had", "do", "does", "did", "will", "would", "could",
            "should", "it", "its", "this", "that", "i", "you", "he", "she", "we",
            "they", "as", "up", "out", "into", "over", "if", "so", "than", "then",
            "per", "also", "very", "just", "your", "about", "some", "more", "all",
            "not", "no", "can", "our", "their", "each", "both", "such", "these",
            "those", "get", "got", "one", "two", "three", "four", "five", "six"
    ));

    @FunctionalInterface
    public interface RecipeFetcher {
        String fetch(int id) throws Exception;
    }

    private final RecipeFetcher fetcher;
    private final Gson gson;

    private final Map<String, Double> corpusIdf;

    public TextMiningBehaviour(Agent agent) {
        super(agent);
        this.gson      = new Gson();
        this.corpusIdf = loadCorpusIdf();

        HttpClient httpClient = HttpClient.newHttpClient();
        this.fetcher = id -> {
            String url = "https://api.spoonacular.com/recipes/" + id
                    + "/information?includeNutrition=true&apiKey=" + API_KEY;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }
            System.err.println("TextMiningAgent: Spoonacular devolvió "
                    + response.statusCode() + " para id=" + id);
            return null;
        };
    }

    TextMiningBehaviour(Agent agent, RecipeFetcher fetcher) {
        super(agent);
        this.fetcher   = fetcher;
        this.gson      = new Gson();
        this.corpusIdf = loadCorpusIdf();
    }

    TextMiningBehaviour(Agent agent, RecipeFetcher fetcher, Map<String, Double> corpusIdf) {
        super(agent);
        this.fetcher   = fetcher;
        this.gson      = new Gson();
        this.corpusIdf = corpusIdf;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CONV_IN);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            System.out.println("TextMiningAgent <- recibe de RecipeSearchAgent:");
            System.out.println(msg.getContent());

            String output = processInput(msg.getContent());

            ACLMessage forward = new ACLMessage(ACLMessage.INFORM);
            forward.addReceiver(new AID(ONTOLOGY_AGENT, AID.ISLOCALNAME));
            forward.setConversationId(CONV_OUT);
            forward.setContent(output);
            myAgent.send(forward);

            System.out.println("TextMiningAgent -> envia a OntologyAgent:");
            System.out.println(output);

            Iterator<AID> replyToIt = msg.getAllReplyTo();
            if (replyToIt.hasNext()) {
                AID replyTo = replyToIt.next();
                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                reply.addReceiver(replyTo);
                reply.setConversationId(CONV_OUT);
                reply.setContent(output);
                myAgent.send(reply);
                System.out.println("TextMiningAgent -> reply-to: " + replyTo.getLocalName());
            }

        } else {
            block();
        }
    }

    String processInput(String jsonInput) {
        try {
            JsonObject root = gson.fromJson(jsonInput, JsonObject.class);

            String userIngredients = root.has("userIngredients")
                    ? root.get("userIngredients").getAsString().trim()
                    : "";

            int    maxTime      = root.has("maxTime")        ? root.get("maxTime").getAsInt()                  : -1;
            String restrictions = root.has("restrictions")   ? root.get("restrictions").getAsString().trim()   : "";
            String userQuantities = root.has("userQuantities") ? root.get("userQuantities").getAsString().trim() : "";
            String preferences  = root.has("preferences")    ? root.get("preferences").getAsString().trim()    : "";
            int    persons      = root.has("persons")        ? root.get("persons").getAsInt()                  : -1;

            JsonArray recipes = root.has("recipes")
                    ? root.getAsJsonArray("recipes")
                    : new JsonArray();

            Map<String, CompletableFuture<RecipeData>> futures = new LinkedHashMap<>();
            for (JsonElement el : recipes) {
                JsonObject recipe = el.getAsJsonObject();
                int    id   = recipe.get("id").getAsInt();
                String name = recipe.get("name").getAsString();
                String key  = sanitizeName(name);
                futures.put(key, CompletableFuture.supplyAsync(() -> fetchAndExtract(id, name)));
            }

            Map<String, RecipeData> recipeDataMap = new LinkedHashMap<>();
            for (Map.Entry<String, CompletableFuture<RecipeData>> entry : futures.entrySet()) {
                try {
                    recipeDataMap.put(entry.getKey(), entry.getValue().get());
                } catch (Exception e) {
                    System.err.println("TextMiningAgent: error en fetch paralelo: " + e.getMessage());
                }
            }

            Map<String, Double> tfidfScores = computeTfIdfScores(userIngredients, recipeDataMap);

            // Pre-cache nutrition data extracted from /information?includeNutrition=true
            if (myAgent != null) {
                for (RecipeData d : recipeDataMap.values()) {
                    if (d.hasNutrition()) {
                        JsonObject prefetch = new JsonObject();
                        prefetch.addProperty("id",       d.id);
                        prefetch.addProperty("name",     d.name);
                        prefetch.addProperty("calories", d.calories);
                        prefetch.addProperty("carbs",    d.carbs);
                        prefetch.addProperty("fat",      d.fat);
                        prefetch.addProperty("protein",  d.protein);
                        ACLMessage pm = new ACLMessage(ACLMessage.INFORM);
                        pm.addReceiver(new AID("NutritionAgent", AID.ISLOCALNAME));
                        pm.setConversationId("NUTRITION_PREFETCH");
                        pm.setContent(prefetch.toString());
                        myAgent.send(pm);
                        System.out.println("TextMiningAgent: nutrición pre-cacheada para '"
                                + d.name + "' (" + d.calories + " cal)");
                    }
                }
            }

            return buildOutputMessage(userIngredients, recipeDataMap, tfidfScores,
                    maxTime, restrictions, userQuantities, preferences, persons);

        } catch (Exception e) {
            System.err.println("TextMiningAgent: error procesando entrada: " + e.getMessage());
            return "error=TextMiningAgent no pudo procesar la entrada\n";
        }
    }

    RecipeData fetchAndExtract(int id, String name) {
        try {
            String body = fetcher.fetch(id);
            if (body != null) {
                return extractRecipeData(id, name, body);
            }
        } catch (Exception e) {
            System.err.println("TextMiningAgent: error obteniendo receta id="
                    + id + " -> " + e.getMessage());
        }
        return new RecipeData(id, name, new ArrayList<>(), new ArrayList<>(),
                -1, -1, false, false, false, false,
                new ArrayList<>(), new ArrayList<>(), -1, "", "");
    }

    RecipeData extractRecipeData(int id, String name, String jsonBody) {
        JsonObject data = gson.fromJson(jsonBody, JsonObject.class);

        List<String> ingredients             = new ArrayList<>();
        List<IngredientInfo> ingredientDetails = new ArrayList<>();

        if (data.has("extendedIngredients")) {
            for (JsonElement el : data.getAsJsonArray("extendedIngredients")) {
                JsonObject ing = el.getAsJsonObject();
                String rawName = getStringSafe(ing, "nameClean");
                if (rawName.isEmpty()) rawName = getStringSafe(ing, "name");
                if (rawName.isEmpty() || !isValidIngredientName(rawName)) continue;
                String normalized = normalize(rawName);
                if (!normalized.isEmpty()) {
                    ingredients.add(normalized);
                    // Prefer measures.metric — Spoonacular pre-converts to g/ml
                    double amount;
                    String unit;
                    if (ing.has("measures") && !ing.get("measures").isJsonNull()) {
                        JsonObject metric = ing.getAsJsonObject("measures")
                                             .getAsJsonObject("metric");
                        amount = getDoubleSafe(metric, "amount");
                        unit   = sanitizeUnit(getStringSafe(metric, "unitShort"));
                    } else {
                        amount = getDoubleSafe(ing, "amount");
                        unit   = sanitizeUnit(getStringSafe(ing, "unit"));
                    }
                    ingredientDetails.add(new IngredientInfo(normalized, amount, unit));
                }
            }
        }

        int readyInMinutes = getIntSafe(data, "readyInMinutes");
        int servings       = getIntSafe(data, "servings");
        boolean vegan      = getBoolSafe(data, "vegan");
        boolean vegetarian = getBoolSafe(data, "vegetarian");
        boolean glutenFree = getBoolSafe(data, "glutenFree");
        boolean dairyFree  = getBoolSafe(data, "dairyFree");

        List<String> cuisines = new ArrayList<>();
        if (data.has("cuisines") && data.get("cuisines").isJsonArray())
            for (JsonElement c : data.getAsJsonArray("cuisines"))
                if (!c.isJsonNull()) { String s = sanitizeName(c.getAsString()); if (!s.isEmpty()) cuisines.add(s.toLowerCase()); }

        List<String> dishTypes = new ArrayList<>();
        if (data.has("dishTypes") && data.get("dishTypes").isJsonArray())
            for (JsonElement d : data.getAsJsonArray("dishTypes"))
                if (!d.isJsonNull()) { String s = sanitizeName(d.getAsString()); if (!s.isEmpty()) dishTypes.add(s.toLowerCase()); }

        int healthScore = getIntSafe(data, "healthScore");

        String summary      = stripHtml(getStringSafe(data, "summary"));
        String instructions = stripHtml(getStringSafe(data, "instructions"));
        String rawText      = (summary + " " + instructions).trim();

        // Nutrición — disponible cuando la URL incluye includeNutrition=true
        String calories = "", carbs = "", fat = "", protein = "";
        if (data.has("nutrition") && !data.get("nutrition").isJsonNull()) {
            JsonObject nutrition = data.getAsJsonObject("nutrition");
            if (nutrition.has("nutrients") && nutrition.get("nutrients").isJsonArray()) {
                for (JsonElement ne : nutrition.getAsJsonArray("nutrients")) {
                    JsonObject n = ne.getAsJsonObject();
                    String nName = getStringSafe(n, "name");
                    int amount   = (int) Math.round(getDoubleSafe(n, "amount"));
                    switch (nName) {
                        case "Calories":      calories = String.valueOf(amount); break;
                        case "Carbohydrates": carbs    = amount + "g";          break;
                        case "Fat":           fat      = amount + "g";          break;
                        case "Protein":       protein  = amount + "g";          break;
                    }
                }
            }
        }

        return new RecipeData(id, name, ingredients, ingredientDetails,
                readyInMinutes, servings, vegan, vegetarian, glutenFree, dairyFree,
                cuisines, dishTypes, healthScore, rawText, instructions,
                calories, carbs, fat, protein);
    }

    /** Overload sin preferencias — mantiene compatibilidad con tests existentes. */
    String buildOutputMessage(String userIngredients,
                              Map<String, RecipeData> recipeDataMap,
                              Map<String, Double> tfidfScores) {
        return buildOutputMessage(userIngredients, recipeDataMap, tfidfScores, -1, "", "", "", -1);
    }

    /** Sobrecarga sin cantidades — para pruebas que usan maxTime + restricciones. */
    String buildOutputMessage(String userIngredients,
                              Map<String, RecipeData> recipeDataMap,
                              Map<String, Double> tfidfScores,
                              int maxTime,
                              String restrictions) {
        return buildOutputMessage(userIngredients, recipeDataMap, tfidfScores, maxTime, restrictions, "", "", -1);
    }

    /** Sobrecarga con cantidades pero sin preferencias/personas. */
    String buildOutputMessage(String userIngredients,
                              Map<String, RecipeData> recipeDataMap,
                              Map<String, Double> tfidfScores,
                              int maxTime,
                              String restrictions,
                              String userQuantities) {
        return buildOutputMessage(userIngredients, recipeDataMap, tfidfScores,
                maxTime, restrictions, userQuantities, "", -1);
    }

    String buildOutputMessage(String userIngredients,
                              Map<String, RecipeData> recipeDataMap,
                              Map<String, Double> tfidfScores,
                              int maxTime,
                              String restrictions,
                              String userQuantities,
                              String preferences,
                              int persons) {
        StringBuilder recipesLine        = new StringBuilder("recipes=");
        StringBuilder ingredientsLine    = new StringBuilder("recipeIngredients=");
        StringBuilder timesLine          = new StringBuilder("recipeTimes=");
        StringBuilder servingsLine       = new StringBuilder("recipeServings=");
        StringBuilder tagsLine           = new StringBuilder("recipeTags=");
        StringBuilder cuisinesLine       = new StringBuilder("recipeCuisines=");
        StringBuilder dishTypesLine      = new StringBuilder("recipeDishTypes=");
        StringBuilder healthScoresLine   = new StringBuilder("recipeHealthScores=");
        StringBuilder tfidfLine          = new StringBuilder("recipeTfIdfScores=");
        StringBuilder instructionsLine   = new StringBuilder("recipeInstructions=");
        StringBuilder recipeIdsLine      = new StringBuilder("recipeIds=");

        boolean first = true;
        for (Map.Entry<String, RecipeData> entry : recipeDataMap.entrySet()) {
            if (!first) {
                recipesLine.append(";");
                ingredientsLine.append(";");
                timesLine.append(";");
                servingsLine.append(";");
                tagsLine.append(";");
                cuisinesLine.append(";");
                dishTypesLine.append(";");
                healthScoresLine.append(";");
                tfidfLine.append(";");
                instructionsLine.append(";");
                recipeIdsLine.append(";");
            }
            first = false;

            String     key = entry.getKey();
            RecipeData d   = entry.getValue();

            recipesLine.append(key).append(":").append(String.join(",", d.ingredients));

            ingredientsLine.append(key).append(":");
            for (int i = 0; i < d.ingredientDetails.size(); i++) {
                if (i > 0) ingredientsLine.append(",");
                IngredientInfo info = d.ingredientDetails.get(i);
                double[] converted = convertToStandardUnit(info.amount, info.unit);
                ingredientsLine.append(info.name)
                        .append("|").append(formatAmount(converted[0]))
                        .append("|").append(unitLabel((int) converted[1]));
            }

            timesLine.append(key).append(":").append(d.readyInMinutes);
            servingsLine.append(key).append(":").append(d.servings);

            List<String> tags = new ArrayList<>();
            if (d.vegan)      tags.add("vegan");
            if (d.vegetarian) tags.add("vegetarian");
            if (d.glutenFree) tags.add("glutenFree");
            if (d.dairyFree)  tags.add("dairyFree");
            tagsLine.append(key).append(":").append(String.join(",", tags));

            cuisinesLine.append(key).append(":").append(String.join(",", d.cuisines));
            dishTypesLine.append(key).append(":").append(String.join(",", d.dishTypes));
            healthScoresLine.append(key).append(":").append(d.healthScore);

            double score = tfidfScores.getOrDefault(key, 0.0);
            tfidfLine.append(key).append(":").append(String.format(Locale.US, "%.4f", score));

            // Limpiamos las instrucciones para evitar conflictos con los separadores ';' y '|'
            String safeInstructions = d.instructions
                    .replaceAll(";", ",")
                    .replaceAll("\\|", "-")
                    .replaceAll("\\s+", " ")
                    .trim();
            instructionsLine.append(key).append(":").append(safeInstructions);

            recipeIdsLine.append(key).append(":").append(d.id);
        }

        String userPrefsLine = buildUserPrefsLine(maxTime, restrictions, preferences, persons);

        return "userIngredients=" + userIngredients + "\n"
                + recipesLine        + "\n"
                + ingredientsLine    + "\n"
                + timesLine          + "\n"
                + servingsLine       + "\n"
                + tagsLine           + "\n"
                + cuisinesLine       + "\n"
                + dishTypesLine      + "\n"
                + healthScoresLine   + "\n"
                + tfidfLine          + "\n"
                + instructionsLine   + "\n"
                + recipeIdsLine      + "\n"
                + userPrefsLine      + "\n"
                + (userQuantities.isEmpty() ? "" : "userQuantities=" + userQuantities + "\n");
    }

    private String buildUserPrefsLine(int maxTime, String restrictions,
                                      String preferences, int persons) {
        StringBuilder sb = new StringBuilder("userPrefs=");
        sb.append("maxTime:").append(maxTime);
        sb.append(";restrictions:").append(restrictions.replace(";", ","));
        if (preferences != null && !preferences.isEmpty()) {
            sb.append(";preferences:").append(preferences.replace(";", ","));
        }
        if (persons > 0) {
            sb.append(";persons:").append(persons);
        }
        return sb.toString();
    }

    private Map<String, Double> loadCorpusIdf() {
        try (InputStream is = getClass().getResourceAsStream(IDF_RESOURCE)) {
            if (is == null) {
                System.out.println("TextMiningAgent: idf_corpus.json no encontrado. "
                        + "Usando IDF local (genera el fichero con utils.IdfCorpusBuilder).");
                return new HashMap<>();
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                @SuppressWarnings("unchecked")
                Map<String, Double> idf = gson.fromJson(reader, Map.class);
                System.out.println("TextMiningAgent: IDF de RecipeNLG cargado ("
                        + idf.size() + " terminos).");
                return idf;
            }
        } catch (Exception e) {
            System.err.println("TextMiningAgent: error cargando idf_corpus.json: " + e.getMessage());
            return new HashMap<>();
        }
    }

    Map<String, Double> computeTfIdfScores(String userIngredients,
                                           Map<String, RecipeData> recipeDataMap) {
        String userText = userIngredients.replace(",", " ");
        List<String> userTokens = tokenize(userText);

        // Aplicamos TF-IDF sobre los nombres de los ingredientes, no sobre las instrucciones,
        // para obtener una similitud más precisa entre ingredientes
        Map<String, List<String>> recipeTokens = new LinkedHashMap<>();
        for (Map.Entry<String, RecipeData> entry : recipeDataMap.entrySet()) {
            String ingredientText = String.join(" ", entry.getValue().ingredients);
            recipeTokens.put(entry.getKey(), tokenize(ingredientText));
        }

        // Usamos el IDF precomputado del corpus si está disponible;
        // en caso contrario, calculamos un IDF local como respaldo
        Map<String, Double> idf;
        if (!corpusIdf.isEmpty()) {
            idf = corpusIdf;
        } else {
            List<List<String>> localCorpus = new ArrayList<>();
            localCorpus.add(userTokens);
            localCorpus.addAll(recipeTokens.values());
            idf = computeIdf(localCorpus);
        }

        Map<String, Double> userTfIdf = computeTfIdfVector(computeTf(userTokens), idf);

        Map<String, Double> scores = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : recipeTokens.entrySet()) {
            Map<String, Double> recipeTfIdf = computeTfIdfVector(computeTf(entry.getValue()), idf);
            scores.put(entry.getKey(), cosineSimilarity(userTfIdf, recipeTfIdf));
        }

        return scores;
    }

    String stripHtml(String html) {
        if (html == null || html.isEmpty()) return "";
        return html.replaceAll("<[^>]+>", " ")
                   .replaceAll("&[a-zA-Z]+;", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty()) return tokens;

        String clean = text.toLowerCase()
                           .replaceAll("[^a-z0-9\\s]", " ")
                           .replaceAll("\\s+", " ")
                           .trim();

        for (String token : clean.split(" ")) {
            if (token.length() >= 3 && !STOPWORDS.contains(token)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    Map<String, Double> computeTf(List<String> tokens) {
        Map<String, Double> tf = new HashMap<>();
        if (tokens.isEmpty()) return tf;

        for (String token : tokens) {
            tf.merge(token, 1.0, Double::sum);
        }
        double total = tokens.size();
        tf.replaceAll((k, v) -> v / total);
        return tf;
    }

    // IDF(t) = log((1 + N) / (1 + df(t))) + 1
    // Aplicamos suavizado para evitar divisiones entre cero
    Map<String, Double> computeIdf(List<List<String>> corpus) {
        Map<String, Integer> df = new HashMap<>();
        int N = corpus.size();

        for (List<String> doc : corpus) {
            Set<String> uniqueTerms = new HashSet<>(doc);
            for (String term : uniqueTerms) {
                df.merge(term, 1, Integer::sum);
            }
        }

        Map<String, Double> idf = new HashMap<>();
        for (Map.Entry<String, Integer> entry : df.entrySet()) {
            double val = Math.log((1.0 + N) / (1.0 + entry.getValue())) + 1.0;
            idf.put(entry.getKey(), val);
        }
        return idf;
    }

    Map<String, Double> computeTfIdfVector(Map<String, Double> tf, Map<String, Double> idf) {
        Map<String, Double> tfidf = new HashMap<>();
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            String term   = entry.getKey();
            double idfVal = idf.getOrDefault(term, 0.0);
            tfidf.put(term, entry.getValue() * idfVal);
        }
        return tfidf;
    }

    // cos(A, B) = (A · B) / (||A|| × ||B||)
    double cosineSimilarity(Map<String, Double> v1, Map<String, Double> v2) {
        double dotProduct = 0.0;
        double norm1      = 0.0;
        double norm2      = 0.0;

        for (Map.Entry<String, Double> entry : v1.entrySet()) {
            dotProduct += entry.getValue() * v2.getOrDefault(entry.getKey(), 0.0);
            norm1      += entry.getValue() * entry.getValue();
        }
        for (double val : v2.values()) {
            norm2 += val * val;
        }

        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // Descarta fragmentos de instrucciones que Spoonacular a veces incluye por error en extendedIngredients
    boolean isValidIngredientName(String name) {
        if (name == null || name.isBlank()) return false;
        String lower = name.trim().toLowerCase();
        if (lower.length() > 50) return false;
        String[] words = lower.split("\\s+");
        if (words.length > 5) return false;
        if (words.length > 0 && INSTRUCTION_VERBS.contains(words[0])) return false;
        if (lower.startsWith("in ") || lower.startsWith("over ")
                || lower.startsWith("at ") || lower.startsWith("on a ")) return false;
        return true;
    }

    String normalize(String text) {
        return text.trim()
                .toLowerCase()
                .replaceAll("[;:\n]", "")
                .replaceAll("[^a-z0-9 ()]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    String sanitizeName(String name) {
        // Sustituimos caracteres que podrían romper el protocolo de mensajes
        // o causar errores al parsear cadenas separadas por '|'
        return name.replaceAll("[;:|\n]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String sanitizeUnit(String unit) {
        if (unit == null) return "";
        return unit.replaceAll("[|;:\n]", "").trim();
    }

    // Códigos de salida de unidades: 0=g, 1=ml, 2=cda, 3=cdta, 4=unidad original
    private static final int U_G    = 0;
    private static final int U_ML   = 1;
    private static final int U_TBSP = 2;
    private static final int U_TSP  = 3;
    private static final int U_ORIG = 4;

    private double[] convertToStandardUnit(double amount, String unit) {
        if (unit == null) return new double[]{amount, U_ORIG};
        String u = unit.trim().toLowerCase();
        switch (u) {
            case "oz":    case "ounce":  case "ounces":
                return new double[]{amount * 28.3495, U_G};
            case "lb":    case "lbs":    case "pound": case "pounds":
                return new double[]{amount * 453.592, U_G};
            case "kg":    case "kilogram": case "kilograms":
                return new double[]{amount * 1000.0,  U_G};
            case "g":     case "gram":   case "grams":
                return new double[]{amount, U_G};
            case "l":     case "liter":  case "litre": case "liters": case "litres":
                return new double[]{amount * 1000.0,  U_ML};
            case "ml":    case "milliliter": case "millilitre":
                return new double[]{amount, U_ML};
            case "fl oz": case "fluid ounce": case "fluid ounces":
                return new double[]{amount * 29.5735, U_ML};
            case "cup":   case "cups":
                return new double[]{amount * 240.0,   U_ML};
            case "tbsp":  case "tablespoon": case "tablespoons":
                return new double[]{amount, U_TBSP};
            case "tsp":   case "teaspoon":   case "teaspoons":
                return new double[]{amount, U_TSP};
            default:
                return new double[]{amount, U_ORIG};
        }
    }

    private String unitLabel(int code) {
        switch (code) {
            case U_G:    return "g";
            case U_ML:   return "ml";
            case U_TBSP: return "tbsp";
            case U_TSP:  return "tsp";
            default:     return "";
        }
    }

    private String formatAmount(double amount) {
        if (amount <= 0) return "0";
        if (amount == Math.floor(amount) && !Double.isInfinite(amount)) {
            return String.valueOf((int) amount);
        }
        String formatted = String.format(Locale.US, "%.2f", amount);
        formatted = formatted.replaceAll("0+$", "").replaceAll("\\.$", "");
        return formatted;
    }

    private int     getIntSafe   (JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsInt() : -1;
    }
    private double  getDoubleSafe(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsDouble() : 0.0;
    }
    private boolean getBoolSafe  (JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() && o.get(k).getAsBoolean();
    }
    private String  getStringSafe(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
    }

    static class IngredientInfo {
        final String name;
        final double amount;
        final String unit;

        IngredientInfo(String name, double amount, String unit) {
            this.name   = name;
            this.amount = amount;
            this.unit   = unit;
        }
    }

    static class RecipeData {
        final int                  id;
        final String               name;
        final List<String>         ingredients;
        final List<IngredientInfo> ingredientDetails;
        final int                  readyInMinutes;
        final int                  servings;
        final boolean              vegan;
        final boolean              vegetarian;
        final boolean              glutenFree;
        final boolean              dairyFree;
        final List<String>         cuisines;
        final List<String>         dishTypes;
        final int                  healthScore;
        final String               rawText;
        final String               instructions;
        final String               calories;
        final String               carbs;
        final String               fat;
        final String               protein;

        RecipeData(int id,
                   String name,
                   List<String> ingredients,
                   List<IngredientInfo> ingredientDetails,
                   int readyInMinutes, int servings,
                   boolean vegan, boolean vegetarian,
                   boolean glutenFree, boolean dairyFree,
                   List<String> cuisines,
                   List<String> dishTypes,
                   int healthScore,
                   String rawText,
                   String instructions) {
            this(id, name, ingredients, ingredientDetails, readyInMinutes, servings,
                 vegan, vegetarian, glutenFree, dairyFree, cuisines, dishTypes,
                 healthScore, rawText, instructions, "", "", "", "");
        }

        RecipeData(int id,
                   String name,
                   List<String> ingredients,
                   List<IngredientInfo> ingredientDetails,
                   int readyInMinutes, int servings,
                   boolean vegan, boolean vegetarian,
                   boolean glutenFree, boolean dairyFree,
                   List<String> cuisines,
                   List<String> dishTypes,
                   int healthScore,
                   String rawText,
                   String instructions,
                   String calories, String carbs, String fat, String protein) {
            this.id                = id;
            this.name              = name;
            this.ingredients       = ingredients;
            this.ingredientDetails = ingredientDetails;
            this.readyInMinutes    = readyInMinutes;
            this.servings          = servings;
            this.vegan             = vegan;
            this.vegetarian        = vegetarian;
            this.glutenFree        = glutenFree;
            this.dairyFree         = dairyFree;
            this.cuisines          = cuisines;
            this.dishTypes         = dishTypes;
            this.healthScore       = healthScore;
            this.rawText           = rawText;
            this.instructions      = instructions;
            this.calories          = calories;
            this.carbs             = carbs;
            this.fat               = fat;
            this.protein           = protein;
        }

        boolean hasNutrition() {
            return !calories.isEmpty();
        }
    }
}
