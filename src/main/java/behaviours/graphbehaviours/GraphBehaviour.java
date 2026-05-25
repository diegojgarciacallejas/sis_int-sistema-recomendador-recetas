package behaviours.graphbehaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.GraphNode;
import utils.IngredientStemmer;

import java.util.*;

/**
 * Grafo bipartito ingrediente↔receta que calcula un coverageScore ponderado.
 *
 * coverageScore    = Σ ratio(ing) / totalIngredientesReceta
 *                    ratio(ing) = min(1.0, gramosUsuario / gramosReceta); 1.0 si no hay datos de gramos
 * utilizationScore = fracción de los ingredientes del usuario utilizados por la receta
 * graphScore       = 0.65 * coverageScore + 0.35 * utilizationScore
 */
public class GraphBehaviour extends CyclicBehaviour {

    // Ingredientes básicos de despensa asumidos como siempre disponibles en cantidad suficiente
    private static final List<String> BASICS = Arrays.asList(
            "salt", "black pepper", "pepper", "olive oil", "oil",
            "water", "sugar", "flour", "butter", "garlic", "onion",
            "vinegar", "baking soda", "baking powder"
    );

    private static final double BASICS_GRAMS = 10_000.0;

    public GraphBehaviour(Agent agent) {
        super(agent);
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId("ONTOLOGY_RESULT");
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            System.out.println("GraphAgent recibe:");
            System.out.println(msg.getContent());

            String fullInput = msg.getContent();

            List<String>              userIngredients      = new ArrayList<>();
            Map<String, List<String>> recipeIngredientsMap = new LinkedHashMap<>();
            String instructionsLine        = "";
            String tfidfLine               = "";
            String timesLine               = "";
            String userPrefsLine           = "";
            String recipeIngredientsLine   = "";
            String recipeTagsLine          = "";
            String healthScoresLine        = "";
            String userQuantitiesLine      = "";
            String recipeIdsLine           = "";
            String recipeServingsLine      = "";
            String recipeSubstitutionsLine = "";

            for (String line : fullInput.split("\n")) {
                if      (line.startsWith("userIngredients="))     parseUserIngredients(line, userIngredients);
                else if (line.startsWith("recipes="))             parseRecipesLine(line.substring("recipes=".length()), recipeIngredientsMap);
                else if (line.startsWith("recipeInstructions="))  instructionsLine        = line;
                else if (line.startsWith("recipeTfIdfScores="))   tfidfLine               = line;
                else if (line.startsWith("recipeTimes="))         timesLine               = line;
                else if (line.startsWith("userPrefs="))           userPrefsLine           = line;
                else if (line.startsWith("recipeIngredients="))   recipeIngredientsLine   = line;
                else if (line.startsWith("recipeTags="))          recipeTagsLine          = line;
                else if (line.startsWith("recipeHealthScores="))  healthScoresLine        = line;
                else if (line.startsWith("userQuantities="))      userQuantitiesLine      = line;
                else if (line.startsWith("recipeIds="))           recipeIdsLine           = line;
                else if (line.startsWith("recipeServings="))      recipeServingsLine      = line;
                else if (line.startsWith("recipeSubstitutions=")) recipeSubstitutionsLine = line;
            }

            Map<String, Double> userGrams = parseUserQuantities(userQuantitiesLine);
            for (String basic : BASICS) {
                String stemmed = IngredientStemmer.stem(basic);
                if (!userIngredients.contains(stemmed)) {
                    userIngredients.add(stemmed);
                }
                userGrams.putIfAbsent(stemmed, BASICS_GRAMS);
            }

            Map<String, Map<String, Double>> recipeGrams    = parseRecipeGrams(recipeIngredientsLine);
            Map<String, Integer>             recipeServings = parseRecipeServings(recipeServingsLine);
            int                              persons        = parsePersons(userPrefsLine);

            System.out.println("GraphAgent: user ingredients = " + userIngredients);
            System.out.println("GraphAgent: user grams = " + userGrams);
            System.out.println("GraphAgent: persons = " + persons);

            List<GraphNode> nodes = buildGraph(
                    userIngredients, recipeIngredientsMap, userGrams, recipeGrams,
                    recipeServings, persons);
            logGraph(userIngredients, nodes);

            String result = buildOutputMessage(
                    nodes, instructionsLine, tfidfLine, timesLine, userPrefsLine,
                    recipeIngredientsLine, recipeTagsLine, healthScoresLine,
                    userQuantitiesLine, recipeIdsLine, recipeSubstitutionsLine);

            ACLMessage forward = new ACLMessage(ACLMessage.INFORM);
            forward.addReceiver(new AID("RecommendationAgent", AID.ISLOCALNAME));
            forward.setConversationId("GRAPH_RESULT");
            forward.setContent(result);
            myAgent.send(forward);

            System.out.println("GraphAgent envía a RecommendationAgent:");
            System.out.println(result);

            Iterator<AID> replyToIt = msg.getAllReplyTo();
            if (replyToIt.hasNext()) {
                AID replyTo = replyToIt.next();
                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                reply.addReceiver(replyTo);
                reply.setConversationId("GRAPH_RESULT");
                reply.setContent(result);
                myAgent.send(reply);
                System.out.println("GraphAgent -> reply-to: " + replyTo.getLocalName());
            }

        } else {
            block();
        }
    }

    private void parseUserIngredients(String line, List<String> out) {
        for (String raw : line.substring("userIngredients=".length()).split(",")) {
            String s = IngredientStemmer.stem(raw.trim().toLowerCase());
            if (!s.isEmpty()) out.add(s);
        }
    }

    private Map<String, Double> parseUserQuantities(String line) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (line.isEmpty()) return map;
        String content = line.startsWith("userQuantities=")
                ? line.substring("userQuantities=".length()) : line;
        for (String entry : content.split(",")) {
            String[] kv = entry.split(":");
            if (kv.length != 2) continue;
            String name = IngredientStemmer.stem(kv[0].trim().toLowerCase());
            try {
                double grams = Double.parseDouble(kv[1].trim());
                if (!name.isEmpty() && grams > 0) map.put(name, grams);
            } catch (NumberFormatException ignored) {}
        }
        return map;
    }

    private Map<String, Map<String, Double>> parseRecipeGrams(String recipeIngredientsLine) {
        Map<String, Map<String, Double>> result = new LinkedHashMap<>();
        if (recipeIngredientsLine.isEmpty()) return result;
        String content = recipeIngredientsLine.substring("recipeIngredients=".length());
        for (String recipeEntry : content.split(";")) {
            String[] parts = recipeEntry.split(":", 2);
            if (parts.length != 2) continue;
            String recipeName = parts[0].trim();
            Map<String, Double> ingGrams = new LinkedHashMap<>();
            for (String ingEntry : parts[1].split(",")) {
                String[] fields = ingEntry.split("\\|");
                if (fields.length < 3) continue;
                String name = IngredientStemmer.stem(fields[0].trim().toLowerCase());
                try {
                    double amount = Double.parseDouble(fields[1].trim());
                    String unit   = fields[2].trim().toLowerCase();
                    double grams  = toComparableGrams(amount, unit);
                    if (!name.isEmpty() && grams > 0) ingGrams.put(name, grams);
                } catch (NumberFormatException ignored) {}
            }
            result.put(recipeName, ingGrams);
        }
        return result;
    }

    /** Devuelve 0.0 para unidades no convertibles (dientes, piezas, etc.) — tratadas como coincidencia binaria. */
    private double toComparableGrams(double amount, String unit) {
        switch (unit) {
            case "g":    case "ml":  return amount;
            case "tbsp":             return amount * 15.0;
            case "tsp":              return amount * 5.0;
            default:                 return 0.0;
        }
    }

    private void parseRecipesLine(String text, Map<String, List<String>> out) {
        for (String entry : text.split(";")) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2) continue;
            String name = parts[0].trim();
            List<String> ings = new ArrayList<>();
            for (String raw : parts[1].split(",")) {
                String s = IngredientStemmer.stem(raw.trim().toLowerCase());
                if (!s.isEmpty()) ings.add(s);
            }
            out.put(name, ings);
        }
    }

    private List<GraphNode> buildGraph(List<String> userIngredients,
                                       Map<String, List<String>> recipeIngredientsMap,
                                       Map<String, Double> userGrams,
                                       Map<String, Map<String, Double>> recipeGrams,
                                       Map<String, Integer> recipeServings,
                                       int persons) {
        int totalUser = Math.max(1, userIngredients.size());
        List<GraphNode> nodes = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : recipeIngredientsMap.entrySet()) {
            String       recipeName   = entry.getKey();
            List<String> recipeIngs   = entry.getValue();
            int          totalRecipe  = Math.max(1, recipeIngs.size());

            Map<String, Double> recipeGramsForRecipe = recipeGrams.getOrDefault(recipeName, Collections.emptyMap());

            // Escala las cantidades de la receta según la proporción personas/raciones para que la cobertura refleje la
            // necesidad real
            int servings = recipeServings.getOrDefault(recipeName, -1);
            if (persons > 0 && servings > 0 && !recipeGramsForRecipe.isEmpty()) {
                double scale = (double) persons / servings;
                Map<String, Double> scaledGrams = new LinkedHashMap<>();
                for (Map.Entry<String, Double> e : recipeGramsForRecipe.entrySet()) {
                    scaledGrams.put(e.getKey(), e.getValue() * scale);
                }
                recipeGramsForRecipe = scaledGrams;
                System.out.printf("GraphAgent: escala %.2f (personas=%d / raciones=%d) para '%s'%n",
                        scale, persons, servings, recipeName);
            }

            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();
            double weightedMatch = 0.0;

            for (String recipeIng : recipeIngs) {
                // Buscar si el usuario tiene este ingrediente (con stemming y matching parcial)
                String matchedUserIng = userIngredients.stream()
                        .filter(u -> IngredientStemmer.matches(u, recipeIng))
                        .findFirst()
                        .orElse(null);

                if (matchedUserIng != null) {
                    matched.add(recipeIng);
                    double ratio = computeQuantityRatio(
                            matchedUserIng, recipeIng, userGrams, recipeGramsForRecipe);
                    weightedMatch += ratio;

                    System.out.printf("  %-25s ↔ %-25s ratio=%.2f%n",
                            matchedUserIng, recipeIng, ratio);
                } else {
                    missing.add(recipeIng);
                }
            }

            double coverageScore    = weightedMatch / totalRecipe;
            long utilized = userIngredients.stream()
                    .filter(u -> recipeIngs.stream().anyMatch(r -> IngredientStemmer.matches(u, r)))
                    .count();
            double utilizationScore = (double) utilized / totalUser;

            double graphScore = 0.65 * coverageScore + 0.35 * utilizationScore;

            nodes.add(new GraphNode(
                    recipeName, recipeIngs, matched, missing,
                    matched.size(), recipeIngs.size(),
                    graphScore, coverageScore, utilizationScore
            ));
        }

        return nodes;
    }

    /**
     * ratio = min(1.0, gramosUsuario / gramosReceta); devuelve 1.0 si faltan datos de gramos.
     */
    private double computeQuantityRatio(String userIng, String recipeIng,
                                        Map<String, Double> userGrams,
                                        Map<String, Double> recipeGramsForRecipe) {
        Double uGrams = userGrams.get(userIng);
        if (uGrams == null) {
            uGrams = userGrams.entrySet().stream()
                    .filter(e -> IngredientStemmer.matches(e.getKey(), userIng))
                    .mapToDouble(Map.Entry::getValue)
                    .findFirst()
                    .orElse(-1.0);
        }
        Double rGrams = recipeGramsForRecipe.get(recipeIng);
        if (rGrams == null) {
            rGrams = recipeGramsForRecipe.entrySet().stream()
                    .filter(e -> IngredientStemmer.matches(e.getKey(), recipeIng))
                    .mapToDouble(Map.Entry::getValue)
                    .findFirst()
                    .orElse(-1.0);
        }
        if (uGrams <= 0 || rGrams <= 0) return 1.0;
        return Math.min(1.0, uGrams / rGrams);
    }

    private String buildOutputMessage(List<GraphNode> nodes,
                                      String instructionsLine,
                                      String tfidfLine,
                                      String timesLine,
                                      String userPrefsLine,
                                      String recipeIngredientsLine,
                                      String recipeTagsLine,
                                      String healthScoresLine,
                                      String userQuantitiesLine,
                                      String recipeIdsLine,
                                      String recipeSubstitutionsLine) {
        StringBuilder sb = new StringBuilder("graphResults=\n");
        for (GraphNode node : nodes) {
            sb.append(node.toMessageFormat()).append("\n");
        }
        if (!instructionsLine.isEmpty())        sb.append(instructionsLine).append("\n");
        if (!tfidfLine.isEmpty())               sb.append(tfidfLine).append("\n");
        if (!timesLine.isEmpty())               sb.append(timesLine).append("\n");
        if (!userPrefsLine.isEmpty())           sb.append(userPrefsLine).append("\n");
        if (!recipeIngredientsLine.isEmpty())   sb.append(recipeIngredientsLine).append("\n");
        if (!recipeTagsLine.isEmpty())          sb.append(recipeTagsLine).append("\n");
        if (!healthScoresLine.isEmpty())        sb.append(healthScoresLine).append("\n");
        if (!userQuantitiesLine.isEmpty())      sb.append(userQuantitiesLine).append("\n");
        if (!recipeIdsLine.isEmpty())           sb.append(recipeIdsLine).append("\n");
        if (!recipeSubstitutionsLine.isEmpty()) sb.append(recipeSubstitutionsLine).append("\n");
        return sb.toString();
    }

    private int parsePersons(String userPrefsLine) {
        if (userPrefsLine.isEmpty()) return -1;
        String content = userPrefsLine.startsWith("userPrefs=")
                ? userPrefsLine.substring("userPrefs=".length()) : userPrefsLine;
        for (String part : content.split(";")) {
            if (part.startsWith("persons:")) {
                try { return Integer.parseInt(part.substring("persons:".length()).trim()); }
                catch (NumberFormatException ignored) {}
            }
        }
        return -1;
    }

    private Map<String, Integer> parseRecipeServings(String line) {
        Map<String, Integer> map = new LinkedHashMap<>();
        if (line.isEmpty()) return map;
        String content = line.startsWith("recipeServings=")
                ? line.substring("recipeServings=".length()) : line;
        for (String entry : content.split(";")) {
            String[] kv = entry.split(":");
            if (kv.length == 2) {
                try { map.put(kv[0].trim(), Integer.parseInt(kv[1].trim())); }
                catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    private void logGraph(List<String> userIngredients, List<GraphNode> nodes) {
        System.out.println("GraphAgent: ingredientes usuario (stemmizados) = " + userIngredients);
        for (GraphNode n : nodes) {
            System.out.printf("  %-35s  coverage=%.2f  utilization=%.2f  graph=%.2f%n",
                    n.getRecipeName(),
                    n.getCoverageScore(),
                    n.getUtilizationScore(),
                    n.getGraphScore());
        }
    }
}