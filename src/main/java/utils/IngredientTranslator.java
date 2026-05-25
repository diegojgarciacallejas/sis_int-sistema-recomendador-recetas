package utils;

import java.util.HashMap;
import java.util.Map;

/** Translates Spanish ingredient names to English for Spoonacular queries. */
public class IngredientTranslator {

    // Keys are lowercase with diacritics stripped for robust lookup
    private static final Map<String, String> ES_TO_EN = new HashMap<>();

    static {
        // Proteínas
        ES_TO_EN.put("pollo",            "chicken");
        ES_TO_EN.put("pechuga de pollo", "chicken breast");
        ES_TO_EN.put("muslo de pollo",   "chicken thigh");
        ES_TO_EN.put("ternera",          "beef");
        ES_TO_EN.put("carne de ternera", "beef");
        ES_TO_EN.put("carne picada",     "ground beef");
        ES_TO_EN.put("cerdo",            "pork");
        ES_TO_EN.put("lomo de cerdo",    "pork loin");
        ES_TO_EN.put("cordero",          "lamb");
        ES_TO_EN.put("pavo",             "turkey");
        ES_TO_EN.put("atun",             "tuna");
        ES_TO_EN.put("salmon",           "salmon");
        ES_TO_EN.put("bacalao",          "cod");
        ES_TO_EN.put("merluza",          "hake");
        ES_TO_EN.put("sardinas",         "sardines");
        ES_TO_EN.put("sardina",          "sardine");
        ES_TO_EN.put("anchoas",          "anchovies");
        ES_TO_EN.put("anchoa",           "anchovy");
        ES_TO_EN.put("gambas",           "shrimp");
        ES_TO_EN.put("camaron",          "shrimp");
        ES_TO_EN.put("camarones",        "shrimp");
        ES_TO_EN.put("mejillones",       "mussels");
        ES_TO_EN.put("mejillon",         "mussel");
        ES_TO_EN.put("almejas",          "clams");
        ES_TO_EN.put("almeja",           "clam");
        ES_TO_EN.put("calamar",          "squid");
        ES_TO_EN.put("calamares",        "squid");
        ES_TO_EN.put("pulpo",            "octopus");
        ES_TO_EN.put("tofu",             "tofu");
        ES_TO_EN.put("huevo",            "egg");
        ES_TO_EN.put("huevos",           "eggs");
        ES_TO_EN.put("jamon",            "ham");
        ES_TO_EN.put("bacon",            "bacon");
        ES_TO_EN.put("tocino",           "bacon");
        ES_TO_EN.put("chorizo",          "chorizo");
        ES_TO_EN.put("salchicha",        "sausage");
        ES_TO_EN.put("salchichas",       "sausages");

        // Carbohidratos
        ES_TO_EN.put("arroz",            "rice");
        ES_TO_EN.put("pasta",            "pasta");
        ES_TO_EN.put("macarrones",       "macaroni");
        ES_TO_EN.put("espaguetis",       "spaghetti");
        ES_TO_EN.put("fideos",           "noodles");
        ES_TO_EN.put("pan",              "bread");
        ES_TO_EN.put("pan rallado",      "breadcrumbs");
        ES_TO_EN.put("harina",           "flour");
        ES_TO_EN.put("avena",            "oats");
        ES_TO_EN.put("patata",           "potato");
        ES_TO_EN.put("patatas",          "potatoes");
        ES_TO_EN.put("papa",             "potato");
        ES_TO_EN.put("papas",            "potatoes");
        ES_TO_EN.put("batata",           "sweet potato");
        ES_TO_EN.put("boniato",          "sweet potato");
        ES_TO_EN.put("quinoa",           "quinoa");
        ES_TO_EN.put("cuscus",           "couscous");

        // Lácteos
        ES_TO_EN.put("leche",            "milk");
        ES_TO_EN.put("queso",            "cheese");
        ES_TO_EN.put("mantequilla",      "butter");
        ES_TO_EN.put("nata",             "cream");
        ES_TO_EN.put("crema",            "cream");
        ES_TO_EN.put("yogur",            "yogurt");
        ES_TO_EN.put("yogurt",           "yogurt");
        ES_TO_EN.put("mozzarella",       "mozzarella");
        ES_TO_EN.put("parmesano",        "parmesan");
        ES_TO_EN.put("queso parmesano",  "parmesan");
        ES_TO_EN.put("queso feta",       "feta cheese");
        ES_TO_EN.put("queso fresco",     "fresh cheese");
        ES_TO_EN.put("nata agria",       "sour cream");

        // Verduras
        ES_TO_EN.put("tomate",           "tomato");
        ES_TO_EN.put("tomates",          "tomatoes");
        ES_TO_EN.put("cebolla",          "onion");
        ES_TO_EN.put("cebollas",         "onions");
        ES_TO_EN.put("cebolleta",        "scallion");
        ES_TO_EN.put("ajo",              "garlic");
        ES_TO_EN.put("ajos",             "garlic");
        ES_TO_EN.put("pimiento",         "pepper");
        ES_TO_EN.put("pimientos",        "peppers");
        ES_TO_EN.put("pimiento rojo",    "red pepper");
        ES_TO_EN.put("pimiento verde",   "green pepper");
        ES_TO_EN.put("espinacas",        "spinach");
        ES_TO_EN.put("espinaca",         "spinach");
        ES_TO_EN.put("zanahoria",        "carrot");
        ES_TO_EN.put("zanahorias",       "carrots");
        ES_TO_EN.put("brocoli",          "broccoli");
        ES_TO_EN.put("coliflor",         "cauliflower");
        ES_TO_EN.put("lechuga",          "lettuce");
        ES_TO_EN.put("calabacin",        "zucchini");
        ES_TO_EN.put("calabaza",         "pumpkin");
        ES_TO_EN.put("champinon",        "mushroom");
        ES_TO_EN.put("champinones",      "mushrooms");
        ES_TO_EN.put("setas",            "mushrooms");
        ES_TO_EN.put("boletus",          "porcini mushroom");
        ES_TO_EN.put("seta",             "mushroom");
        ES_TO_EN.put("niscalo",          "saffron milk cap mushroom");
        ES_TO_EN.put("trufa",            "truffle");
        ES_TO_EN.put("berenjenas",       "eggplant");
        ES_TO_EN.put("berenjena",        "eggplant");
        ES_TO_EN.put("apio",             "celery");
        ES_TO_EN.put("pepino",           "cucumber");
        ES_TO_EN.put("aguacate",         "avocado");
        ES_TO_EN.put("maiz",             "corn");
        ES_TO_EN.put("puerro",           "leek");
        ES_TO_EN.put("puerros",          "leeks");
        ES_TO_EN.put("rabano",           "radish");
        ES_TO_EN.put("nabo",             "turnip");
        ES_TO_EN.put("alcachofa",        "artichoke");
        ES_TO_EN.put("alcachofas",       "artichokes");
        ES_TO_EN.put("esparragos",       "asparagus");
        ES_TO_EN.put("esparrago",        "asparagus");
        ES_TO_EN.put("remolacha",        "beet");
        ES_TO_EN.put("col",              "cabbage");
        ES_TO_EN.put("repollo",          "cabbage");
        ES_TO_EN.put("col rizada",       "kale");
        ES_TO_EN.put("guisantes",        "peas");

        // Legumbres
        ES_TO_EN.put("judias",           "beans");
        ES_TO_EN.put("alubias",          "beans");
        ES_TO_EN.put("lentejas",         "lentils");
        ES_TO_EN.put("garbanzos",        "chickpeas");
        ES_TO_EN.put("soja",             "soy");

        // Frutas
        ES_TO_EN.put("limon",            "lemon");
        ES_TO_EN.put("limones",          "lemons");
        ES_TO_EN.put("lima",             "lime");
        ES_TO_EN.put("limas",            "limes");
        ES_TO_EN.put("manzana",          "apple");
        ES_TO_EN.put("manzanas",         "apples");
        ES_TO_EN.put("platano",          "banana");
        ES_TO_EN.put("naranja",          "orange");
        ES_TO_EN.put("naranjas",         "oranges");
        ES_TO_EN.put("fresa",            "strawberry");
        ES_TO_EN.put("fresas",           "strawberries");
        ES_TO_EN.put("uva",              "grape");
        ES_TO_EN.put("uvas",             "grapes");
        ES_TO_EN.put("pera",             "pear");
        ES_TO_EN.put("melocoton",        "peach");
        ES_TO_EN.put("mango",            "mango");
        ES_TO_EN.put("pina",             "pineapple");
        ES_TO_EN.put("coco",             "coconut");
        ES_TO_EN.put("arandano",         "blueberry");
        ES_TO_EN.put("arandanos",        "blueberries");
        ES_TO_EN.put("frambuesas",       "raspberries");

        // Grasas y aceites
        ES_TO_EN.put("aceite de oliva",  "olive oil");
        ES_TO_EN.put("aceite",           "oil");
        ES_TO_EN.put("aceite de coco",   "coconut oil");
        ES_TO_EN.put("aceite de sesamo", "sesame oil");
        ES_TO_EN.put("manteca",          "lard");

        // Especias y condimentos
        ES_TO_EN.put("sal",              "salt");
        ES_TO_EN.put("pimienta negra",   "black pepper");
        ES_TO_EN.put("pimienta",         "pepper");
        ES_TO_EN.put("comino",           "cumin");
        ES_TO_EN.put("pimenton",         "paprika");
        ES_TO_EN.put("oregano",          "oregano");
        ES_TO_EN.put("albahaca",         "basil");
        ES_TO_EN.put("tomillo",          "thyme");
        ES_TO_EN.put("romero",           "rosemary");
        ES_TO_EN.put("jengibre",         "ginger");
        ES_TO_EN.put("cilantro",         "cilantro");
        ES_TO_EN.put("perejil",          "parsley");
        ES_TO_EN.put("canela",           "cinnamon");
        ES_TO_EN.put("curry",            "curry");
        ES_TO_EN.put("curcuma",          "turmeric");
        ES_TO_EN.put("nuez moscada",     "nutmeg");
        ES_TO_EN.put("laurel",           "bay leaf");
        ES_TO_EN.put("pimentón dulce",   "sweet paprika");
        ES_TO_EN.put("azafran",          "saffron");
        ES_TO_EN.put("cayena",           "cayenne");
        ES_TO_EN.put("chile",            "chili");
        ES_TO_EN.put("chili",            "chili");
        ES_TO_EN.put("jalapeno",         "jalapeno");
        ES_TO_EN.put("mostaza",          "mustard");

        // Salsas y líquidos
        ES_TO_EN.put("salsa de soja",    "soy sauce");
        ES_TO_EN.put("salsa de tomate",  "tomato sauce");
        ES_TO_EN.put("caldo de pollo",   "chicken broth");
        ES_TO_EN.put("caldo de carne",   "beef broth");
        ES_TO_EN.put("caldo",            "broth");
        ES_TO_EN.put("vinagre",          "vinegar");
        ES_TO_EN.put("vinagre de vino",  "wine vinegar");
        ES_TO_EN.put("vino blanco",      "white wine");
        ES_TO_EN.put("vino tinto",       "red wine");
        ES_TO_EN.put("vino",             "wine");
        ES_TO_EN.put("cerveza",          "beer");
        ES_TO_EN.put("agua",             "water");

        // Dulces y repostería
        ES_TO_EN.put("azucar",           "sugar");
        ES_TO_EN.put("azucar moreno",    "brown sugar");
        ES_TO_EN.put("miel",             "honey");
        ES_TO_EN.put("chocolate",        "chocolate");
        ES_TO_EN.put("cacao",            "cocoa");
        ES_TO_EN.put("vainilla",         "vanilla");
        ES_TO_EN.put("levadura",         "yeast");
        ES_TO_EN.put("bicarbonato",      "baking soda");

        // Frutos secos
        ES_TO_EN.put("almendras",        "almonds");
        ES_TO_EN.put("almendra",         "almond");
        ES_TO_EN.put("nueces",           "walnuts");
        ES_TO_EN.put("nuez",             "walnut");
        ES_TO_EN.put("piones",           "pine nuts");
        ES_TO_EN.put("pasas",            "raisins");
        ES_TO_EN.put("anacardos",        "cashews");
        ES_TO_EN.put("pistachos",        "pistachios");
        ES_TO_EN.put("cacahuetes",       "peanuts");
        ES_TO_EN.put("cacahuete",        "peanut");
        ES_TO_EN.put("sesamo",           "sesame");
    }

    public static String translateIngredients(String ingredients) {
        if (ingredients == null || ingredients.isBlank()) return ingredients;
        StringBuilder result = new StringBuilder();
        String[] parts = ingredients.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(",");
            result.append(translateOne(parts[i].trim()));
        }
        return result.toString();
    }

    public static String translateOne(String ingredient) {
        if (ingredient == null || ingredient.isBlank()) return ingredient;
        String key = removeDiacritics(ingredient.trim().toLowerCase());
        String translation = ES_TO_EN.get(key);
        return translation != null ? translation : ingredient.trim();
    }

    static String removeDiacritics(String text) {
        if (text == null) return "";
        return text
                .replace("á", "a").replace("à", "a").replace("â", "a")
                .replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("í", "i").replace("ì", "i").replace("î", "i")
                .replace("ó", "o").replace("ò", "o").replace("ô", "o")
                .replace("ú", "u").replace("ù", "u").replace("û", "u")
                .replace("ü", "u").replace("ñ", "n")
                .replace("ç", "c");
    }
}
