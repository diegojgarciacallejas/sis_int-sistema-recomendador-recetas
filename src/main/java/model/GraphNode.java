package model;

import java.util.List;
import java.util.Locale;

public class GraphNode {

    private final String       recipeName;
    private final List<String> ingredients;
    private final List<String> matchedIngredients;
    private final List<String> missingIngredients;
    private final int          matches;
    private final int          totalIngredients;
    private final double       graphScore;
    private final double       coverageScore;
    private final double       utilizationScore;

    public GraphNode(String recipeName,
                     List<String> ingredients,
                     List<String> matchedIngredients,
                     List<String> missingIngredients,
                     int matches,
                     int totalIngredients,
                     double graphScore,
                     double coverageScore,
                     double utilizationScore) {
        this.recipeName          = recipeName;
        this.ingredients         = ingredients;
        this.matchedIngredients  = matchedIngredients;
        this.missingIngredients  = missingIngredients;
        this.matches             = matches;
        this.totalIngredients    = totalIngredients;
        this.graphScore          = graphScore;
        this.coverageScore       = coverageScore;
        this.utilizationScore    = utilizationScore;
    }

    public String       getRecipeName()         { return recipeName; }
    public List<String> getIngredients()         { return ingredients; }
    public List<String> getMatchedIngredients()  { return matchedIngredients; }
    public List<String> getMissingIngredients()  { return missingIngredients; }
    public int          getMatches()             { return matches; }
    public int          getTotalIngredients()    { return totalIngredients; }
    public double       getGraphScore()          { return graphScore; }
    public double       getCoverageScore()       { return coverageScore; }
    public double       getUtilizationScore()    { return utilizationScore; }

    public String toMessageFormat() {
        return recipeName
                + ";graphScore="        + String.format(Locale.US, "%.4f", graphScore)
                + ";coverageScore="     + String.format(Locale.US, "%.4f", coverageScore)
                + ";utilizationScore="  + String.format(Locale.US, "%.4f", utilizationScore)
                + ";matches="           + matches
                + ";total="             + totalIngredients
                + ";matched="           + String.join(",", matchedIngredients)
                + ";missing="           + String.join(",", missingIngredients);
    }
}
