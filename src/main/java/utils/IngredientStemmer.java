package utils;
/**
 * Stemmer compartido de ingredientes que normaliza formas plurales→singulares.
 * Para ingredientes compuestos, solo se aplica stemming al último token (sustantivo principal):
 *   "chicken breasts" → "chicken breast", "green peppers" → "green pepper"
 * matches() maneja relaciones genéricas↔específicas como "chicken" ↔ "chicken breast".
 */
public final class IngredientStemmer {

    private IngredientStemmer() {}

    public static String stem(String word) {
        if (word == null || word.isEmpty()) return "";
        word = word.trim().toLowerCase();

        if (!word.contains(" ")) {
            return stemSingle(word);
        }

        // Only the head noun (last word) carries grammatical number
        String[] parts = word.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(i == parts.length - 1 ? stemSingle(parts[i]) : parts[i]);
        }
        return sb.toString();
    }

    public static boolean matches(String ingA, String ingB) {
        if (ingA == null || ingB == null) return false;
        String a = stem(ingA.trim().toLowerCase());
        String b = stem(ingB.trim().toLowerCase());
        if (a.equals(b)) return true;
        if (b.startsWith(a + " ") || a.startsWith(b + " ")) return true; // "chicken" ↔ "chicken breast"
        if (b.endsWith(" " + a) || a.endsWith(" " + b)) return true;     // "pepper"  ↔ "bell pepper"
        return false;
    }

    private static String stemSingle(String word) {
        if (word == null || word.isEmpty()) return word;
        int len = word.length();

        // -ies → -y : berries→berry, cherries→cherry
        if (len > 3 && word.endsWith("ies"))
            return word.substring(0, len - 3) + "y";

        // -oes → -o : tomatoes→tomato, potatoes→potato
        if (len > 4 && word.endsWith("oes"))
            return word.substring(0, len - 2);

        // -ches → -ch : peaches→peach, lunches→lunch
        if (len > 4 && word.endsWith("ches"))
            return word.substring(0, len - 2);

        // -shes → -sh : dishes→dish
        if (len > 4 && word.endsWith("shes"))
            return word.substring(0, len - 2);

        // -xes → -x : mixes→mix
        if (len > 3 && word.endsWith("xes"))
            return word.substring(0, len - 2);

        // generic -s, but not -ss/-us/-is/-as (bass, asparagus, anís, peas)
        if (len > 3 && word.endsWith("s")
                && !word.endsWith("ss")
                && !word.endsWith("us")
                && !word.endsWith("is")
                && !word.endsWith("as"))
            return word.substring(0, len - 1);

        return word;
    }
}
