package model;

import java.util.Locale;

public class RecipeScore {

    private final String recipeName;
    private final double graphScore;
    private final double coverageScore;
    private final double utilizationScore;
    private final double tfidfScore;
    private final double finalScore;
    private final String instructions;
    private final String ingredientDetails;
    private final String tags;
    private final int    healthScore;

    public RecipeScore(String recipeName,
                       double graphScore,
                       double coverageScore,
                       double utilizationScore,
                       double tfidfScore,
                       double finalScore,
                       String instructions) {
        this(recipeName, graphScore, coverageScore, utilizationScore,
             tfidfScore, finalScore, instructions, "", "", -1);
    }

    public RecipeScore(String recipeName,
                       double graphScore,
                       double coverageScore,
                       double utilizationScore,
                       double tfidfScore,
                       double finalScore,
                       String instructions,
                       String ingredientDetails) {
        this(recipeName, graphScore, coverageScore, utilizationScore,
             tfidfScore, finalScore, instructions, ingredientDetails, "", -1);
    }

    public RecipeScore(String recipeName,
                       double graphScore,
                       double coverageScore,
                       double utilizationScore,
                       double tfidfScore,
                       double finalScore,
                       String instructions,
                       String ingredientDetails,
                       String tags,
                       int    healthScore) {
        this.recipeName        = recipeName;
        this.graphScore        = graphScore;
        this.coverageScore     = coverageScore;
        this.utilizationScore  = utilizationScore;
        this.tfidfScore        = tfidfScore;
        this.finalScore        = finalScore;
        this.instructions      = instructions      != null ? instructions      : "";
        this.ingredientDetails = ingredientDetails != null ? ingredientDetails : "";
        this.tags              = tags              != null ? tags              : "";
        this.healthScore       = healthScore;
    }

    public String getRecipeName()        { return recipeName; }
    public double getGraphScore()        { return graphScore; }
    public double getCoverageScore()     { return coverageScore; }
    public double getUtilizationScore()  { return utilizationScore; }
    public double getTfidfScore()        { return tfidfScore; }
    public double getFinalScore()        { return finalScore; }
    public String getInstructions()      { return instructions; }
    public String getIngredientDetails() { return ingredientDetails; }
    public String getTags()              { return tags; }
    public int    getHealthScore()       { return healthScore; }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%s | graph=%.2f | coverage=%.2f | utilization=%.2f | tfidf=%.4f | final=%.2f",
                recipeName, graphScore, coverageScore, utilizationScore, tfidfScore, finalScore);
    }
}
