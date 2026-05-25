package agents;

import behaviours.ontologybehaviours.FoodOntology;
import behaviours.ontologybehaviours.SubstitutionRule;
import utils.IngredientStemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class OntologyProcessor {

    private static final List<String> BASICS = Arrays.asList(
            "salt", "pepper", "olive oil", "oil",
            "water", "sugar", "flour", "butter", "garlic", "onion",
            "vinegar", "baking soda", "baking powder"
    );

    public String process(String input) {
        String userIngredients = "";
        String recipesValue    = "";
        List<String> otherLines = new ArrayList<>();

        for (String line : input.split("\n")) {
            if (line.trim().isEmpty()) continue;
            if (line.startsWith("userIngredients=")) {
                userIngredients = line.replace("userIngredients=", "").trim();
            } else if (line.startsWith("recipes=")) {
                recipesValue = line.replace("recipes=", "").trim();
            } else {
                otherLines.add(line);
            }
        }

        List<String> userIngList = new ArrayList<>();
        for (String ing : userIngredients.split(",")) {
            String stemmed = stem(ing.trim().toLowerCase());
            if (!stemmed.isEmpty()) userIngList.add(stemmed);
        }

        for (String basic : BASICS) {
            String stemmed = stem(basic);
            if (!userIngList.contains(stemmed)) userIngList.add(stemmed);
        }

        System.out.println("OntologyProcessor: ingredientes usuario + básicos: " + userIngList);

        StringBuilder enrichedRecipes = new StringBuilder("recipes=");
        Map<String, List<String>> substitutionsMap = new LinkedHashMap<>();
        boolean firstRecipe = true;

        if (!recipesValue.isEmpty()) {
            for (String entry : recipesValue.split(";")) {
                String[] parts = entry.split(":", 2);
                if (parts.length != 2) continue;

                String   recipeName        = parts[0].trim();
                String[] recipeIngredients = parts[1].split(",");

                List<String> enrichedIngredients = new ArrayList<>();
                List<String> substitutionsForRecipe = new ArrayList<>();

                for (String ing : recipeIngredients) {
                    String normalized = ing.trim().toLowerCase();
                    if (normalized.isEmpty()) continue;

                    enrichedIngredients.add(normalized);

                    String stemmedNorm = stem(normalized);
                    if (!userIngList.contains(stemmedNorm)) {
                        List<SubstitutionRule> rules = FoodOntology.getSubstitutionRules(stemmedNorm);
                        SubstitutionRule bestRule = null;
                        for (SubstitutionRule rule : rules) {
                            String subStemmed = stem(rule.getSubstitute());
                            if (userIngList.contains(subStemmed)) {
                                if (bestRule == null || rule.getCompatibilityScore() > bestRule.getCompatibilityScore()) {
                                    bestRule = rule;
                                }
                            }
                        }

                        if (bestRule != null) {
                            String substitute = stem(bestRule.getSubstitute());
                            enrichedIngredients.add(substitute);
                            int scorePercent = (int)(bestRule.getCompatibilityScore() * 100);
                            substitutionsForRecipe.add(normalized + "→" + substitute + "(" + scorePercent + "%)");
                            System.out.println("OntologyAgent: '" + normalized
                                    + "' sustituido por '" + substitute
                                    + "' (compatibilidad " + scorePercent + "%)");
                        }
                    }
                }

                if (!firstRecipe) enrichedRecipes.append(";");
                firstRecipe = false;
                enrichedRecipes.append(recipeName).append(":")
                        .append(String.join(",", enrichedIngredients));

                if (!substitutionsForRecipe.isEmpty()) {
                    substitutionsMap.put(recipeName, substitutionsForRecipe);
                }
            }
        }

        StringBuilder substitutionsLine = new StringBuilder("recipeSubstitutions=");
        boolean firstSub = true;
        for (Map.Entry<String, List<String>> entry : substitutionsMap.entrySet()) {
            if (!firstSub) substitutionsLine.append(";");
            firstSub = false;
            substitutionsLine.append(entry.getKey()).append(":")
                    .append(String.join(",", entry.getValue()));
        }

        StringBuilder output = new StringBuilder();
        output.append("userIngredients=").append(userIngredients).append("\n");
        output.append(enrichedRecipes).append("\n");
        if (!substitutionsMap.isEmpty()) {
            output.append(substitutionsLine).append("\n");
        }
        for (String line : otherLines) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    static String stem(String word) {
        return IngredientStemmer.stem(word);
    }
}