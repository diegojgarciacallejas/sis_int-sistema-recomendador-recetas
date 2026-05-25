package behaviours.ontologybehaviours;
public class SubstitutionRule {
    private final String original;
    private final String substitute;
    private final double compatibilityScore;

    public SubstitutionRule(String original, String substitute, double compatibilityScore) {
        this.original = original;
        this.substitute = substitute;
        this.compatibilityScore = compatibilityScore;
    }

    public String getOriginal() { return original; }
    public String getSubstitute() { return substitute; }
    public double getCompatibilityScore() { return compatibilityScore; }
}