package behaviours.recommendationbehaviours;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.RecipeScore;
import utils.RestrictionChecker;

import java.util.*;

public class RecommendationBehaviour extends CyclicBehaviour {

    // Los valores de coseno TF-IDF suelen ser pequeños;
    // los escalamos al rango [0,1]
    private static final double TFIDF_SCALE   = 5.0;
    // Penalización por tiempo máximo: representa el 30% de la puntuación final
    private static final double MAX_TIME_PEN  = 0.30;

    public RecommendationBehaviour(Agent agent) {
        super(agent);
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId("GRAPH_RESULT");
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            System.out.println("RecommendationAgent recibió:");
            System.out.println(msg.getContent());

            String rawInput = msg.getContent();
            List<RecipeScore>    ranking    = calculateRanking(rawInput);
            Map<String, Integer> recipeIds  = parseRecipeIds(rawInput);
            Map<String, String>  substitutions = parseSubstitutions(rawInput);

            Map<String, String> nutritionMap = collectNutrition(ranking, recipeIds);

            String output = buildRankingMessage(ranking, nutritionMap, substitutions);

            System.out.println("Ranking final:");
            System.out.println(output);

            sendToInterface(output);

            Iterator<AID> replyToIt = msg.getAllReplyTo();
            if (replyToIt.hasNext()) {
                AID replyTo = replyToIt.next();
                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                reply.addReceiver(replyTo);
                reply.setConversationId("RECOMMENDATION_RESULT");
                reply.setContent(output);
                myAgent.send(reply);
                System.out.println("RecommendationAgent -> reply-to: " + replyTo.getLocalName());
            }
        } else {
            block();
        }
    }

    private List<RecipeScore> calculateRanking(String input) {
        Map<String, Double>  tfidfScores       = parseTfidfScores(input);
        Map<String, String>  instructions      = parseInstructions(input);
        Map<String, Integer> recipeTimes       = parseRecipeTimes(input);
        Map<String, String>  recipeIngredients = parseRecipeIngredientDetails(input);
        Map<String, String>  recipeTags        = parseSimpleStringMap(input, "recipeTags=");
        Map<String, Integer> healthScores      = parseSimpleIntMap(input, "recipeHealthScores=");

        int         maxTime      = parseMaxTime(input);
        String      restrictions = parseRestrictions(input);
        Set<String> preferences  = parsePreferences(input);

        List<RecipeScore> results = new ArrayList<>();

        for (String line : input.split("\n")) {
            if (line.startsWith("graphResults=")
                    || line.startsWith("recipeInstructions=")
                    || line.startsWith("recipeTfIdfScores=")
                    || line.startsWith("recipeTimes=")
                    || line.startsWith("userPrefs=")
                    || line.startsWith("recipeIngredients=")
                    || line.startsWith("recipeTags=")
                    || line.startsWith("recipeHealthScores=")
                    || line.startsWith("recipeServings=")
                    || line.startsWith("recipeCuisines=")
                    || line.startsWith("recipeDishTypes=")
                    || line.startsWith("recipeIds=")
                    || line.startsWith("userQuantities=")
                    || line.startsWith("recipeSubstitutions=")
                    || line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(";");
            if (parts.length < 3) continue;

            String recipeName       = parts[0].trim();
            double graphScore       = parseField(parts, "graphScore");
            double coverageScore    = parseField(parts, "coverageScore");
            double utilizationScore = parseField(parts, "utilizationScore");

            double rawTfidf  = tfidfScores.getOrDefault(recipeName, 0.0);
            double tfidfNorm = Math.min(1.0, rawTfidf * TFIDF_SCALE);

            double finalScore = 0.50 * coverageScore
                    + 0.20 * utilizationScore
                    + 0.30 * tfidfNorm;

            double timePenaltyCoeff = preferences.contains("quick") ? 0.25 : 0.15;
            if (maxTime > 0) {
                int recipeTime = recipeTimes.getOrDefault(recipeName, -1);
                if (recipeTime > 0 && recipeTime > maxTime) {
                    double overRatio = (double)(recipeTime - maxTime) / maxTime;
                    double penalty   = Math.min(MAX_TIME_PEN, overRatio * timePenaltyCoeff);
                    finalScore -= penalty;
                    System.out.printf("RecommendationAgent: penalización tiempo -%s%% en '%s' "
                                    + "(%d min > límite %d min)%n",
                            String.format(Locale.US, "%.0f", penalty * 100),
                            recipeName, recipeTime, maxTime);
                }
            }

            finalScore = Math.max(0.0, finalScore);

            int healthVal = healthScores.getOrDefault(recipeName, -1);

            if (preferences.contains("healthy") && healthVal >= 60) {
                finalScore = Math.min(1.0, finalScore + 0.05);
                System.out.println("RecommendationAgent: bonus 'healthy' para '" + recipeName + "'");
            }

            if (preferences.contains("light") && healthVal >= 70) {
                finalScore = Math.min(1.0, finalScore + 0.05);
                System.out.println("RecommendationAgent: bonus 'light' para '" + recipeName + "'");
            }

            String instrText = instructions.getOrDefault(recipeName, "").toLowerCase();
            if (preferences.contains("no oven")
                    && (instrText.contains("oven") || instrText.contains("bake")
                    || instrText.contains("baked") || instrText.contains("roast"))) {
                finalScore = Math.max(0.0, finalScore - 0.20);
                System.out.println("RecommendationAgent: penalización 'no oven' para '" + recipeName + "'");
            }

            if (!restrictions.isEmpty()) {
                List<String> allIngredients = parseRecipeIngredients(parts);
                if (!RestrictionChecker.isCompatible(restrictions, allIngredients)) {
                    finalScore = 0.0;
                    System.out.println("RecommendationAgent: '"
                            + recipeName + "' incompatible con '" + restrictions + "'");
                }
            }

            results.add(new RecipeScore(
                    recipeName,
                    graphScore,
                    coverageScore,
                    utilizationScore,
                    rawTfidf,
                    finalScore,
                    instructions.getOrDefault(recipeName, ""),
                    recipeIngredients.getOrDefault(recipeName, ""),
                    recipeTags.getOrDefault(recipeName, ""),
                    healthScores.getOrDefault(recipeName, -1)
            ));
        }

        results.sort(Comparator.comparingDouble(RecipeScore::getFinalScore).reversed());
        return results;
    }

    private String buildRankingMessage(List<RecipeScore> ranking,
                                       Map<String, String> nutritionMap,
                                       Map<String, String> substitutions) {
        StringBuilder sb = new StringBuilder();

        int pos = 1;
        for (RecipeScore r : ranking) {
            String nutritionShort = nutritionMap.getOrDefault(r.getRecipeName(), "");
            sb.append(pos)
                    .append("|").append(r.getRecipeName())
                    .append("|").append(String.format(Locale.US, "%.4f", r.getGraphScore()))
                    .append("|").append(String.format(Locale.US, "%.4f", r.getFinalScore()))
                    .append("|").append(nutritionShort.replace("|", "/"))
                    .append("\n");

            sb.append("  Coverage: ")
                    .append(String.format(Locale.US, "%.0f%%", r.getCoverageScore() * 100))
                    .append(" of recipe ingredients covered | You contribute: ")
                    .append(String.format(Locale.US, "%.0f%%", r.getUtilizationScore() * 100))
                    .append(" of your ingredients")
                    .append("\n");

            String tags   = r.getTags();
            int    health = r.getHealthScore();
            if ((tags != null && !tags.isBlank()) || health >= 0) {
                sb.append("  ");
                if (tags != null && !tags.isBlank()) {
                    for (String tag : tags.split(",")) {
                        switch (tag.trim()) {
                            case "vegan":       sb.append("🌱 Vegan  ");        break;
                            case "vegetarian":  sb.append("🥦 Vegetarian  ");   break;
                            case "glutenFree":  sb.append("🌾 Gluten-free  ");  break;
                            case "dairyFree":   sb.append("🥛 Dairy-free  ");   break;
                        }
                    }
                }
                if (health >= 0) sb.append("❤ Health: ").append(health).append("/100");
                sb.append("\n");
            }

            String nutritionDetail = nutritionMap.getOrDefault(r.getRecipeName(), null);
            if (nutritionDetail != null && !nutritionDetail.isBlank()) {
                sb.append("  🍎 Nutrition: ").append(nutritionDetail).append("\n");
            }

            sb.append("  ").append(buildExplanation(r)).append("\n");

            String subs = substitutions.getOrDefault(r.getRecipeName(), "");
            if (!subs.isBlank()) {
                sb.append("  ⚗ Substitutions applied: ");
                for (String sub : subs.split(",")) {
                    sb.append(sub.trim()).append("  ");
                }
                sb.append("\n");
            }

            String ingDetails = r.getIngredientDetails();
            if (ingDetails != null && !ingDetails.isBlank()) {
                sb.append("  Ingredients:\n");
                for (String entry : ingDetails.split(",")) {
                    String[] parts = entry.split("\\|");
                    if (parts.length >= 3) {
                        String ingName = parts[0].trim();
                        String amount  = parts[1].trim();
                        String unit    = parts[2].trim();
                        String display = unit.isEmpty() ? amount : amount + " " + unit;
                        sb.append("    • ").append(ingName).append(": ").append(display).append("\n");
                    } else if (parts.length == 1 && !parts[0].trim().isEmpty()) {
                        sb.append("    • ").append(parts[0].trim()).append("\n");
                    }
                }
            }

            String steps = r.getInstructions();
            if (steps != null && !steps.isBlank()) {
                sb.append("  Preparation: ").append(steps).append("\n");
            }

            sb.append("\n");
            pos++;
        }

        return sb.toString();
    }

    private String buildExplanation(RecipeScore r) {
        if (r.getFinalScore() == 0.0)
            return "⚠ Not recommended: incompatible with your dietary restrictions.";
        if (r.getCoverageScore() >= 0.65)
            return "✓ Great match: you have over 65% of the required ingredients.";
        if (r.getCoverageScore() >= 0.30)
            return "~ Acceptable match: you share some key ingredients.";
        return "✗ Weak match: several ingredients from this recipe are missing.";
    }

    private void sendToInterface(String rankingMessage) {
        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.addReceiver(new AID("InterfaceAgent", AID.ISLOCALNAME));
        response.setConversationId("RECOMMENDATION_RESULT");
        response.setContent(rankingMessage);
        myAgent.send(response);
        System.out.println("RecommendationAgent: ranking enviado a InterfaceAgent.");
    }

    private Map<String, Double> parseTfidfScores(String input) {
        Map<String, Double> scores = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("recipeTfIdfScores=")) continue;
            for (String entry : line.substring("recipeTfIdfScores=".length()).split(";")) {
                String[] kv = entry.split(":");
                if (kv.length == 2) {
                    try { scores.put(kv[0].trim(),
                            Double.parseDouble(kv[1].trim().replace(",", "."))); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return scores;
    }

    private Map<String, String> parseInstructions(String input) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("recipeInstructions=")) continue;
            for (String entry : line.substring("recipeInstructions=".length()).split(";")) {
                int colon = entry.indexOf(":");
                if (colon > 0) {
                    map.put(entry.substring(0, colon).trim(),
                            entry.substring(colon + 1).trim());
                }
            }
        }
        return map;
    }

    private Map<String, String> parseRecipeIngredientDetails(String input) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("recipeIngredients=")) continue;
            for (String entry : line.substring("recipeIngredients=".length()).split(";")) {
                int colon = entry.indexOf(":");
                if (colon > 0) {
                    map.put(entry.substring(0, colon).trim(),
                            entry.substring(colon + 1).trim());
                }
            }
        }
        return map;
    }

    private Map<String, Integer> parseRecipeTimes(String input) {
        Map<String, Integer> times = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("recipeTimes=")) continue;
            for (String entry : line.substring("recipeTimes=".length()).split(";")) {
                String[] kv = entry.split(":");
                if (kv.length == 2) {
                    try { times.put(kv[0].trim(), Integer.parseInt(kv[1].trim())); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return times;
    }

    private int parseMaxTime(String input) {
        for (String line : input.split("\n")) {
            if (!line.startsWith("userPrefs=")) continue;
            for (String part : line.substring("userPrefs=".length()).split(";")) {
                if (part.startsWith("maxTime:")) {
                    try { return Integer.parseInt(part.substring("maxTime:".length()).trim()); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return -1;
    }

    private String parseRestrictions(String input) {
        for (String line : input.split("\n")) {
            if (!line.startsWith("userPrefs=")) continue;
            for (String part : line.substring("userPrefs=".length()).split(";")) {
                if (part.startsWith("restrictions:")) {
                    return part.substring("restrictions:".length()).trim();
                }
            }
        }
        return "";
    }

    private List<String> parseRecipeIngredients(String[] parts) {
        List<String> ings = new ArrayList<>();
        for (String part : parts) {
            if (part.startsWith("matched=") || part.startsWith("missing=")) {
                String val = part.substring(part.indexOf('=') + 1).trim();
                if (!val.isEmpty()) {
                    for (String i : val.split(",")) {
                        String s = i.trim();
                        if (!s.isEmpty()) ings.add(s);
                    }
                }
            }
        }
        return ings;
    }

    private Map<String, Integer> parseRecipeIds(String input) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("recipeIds=")) continue;
            for (String entry : line.substring("recipeIds=".length()).split(";")) {
                int colon = entry.indexOf(":");
                if (colon > 0) {
                    try {
                        map.put(entry.substring(0, colon).trim(),
                                Integer.parseInt(entry.substring(colon + 1).trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return map;
    }

    private Set<String> parsePreferences(String input) {
        Set<String> prefs = new HashSet<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("userPrefs=")) continue;
            for (String part : line.substring("userPrefs=".length()).split(";")) {
                if (part.startsWith("preferences:")) {
                    String val = part.substring("preferences:".length()).trim();
                    for (String p : val.split(",")) {
                        String trimmed = p.trim().toLowerCase();
                        if (!trimmed.isEmpty()) prefs.add(trimmed);
                    }
                }
            }
        }
        return prefs;
    }

    private Map<String, String> parseSubstitutions(String input) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith("recipeSubstitutions=")) continue;
            for (String entry : line.substring("recipeSubstitutions=".length()).split(";")) {
                int colon = entry.indexOf(":");
                if (colon > 0) {
                    map.put(entry.substring(0, colon).trim(),
                            entry.substring(colon + 1).trim());
                }
            }
        }
        return map;
    }

    private Map<String, String> collectNutrition(List<RecipeScore> ranking,
                                                 Map<String, Integer> recipeIds) {
        Map<String, String> nutritionMap = new LinkedHashMap<>();
        if (recipeIds.isEmpty()) return nutritionMap;

        Gson gson = new Gson();

        for (RecipeScore recipe : ranking) {
            String  name = recipe.getRecipeName();
            Integer id   = recipeIds.get(name);
            if (id == null || id <= 0) continue;

            String replyWith = "nutr_" + System.nanoTime();

            ACLMessage req = new ACLMessage(ACLMessage.INFORM);
            req.addReceiver(new AID("NutritionAgent", AID.ISLOCALNAME));
            req.setConversationId("NUTRITION_RESULT_REQUEST");
            req.setReplyWith(replyWith);
            JsonObject body = new JsonObject();
            body.addProperty("id",   id);
            body.addProperty("name", name);
            req.setContent(body.toString());
            myAgent.send(req);

            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchConversationId("NUTRITION_RESULT"),
                    MessageTemplate.MatchInReplyTo(replyWith)
            );
            ACLMessage resp = myAgent.blockingReceive(mt, 5000);

            if (resp != null && resp.getPerformative() == ACLMessage.INFORM) {
                try {
                    JsonObject data    = gson.fromJson(resp.getContent(), JsonObject.class);
                    String     cal     = safeNutrField(data, "calories");
                    String     carbs   = safeNutrField(data, "carbs");
                    String     fat     = safeNutrField(data, "fat");
                    String     protein = safeNutrField(data, "protein");
                    String     info    = cal + " cal  •  " + carbs + " carbs  •  "
                            + fat + " fat  •  " + protein + " protein";
                    nutritionMap.put(name, info);
                    System.out.println("RecommendationAgent: nutrición para '" + name + "': " + info);
                } catch (Exception e) {
                    System.err.println("RecommendationAgent: error parseando nutrición para '"
                            + name + "': " + e.getMessage());
                }
            } else {
                System.out.println("RecommendationAgent: sin datos de nutrición para '"
                        + name + "' (timeout o fallo)");
            }
        }
        return nutritionMap;
    }

    private String safeNutrField(JsonObject obj, String key) {
        return (obj.has(key) && !obj.get(key).isJsonNull())
                ? obj.get(key).getAsString()
                : "?";
    }

    private Map<String, String> parseSimpleStringMap(String input, String prefix) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith(prefix)) continue;
            for (String entry : line.substring(prefix.length()).split(";")) {
                int colon = entry.indexOf(":");
                if (colon > 0)
                    map.put(entry.substring(0, colon).trim(), entry.substring(colon + 1).trim());
            }
        }
        return map;
    }

    private Map<String, Integer> parseSimpleIntMap(String input, String prefix) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String line : input.split("\n")) {
            if (!line.startsWith(prefix)) continue;
            for (String entry : line.substring(prefix.length()).split(";")) {
                int colon = entry.indexOf(":");
                if (colon > 0) {
                    try { map.put(entry.substring(0, colon).trim(),
                            Integer.parseInt(entry.substring(colon + 1).trim())); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return map;
    }

    private double parseField(String[] parts, String fieldName) {
        String prefix = fieldName + "=";
        for (String part : parts) {
            if (part.startsWith(prefix)) {
                try { return Double.parseDouble(
                        part.substring(prefix.length()).replace(",", ".")); }
                catch (NumberFormatException ignored) {}
            }
        }
        return 0.0;
    }
}