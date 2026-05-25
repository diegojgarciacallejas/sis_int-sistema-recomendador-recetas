package behaviours.ontologybehaviours;

import java.util.*;

public class FoodOntology {

    private static final Map<String, FoodCategory> CATEGORIES = new HashMap<>();
    private static final List<SubstitutionRule> SUBSTITUTIONS = new ArrayList<>();

    static {

        String[] proteins = {
                "abalone", "acorn fed iberian pig", "alaska pollock", "alaskan pollock fillet", "albacore",
                "american lobster", "american sea scallop", "anchovy", "anchovy filets", "anchovy paste",
                "anglerfish", "angus beef", "argentine hake", "argentine shortfin squid", "atlantic bay scallop",
                "atlantic calico scallop", "atlantic cod", "atlantic herring", "atlantic herring fillet",
                "atlantic mackerel", "atlantic salmon", "atlantic wolffish", "baby squid", "bacon",
                "baked lean lamb saddle", "baltic herring", "baltic sprat", "barn eggs", "bayonne ham",
                "beef", "beef bones", "beef heart", "beef kidney", "beef knuckle", "beef liver", "beef lung",
                "beef meat", "beef meatball", "beef tongue", "beef tripe", "beef udder", "bigeye scad",
                "bigeye tuna", "black botifarra", "black pudding", "black seabream", "blue grenadier",
                "blue ling", "blue mussel", "blue whiting", "bogue", "boiled beef knuckle", "boiled beef oxtail",
                "boiled egg", "boiled lamb neck", "boston butt", "botifarra", "braised beef short ribs",
                "braised lamb kidneys", "braised lamb leg", "braised lamb neck", "breast", "bresaola",
                "british beef", "british chicken", "british chicken breast", "british ham", "british lamb",
                "british pork", "british pork belly", "bull", "bull meat", "burbot", "calf liver", "camel","camel meat", "canned tuna", "capelin", "capelin raw", "caribbean spiny lobster", "carp",
                "category a eggs", "chicken", "chicken blood", "chicken bone", "chicken breast",
                "chicken breast fillet", "chicken cutlet", "chicken egg", "chicken egg white",
                "chicken egg yolk", "chicken egg yolk powder", "chicken fillet", "chicken fillet with skin",
                "chicken heart", "chicken liver", "chicken meat", "chicken meatball", "chicken neck",
                "chicken protein", "chicken stomach", "chicken thigh", "chicken thigh fillet", "chicken wing",
                "chickpea protein", "chilean mussel", "chipolata slim sausage", "chop grilled lamb rib",
                "chorizo", "chub mackerel", "chuck steak", "chum salmon", "clam", "clam meat",
                "clam or mussel", "cockles", "cod", "cod liver", "cod roe", "common bream",
                "common cooked octopus", "common cuttlefish", "common dab", "common dentex",
                "common octopus", "common sole", "concentrated whole egg", "cooked beef kidney",
                "cooked beef meat", "cooked beef tongue", "cooked calf liver", "cooked calf tongue",
                "cooked ham", "cooked lamb brains", "cooked lamb hearts", "cooked lamb livers",
                "cooked lamb sweetbread", "cooked lardoons", "cooked pork filet mignon", "cooked pork loin",
                "cooked pork rack", "cooked pork roast", "cooked shrimp", "cooked young goat", "coppa",
                "corvina", "cow", "crab", "crab substitute", "crayfish", "crayfish or shrimp",
                "cured sausage", "dashimaki tamago", "deep-sea shrimp", "deep-water cape hake",
                "deepwater redfish", "deer", "dehydrated soy protein", "dewlap", "donkey", "donkey meat",
                "dried beef preparation", "dry-cured ham", "dry-cured parma ham", "dry-cured serrano ham",
                "duck", "duck breast", "duck egg", "duck fillet", "duck foie gras", "duck gizzard",
                "duck leg", "duck liver", "duck meat", "duck wing", "edible crab", "egg", "egg derivatives",
                "egg powder", "egg protein", "egg white", "egg yolk", "egg yolk powder",
                "eggs from caged hens", "eggs from switzerland", "european anchovy", "european conger",
                "european flying squid", "european hake", "european lobster", "european oysters",
                "european perch", "european plaice", "european seabass", "european sprat",
                "farmed rainbow trout", "farmed raw carp", "farmed tilapia", "farmed trout", "farmed turbot","fish", "fish meat", "fish protein", "fish stock", "flounder", "frankfurter sausages",
                "free range chicken", "free range chicken eggs", "free range egg white", "free range egg yolk",
                "free range eggs", "fresh category a eggs", "fresh egg", "freshwater bream", "freshwater fish",
                "frog legs", "game animal", "garfish", "garlic sausage", "giant tiger prawn", "gilthead seabream",
                "goat", "goat meat", "goose", "goose liver", "goose meat", "grass-fed beef", "great scallop",
                "greater argentine", "greenland halibut", "grenadier", "grey shrimp", "grilled lamb chop fillet",
                "grilled lamb cutlet", "grilled lamb leg", "grilled lamb saddle", "grilled pork chop", "grouper",
                "gurnard", "haddock", "hake", "halal chicken filet", "halibut", "ham", "ham in cube",
                "ham sausages", "hard-boiled egg", "herring", "herring fillet", "heterocarpus", "horse",
                "horse mackerel", "horse meat", "humboldt squid", "indian squid", "jack mackerel",
                "kangaroo", "kangaroo meat", "lamb", "lamb chop fillet", "lamb cutlet", "lamb gut",
                "lamb hearts", "lamb kidneys", "lamb leg", "lamb livers", "lamb meat", "lamb neck",
                "lamb on skewer", "lamb rib", "lamb saddle", "lamb shoulders", "lamb sweetbread",
                "lean duck liver", "lean pork", "lemon sole", "lightly smoked cod roe", "ling",
                "liquid egg", "liquid egg white", "liquid egg yolk", "liquid whole egg", "lobster",
                "lumpfish egg", "mackerel", "mackerel fillet", "mantis shrimp", "meat", "meat bean",
                "meat patty", "meat preparation", "meat stock", "meatball", "mediterranean bass",
                "mediterranean mussel", "megrim", "merguez", "minced beef", "minced pork",
                "montbeliard sausage", "mortadella", "morteaux sausage", "mozambique tilapia",
                "mullet", "musky octopus", "mussel", "mutton", "mutton leg", "nemipterus spp",
                "nile perch", "nile tilapia", "noble scallop", "north pacific hake", "northern prawn",
                "norway lobster", "oarfish", "octopus", "octopus aegina", "offal", "oily fish", "omelette", "organic chicken egg", "organic milk proteins", "oscypek", "ossau-iraty",
                "oxtail", "oyster", "pacific cod", "pacific oyster", "pan-fried lamb leg",
                "pan-fried lamb rib", "pan-fried lamb saddle", "pandalus jordani", "paneer", "pangas",
                "pangas fillet", "parapenaeopsis stylifera", "parmigiano reggiano", "part skim mozzarella",
                "partridge", "pasteurised egg yolk", "pasteurized fresh egg", "patagonian grenadier",
                "patagonian scallop", "patagonian squid", "patty", "pea protein", "pea protein isolate",
                "pecorino", "pecorino romano", "penaeidae", "pepper jack", "pepperoni", "perch",
                "peruvian scallop", "petit suisse", "pharaoh cuttlefish", "picodon", "pig cheek",
                "pig snout", "pigs blood", "pigs trotters", "pink clam", "pink salmon", "pink shrimps",
                "poached egg", "pollack", "pont l'evêque", "porgies", "pork", "pork belly", "pork breast",
                "pork by-product", "pork chops", "pork cutlet", "pork filet mignon", "pork fillet",
                "pork heart", "pork kidney", "pork knuckle", "pork leg", "pork liver", "pork loin",
                "pork meat", "pork minute steak", "pork offal", "pork protein", "pork sausage",
                "pork shoulder", "pork spare-ribs", "pork tongue", "pork tripe", "potato protein",
                "poultry", "poultry liver", "poultry meat", "poultry protein", "pouting", "powdered egg white",
                "precooked chicken meat", "precooked tuna", "prepared meat", "proteic extruded soy",
                "protein", "proteins mix", "provolone", "quail", "quail eggs", "quark", "queen scallop",
                "quenelle", "quorn mushroom product", "rabbit", "rabbit liver", "rabbit meat", "raclette",
                "rainbow shrimp", "rainbow trout", "rainbow trout fillet", "raw alaska pollock",
                "raw anglerfish", "raw arctic char raw", "raw atlantic bass", "raw brown bullhead",
                "raw common sole", "raw duck egg", "raw european perch", "raw european plaice",
                "raw european seabass", "raw farmed salmon", "raw golden redfish", "raw grouper",
                "raw ham", "raw lean pork tenderloin", "raw merguez sausage", "raw nile perch",
                "raw peeled shrimp", "raw pork ham escalope", "raw pork loin", "raw pork shoulder",
                "raw salmon", "raw salmon trout", "raw spotted wolffish", "raw striped bass",
                "raw tub gurnard", "raw tuna", "raw wild salmon", "reblochon", "red prawn",
                "red swamp crayfish", "rehydrated egg white", "rehydrated pea protein", "rehydrated soy protein",
                "rehydrated wheat protein", "reindeer", "reindeer meat", "reindeer shoulder", "ricotta",
                "roast chicken", "roasted lamb leg", "roasted lamb shoulders", "rocamadour", "rockfish",
                "roe", "romano cheese", "roquefort", "russell's snapper", "saint marcellin", "saint nectaire",
                "saint-moret", "sainte maure cheese", "sainte-maure de touraine", "saithe", "salami",
                "salema", "salers", "salmon", "salmon fillet", "salted dried cod", "saltwater fish",
                "sardine", "sardine fillets", "sausage", "scallop", "scallop with coral", "scallop without coral",
                "scarlet shrimp", "scorpion fish", "scottish salmon", "sea squirt", "sea trout", "seabream",
                "seitan", "semi-hard cheeses", "sheep's-milk cheese", "shellfish", "shrimp", "skipjack tuna",
                "skipjack tuna fillet", "smoked atlantic salmon", "smoked bacon", "smoked breast",
                "smoked cod roe", "smoked cooked lardoons", "smoked cured ham", "smoked cured sausage",
                "smoked lardoons", "smoked pork belly", "smoked rainbow trout", "smoked salmon",
                "smoked salmon trimmings", "smoked sausage", "snail", "snapper", "sockeye salmon",
                "soft cheese", "soft white cheese", "soft-boiled egg", "sole", "soy protein",
                "soy protein isolate", "soy protein product", "spineless cuttlefish", "spiny lobster", "spiny scorpionfish", "spotted wolffish",
                "squid", "stewed pork", "striped bass", "sugared egg yolk", "sunflower protein",
                "superior chicken fillet", "superior cooked ham", "surimi", "swordfish",
                "swordtip squid", "tempeh", "teuthida", "textured pea protein", "textured plant protein",
                "textured soy protein", "textured wheat protein", "thread-fin bream", "threadfin bream",
                "tilapia", "tilapia fillet", "tofu", "tonguesole", "trout", "tub gurnard", "tuna",
                "tuna chunks", "tuna egg", "tuna in brine", "tuna in oil", "tuna steak", "turbot",
                "turkey", "turkey breast", "turkey heart", "turkey liver", "turkey meat", "turkey thigh",
                "turkey thigh meat", "undulate venus", "veal", "veal bone", "veal meat", "venison",
                "weever", "wheat gluten", "wheat protein", "wheat protein isolate", "whey protein",
                "whey protein isolate", "white chicken meat", "white fish", "white fish fillets",
                "white fish meat", "white scallop", "whiteleg shrimp", "whiting", "whole chicken",
                "whole egg", "whole fresh eggs", "whole powdered egg", "whole rice protein",
                "wild boar", "wild boar meat", "wild salmon", "wild turbot", "yeast protein",
                "yellowfin tuna", "yellowstripe scad", "young goat"
        };
        for (String p : proteins) CATEGORIES.put(p, FoodCategory.PROTEIN);








        String[] carbs = {
                "adzuki bean", "alkaline noodles", "almond flour", "amaranth flour", "arborio rice",
                "arrowroot", "baby lima beans", "baguette", "barley", "barley and wheat malt", "barley flakes",
                "barley flour", "barley groats", "barley malt flour", "barley starch", "barnyard millet",
                "basmati rice", "beans", "beluga lentils", "biscuit", "biscuit and chocolate", "biscuit pieces",
                "black bean", "black bean flour", "black chickpea", "black gram", "black lentil flour",
                "black lentils", "black quinoa", "black rice", "black soy bean", "black thai rice",
                "black turtle bean", "black-eyed pea", "blue corn", "blue lupin", "borlotti bean",
                "boston bean", "bran", "bran flour", "bread", "bread roll", "breadcrumbs", "breadfruit",
                "bretzel", "brioche", "british wholegrain wheat", "broad bean", "broad bean flour",
                "brown basmati rice", "brown beans", "brown lentils", "brown rice", "brown rice flour",
                "buckwheat", "buckwheat flakes", "buckwheat flour", "buckwheat grain", "buckwheat grit",
                "bulgur", "butter biscuit", "cake", "camargue rice","canned corn", "cannellini beans", "carnaroli rice", "carob bean", "carob seed flour",
                "caroube dalmate", "cassava", "cassava flour", "cereal", "cereal and seed mixture",
                "cereal crispies", "cereal extrudate", "cereal flakes", "cereal flour", "cereal with cocoa",
                "chestnut", "chestnut flour", "chestnut from the eu", "chestnut powder", "chickpea",
                "chickpea flour", "chinese chestnut", "cocoa biscuit", "coconut flour", "cooked basmati rice",
                "cooked chickpeas", "cooked fresh pasta", "cooked green lentils", "cooked pasta",
                "cooked rice", "cooked shortbread dough", "cooked split peas", "cooked tagliatelle",
                "cooked wheat", "cooked white rice", "corn flakes", "corn flour", "corn malt", "corn starch",
                "corn tortilla", "cornmeal", "couscous", "cowpea", "crispbread", "crispy flakes", "crouton",
                "crust", "crêpe", "dehydrated sourdough", "devitalised sourdough", "dextrinated barley flour",
                "dextrinated cornmeal", "dextrinated rice flour", "dextrinated rye flour",
                "dextrinated spelt flour", "dextrinated wheat flour", "dough", "dried broad bean",
                "dried chickpea", "dried kidney beans", "dried lentils", "dried lima bean", "dried pasta",
                "durum wheat", "durum wheat bulgur", "durum wheat flour", "durum wheat kernels",
                "durum wheat semolina", "durum wheat sourdough", "durum whole wheat flour", "egg pasta",
                "einkorn flour", "einkorn wheat", "emmer", "emmer flour", "extruded buckwheat flour",
                "extruded cereals", "extruded corn", "extruded rice", "faba bean preparation", "falafel","fermented rice", "fermented rye malt", "fermented wheat starch", "finger millet",
                "flax flour", "flour", "flour blend", "fortified wheat flour", "foxtail millet", "freekeh",
                "fresh egg pasta", "fresh pasta", "garden peas flour", "germinated black rice",
                "germinated brown rice", "germinated red rice", "germinated rice", "gingerbread",
                "glass noodle", "gluten free oat flour", "gluten free oats", "gluten-free oat flakes",
                "glutinous rice", "graham flour", "grated rye bread", "green banana flour", "grünkern",
                "hemp flour", "hom mali rice", "hulled barley", "hydrolysed cereal", "inactive dough",
                "instant mashed potatoes", "instant oatmeal", "ivory teff flour", "jodenkoeken",
                "kasha", "khorasan wheat", "khorasan wheat flour", "konjac flour", "lasagne",
                "lentil flour", "linguine", "little millet", "long grain basmati rice",
                "long grain brown rice", "long grain rice", "long grain white rice", "lupin flour",
                "lupin grit", "macaroni", "maize bran", "malt", "malt flour", "malted barley",
                "malted rye flakes", "malted rye flour", "malted wheat flakes", "malted wheat flour",
                "mashed potato", "medium grain rice", "medium grain white rice", "millet",
                "millet flakes", "millet flour", "millet groats", "mixed cereal flour", "modified corn flour",
                "modified flour", "müesli", "nachos", "natural rice sourdough", "natural rye sourdough",
                "natural sourdough", "natural wheat sourdough", "neapolitan wafer", "noodle", "oat",
                "oat base", "oat bran", "oat flakes", "oat flour", "oat grain", "oat malt", "oganic white quinoa","organic almond flour", "organic barley", "organic brown rice", "organic corn",
                "organic corn flour", "organic green lentils", "organic long grain rice",
                "organic red lentils", "organic round rice", "organic sweet potato",
                "organic white round rice", "panada", "panko", "parboiled rice", "pardina lentils",
                "pasta", "pastry", "pea flour", "pea starch", "peanut flour", "pearl barley",
                "pearled farro", "pedrosillano chickpeas", "penne", "petit-beurre", "pierogi",
                "pilsner malted barley", "pinto beans", "pita", "pizza base", "pizza crust",
                "pizza dough", "plazma biscuit", "popcorn", "pot barley", "potato", "potato flakes",
                "potato flour", "potato starch", "pre-cooked durum wheat", "precooked lentils",
                "precooked pasta", "precooked potato", "pregelatinized rye flour", "psyllium",
                "puffed buckwheat", "puffed cereal", "puffed rice", "puffed rice and corn",
                "puffed wheat", "pulse", "pumpkin seed flour", "quinoa", "quinoa flour",
                "red bean", "red cargo rice", "red lentil flour", "red lentils", "red quinoa",
                "red rice", "refined wheat flour", "rehydrated chickpeas", "rehydrated nuckwheat",
                "restructured soya flour", "rice", "rice flakes", "rice flour", "rice from italy",
                "rice groats", "rice noodles", "rice semolina", "rice starch", "risoni", "risotto rice",
                "rivet wheat", "round rice", "rusk", "russet potatoes", "rye", "rye bread",
                "rye bread breadcrumbs", "rye flakes", "rye flour", "rye grains", "rye kernels",
                "rye malt", "rye malt flour", "rye malt grist", "rye scalding", "sablé", "sago",
                "semi-whole rice", "semi-whole round rice", "semi-whole thai rice", "semi-wholemeal",
                "semi-wholemeal flour", "semolina", "semolina blend", "semolina flour", "sesame seed bun",
                "shortbread", "shortcrust dough", "sifted rye flour", "small red beans", "soaked chickpeas",
                "soba", "soft wheat", "soft wheat flour", "soft whole wheat flour", "sorghum",
                "sorghum flour", "soya flour", "soya flower", "spaghetti", "special bread",
                "speculaas", "speculoos", "spelled krispies", "spelt", "spelt flakes",
                "spelt flour", "spelt groats", "split pea flour", "sponge cake", "sprouted barley", "sprouted buckwheat",
                "sprouted lentils", "sprouted millet", "sprouted spelt", "sprouted wheat", "starch",
                "starch wafer", "stuffed pasta", "sugar beet fibre", "sugarcane fiber", "sushi rice",
                "swedish potato", "sweet bean paste", "sweet potato", "sweet potato noodle",
                "sweet potato starch", "sweetcorn", "tagliatelle", "tapioca", "tapioca dextrin",
                "tapioca flour", "tapioca maltodextrin", "taro", "taro tuber", "teff", "teff flour",
                "teff seed", "tenkasu", "tenkasu with shrimp", "thai long grain rice", "thai rice",
                "thaï brown rice", "tigernut", "tortilla", "triticale", "udon", "vermicelli", "wafer",
                "waffle", "waffle with cocoa", "waxy corn", "waxy maize starch", "wheat", "wheat bran",
                "wheat breadcrumbs", "wheat bulgur", "wheat cakes", "wheat crispbread", "wheat fiber",
                "wheat flakes", "wheat flour", "wheat flour preparation", "wheat germ", "wheat grain",
                "wheat grit", "wheat groats", "wheat malt", "wheat maltodextrin", "wheat semolina",
                "wheat sourdough", "wheat sourdough powder", "wheat stalk fiber", "wheat starch",
                "wheat tortilla", "white basmati rice", "white beans", "white corn", "white corn flour",
                "white lupin", "white quinoa", "white quinoa flour", "white rice", "white rice flour",
                "white rice koji", "white round rice", "whole buckwheat flakes", "whole grain brown rice",
                "whole grain corn flour", "whole grain durum wheat", "whole grain flour blend",
                "whole grain millet flour", "whole grain oat flakes", "whole grain quinoa flour",
                "whole grain rice", "whole grain rye flakes", "whole grain spelt flakes",
                "whole grain teff flour", "whole grain teff seed", "whole grain triticale",
                "whole green pea flour", "whole round rice", "whole rye sourdough", "whole sorghum flour",
                "whole soybean grain", "whole spelt", "whole spelt kernels", "whole wheat",
                "whole wheat bulgur", "whole wheat flakes", "whole wheat flour", "whole wheat groats",
                "whole wheat kernels", "wholemeal", "wholemeal barley", "wholemeal barley flour",
                "wholemeal breadcrumbs", "wholemeal buckwheat", "wholemeal cereal",
                "wholemeal cereal flakes", "wholemeal flour", "wholemeal lentil flour", "wholemeal oat",
                "wholemeal oat flour", "wholemeal rye", "wholemeal rye bread", "wholemeal rye flour",
                "wholemeal rye groats", "wholemeal spelt flour", "wild rice", "yam", "yellow corn",
                "yellow corn flour", "yellow lentils", "yellow lupin", "yellow pea", "yellow split pea"
        };
        for (String c : carbs) CATEGORIES.put(c, FoodCategory.CARBOHYDRATE);







        String[] dairy = {
                "abondance", "acid whey", "american cheese", "asiago", "asiago medium cheese", "beaufort",
                "bleu d'auvergne", "bleu de bresse", "bleu de gex", "blue cheese", "blue stilton", "brie",
                "brie de meaux", "brie de melun", "british cream", "british yogurt", "bryndza", "buffalo milk",
                "buffalo milk cream", "buffalo mozzarella", "butter", "buttermilk", "buttermilk powder",
                "buttermilk solids", "camembert", "cantal", "cantal salers", "casein", "caseinate", "chaource cheese",
                "charentes butter pdo", "cheddar", "cheddar powder", "cheese", "cheese powder",
                "cheese rind", "churned butter", "clarified butter", "clarified cow's butter", "colby cheese",
                "coloured cheddar", "comté", "concentrated whey", "condensed whey", "condensed whole milk",
                "cotija cheese", "cottage cheese", "cow cheese", "cow mozzarella", "cow's cheese with chives",
                "cow's milk", "cream", "cream cheese", "cream from normandy", "crème fraîche",
                "crottin cheese", "cured cheese", "demineralised whey", "dill butter", "double cream",
                "double gloucester", "edam", "emmental", "emmental from france", "evaporated milk",
                "faiselle", "farmers maroilles","fermented milk products", "feta", "filmjölk", "filtered whey powder", "follow-on milk",
                "fontal", "fontina", "fontina aop", "fourme d'ambert", "fourme de montbrison",
                "fresh butter", "fresh cream", "fresh cream cheese", "fromage blanc", "full fat soft cheese",
                "goat butter", "goat cheese", "goat milk", "goat's milk yogurt", "gorgonzola", "gouda",
                "grana padano", "grana padano pdo cheese", "grated emmental cheese", "greek yogurt",
                "gruyere pgi", "gruyère", "gruyère aop", "halloumi", "hard cheese", "havarti", "haymilk",
                "heavy cream", "industrial maroille", "jarlsberg", "kefir", "lactase", "lactose-free cream",
                "lactose-free quark", "lancashire cheese", "langres", "light cream", "lightly salted butter",
                "livarot", "low fat cheese", "low moisture mozzarella", "maasdam", "maroilles", "mascarpone",
                "medium fat hard cheese", "medium fat soft cheese", "medium mature cheddar",
                "melted cheddar", "melted cheese", "melted emmental cheese", "melted grated cheese",
                "melted powdered cheese", "mild cheddar", "mild kefir", "mild whole kefir",
                "mild whole yogurt", "mild yogurt", "milk", "milk enriched with cream",
                "milk fat substitute", "milk fat without lactose", "milk from france", "milk powder",
                "milk preparation", "mimolette", "mixed cheese", "monterey jack", "morbier", "mozzarella",
                "muenster cheese", "munster", "neufchâtel", "oat milk", "organic almond flour", "organic barley", "organic brown rice", "organic corn",
                "organic corn flour", "organic green lentils", "organic long grain rice",
                "organic red lentils", "organic round rice", "organic sweet potato",
                "organic white round rice", "panada", "panko", "parboiled rice", "pardina lentils",
                "pasta", "pastry", "pea flour", "pea starch", "peanut flour", "pearl barley",
                "pearled farro", "pedrosillano chickpeas", "penne", "petit-beurre", "pierogi",
                "pilsner malted barley", "pinto beans", "pita", "pizza base", "pizza crust",
                "pizza dough", "plazma biscuit", "popcorn", "pot barley", "potato", "potato flakes",
                "potato flour", "potato starch", "pre-cooked durum wheat", "precooked lentils",
                "precooked pasta", "precooked potato", "pregelatinized rye flour", "psyllium",
                "puffed buckwheat", "puffed cereal", "puffed rice", "puffed rice and corn",
                "puffed wheat", "pulse", "pumpkin seed flour", "quinoa", "quinoa flour",
                "red bean", "red cargo rice", "red lentil flour", "red lentils", "red quinoa",
                "red rice", "refined wheat flour", "rehydrated chickpeas", "rehydrated nuckwheat",
                "restructured soya flour", "rice", "rice flakes", "rice flour", "rice from italy",
                "rice groats", "rice noodles", "rice semolina", "rice starch", "risoni", "risotto rice",
                "rivet wheat", "round rice", "rusk", "russet potatoes", "rye", "rye bread",
                "rye bread breadcrumbs", "rye flakes", "rye flour", "rye grains", "rye kernels",
                "rye malt", "rye malt flour", "rye malt grist", "rye scalding", "sablé", "sago",
                "semi-whole rice", "semi-whole round rice", "semi-whole thai rice", "semi-wholemeal",
                "semi-wholemeal flour", "semolina", "semolina blend", "semolina flour", "sesame seed bun",
                "shortbread", "shortcrust dough", "sifted rye flour", "small red beans", "soaked chickpeas",
                "soba", "soft wheat", "soft wheat flour", "soft whole wheat flour", "sorghum",
                "sorghum flour", "soya flour", "soya flower", "spaghetti", "special bread",
                "speculaas", "speculoos", "spelled krispies", "spelt", "spelt flakes",
                "spelt flour", "spelt groats", "organic yogurt", "pasteurised buffalo milk", "pasteurised goat milk",
                "pasteurised whole milk", "pasteurized butter", "pasteurized milk yogurt",
                "pasteurized skimmed milk", "plain greek-style yogurt", "plain yogurt",
                "raw cream", "reconstituted butter", "semi-skimmed goat milk",
                "semi-skimmed mild yogurt", "semi-skimmed milk", "semi-skimmed milk powder",
                "sheep and goat milk", "sheep's whey", "sheeps milk", "sheeps or goat milk",
                "single cream", "skimmed goat milk", "skimmed mild yogurt", "skimmed milk",
                "skimmed milk powder", "skimmed milk soft cheese", "skimmed sheeps milk",
                "skimmed sweet whey", "skimmed yogurt", "skyr", "smetana", "sour cream",
                "soured milk", "sterilized cream", "sterilized uht cream", "stilton", "sweet cream butter",
                "sweet whey", "sweet whey powder", "sweetened condensed milk", "sweetened plain yogurt",
                "swiss cheese", "taleggio", "tilsit", "tomme", "tomme de savoie", "uht cream",
                "unsalted butter", "unsalted churned butter", "vintage cheddar", "västerbotten cheese",
                "wensleydale", "whey", "whey permeate", "whey permeate powder", "whey powder",
                "whey product", "whipped cream", "whipping cream", "white cheddar", "white stilton",
                "whole cream", "whole goat milk", "whole goat milk uht", "whole kefir", "whole milk",
                "whole milk mozzarella", "whole milk powder", "whole milk yogurt",
                "whole milk yogurt powder", "whole sheep milk", "whole yogurt", "yogurt", "yogurt powder",
                "young mimolette"
        };
        for (String d : dairy) CATEGORIES.put(d, FoodCategory.DAIRY);

        String[] veggies = {
                "acorn squash", "alfalfa", "alfalfa seeds", "algae", "artichoke", "artichoke heart",
                "ascophyllum", "ashitaba", "asparagus", "asparagus heads", "aubergine", "baby arugula",
                "baby carrots", "baby corn", "baby cucumber", "baby kale", "baby spinach", "bamboo",
                "bamboo shoot", "batavia", "bean sprouts", "beetroot", "beetroot juice", "beetroot powder",
                "beetroot sprouts", "belgian endive", "bell pepper", "bitter melon", "black carrot",
                "black carrot juice", "black maca root", "black radish", "black truffle", "bloody milk cap",
                "boletus", "broad-leaved endive", "broccoli", "broccoli cress", "broccoli floret",
                "brussels sprouts", "burdock", "butternut squash", "cabbage", "cactus", "cantharellus", "capers", "capsicum annuum", "cardoon", "carrot", "carrot concentrate",
                "carrot extract", "carrot juice", "carrot juice powder", "carrot powder", "carrot purée",
                "cauliflower", "cauliflower florets", "celeriac", "celery", "celery juice", "celery leaves",
                "celery puree", "celery stalk", "cep", "chard", "chard concentrate", "chayotte",
                "cherry plum tomatoes", "cherry tomato", "chestnut mushroom", "chicory", "chicory root",
                "chinese cabbage", "chinese yam", "chlorella", "chopped tomato", "collard greens",
                "colorful pepper", "concentrated leek juice", "cooked carrot", "cooked kohlrabi",
                "cooked parsnip", "cooked potato", "cooked red beetroot", "cooked rutabaga", "cooked salsify",
                "cooked spinach", "corn", "corn kernel", "corn salad", "courgette", "craterellus", "cress",
                "crudités", "crushed peeled tomato", "crushed tomato", "cucumber", "cucumber juice",
                "cucumber or gherkin", "cultivated mushroom", "curly endive", "daikon", "dandelion",
                "dark cep", "dehydrated onion", "dehydrated potato flakes", "dehydrated seaweed",
                "dehydrated shiitake", "dehydrated spirulina", "dried aubergine", "dried carrot",
                "dried courgette", "dried garlic", "dried tomatoes", "dried vegetables", "dulse", "edamame",
                "endive", "extra fine garden peas", "extra fine sweat peas", "extra-fine green beans",
                "fairy ring mushroom", "fennel", "field mushroom", "finely crushed tomato", "flageolets", "flat bean", "forest mushroom",
                "fresh garlic", "fresh glasswort", "fresh onions", "fresh tomatoes", "fresh vegetables",
                "fried onion", "friggitello", "frillice iceberg lettuce", "fruit vegetable", "fucus",
                "gai lan", "garden cress", "garden orache", "garden peas", "garlic", "garlic chive",
                "garlic paste", "garlic puree", "gherkin", "giant squash", "glasswort", "golden chanterelle",
                "grass pea", "green and black olives", "green and red lentils", "green asparagus",
                "green asparagus heads", "green baby batavia", "green bean", "green bell pepper",
                "green endive", "green flageolets", "green jalapeno peppers", "green lentils",
                "green olive", "green olives with pits", "green peas", "green pepper", "green soy bean",
                "green split pea", "green tomato", "green tomato puree", "grey pea", "grilled aubergines",
                "grilled chestnut", "grilled courgette", "grilled tomato", "heart of palm", "honey mushroom",
                "horn of plenty", "hot chili peppers", "hot friggitello", "hot jalapeno pepper",
                "hulled soya bean", "iceberg lettuce", "imleria", "imleria badia", "irish moss",
                "italian bell pepper", "italian pepper", "jalapeno pepper", "japanese hornwort",
                "jerusalem artichoke", "kalamata olive", "kalamon olive", "kale", "kale juice", "kale puree",
                "kale sprouts", "kelp", "kidney bean", "kikurage mushroom", "kimchi", "kohlrabi", "komatsuna",
                "kombu", "lactarius sanguifluus", "laminaria", "land cress", "large garden peas",
                "large lima beans", "large white beans", "laver", "leaf vegetable", "leccinum", "leek",
                "leek sprouts", "legume", "lentils", "lettuce", "lima bean", "lingot beans", "lotus root",
                "lupin bean", "maca", "marsh-mallow", "milkcap", "mirepoix", "mixed beans", "mixed mushrooms",
                "mixed salad", "mizuna", "morel", "mukimame", "mung bean", "mung bean sprout", "mushroom",
                "mushroom powder", "nameko", "nelumbo", "nettle", "new potato", "nori", "nozawana",
                "oak leaf lettuce", "okara", "okra", "olive", "onion", "onion sproute", "onion-family vegetable", "organic carrots",
                "organic tomato", "organic tomato purée", "oyster mushroom", "pak choi",
                "palmaria palmata", "paprika or bell pepper", "parsley", "parsley root", "parsnip",
                "pea", "pearl onion", "peeled tomatoes", "pholiota", "pickled cucumber",
                "pickled gherkin", "pine bolete", "piquillo pepper", "pisto", "pitted green olives",
                "pitted olive", "pleurotus", "poblano chili", "pod and seed vegetable",
                "pointed cabbage", "portobello", "pureed green bean", "purple sweet potato",
                "purslane", "radicchio", "radish", "radish sprouts", "rapini", "raw alfalfa seeds",
                "raw black salsify", "raw cucumber pulp", "raw sauerkraut", "red and yellow peppers",
                "red baby batavia", "red beetroot", "red beetroot juice", "red bell pepper",
                "red cabbage", "red chard", "red chili pepper", "red chili purée", "red endive",
                "red grilled pepper", "red kuri squash", "red leaf lettuce", "red onion",
                "red radish", "red shallot", "red shiso", "red-capped scaber stalk",
                "rivolo cherry tomato", "rocket", "romaine lettuce", "romanesco broccoli",
                "root vegetable", "roscoff onions", "roscoff pdo onions", "round green beans",
                "rutabaga", "saccharina japonica", "saffron milk cap", "salad", "salsify",
                "sasa veitchii", "sauerkraut", "sauerkraut juice", "savoy cabbage", "scaber stalk",
                "sea belt", "sea kale", "sea lettuce", "seaweed", "sheathed woodtuft", "shiitake",
                "shiso", "shoot vegetable", "sifted tomato", "slippery jack", "small white onions",
                "snow pea", "sorrel", "sorrel or acid", "spaghetti squash", "spaghetti squash pulp","spinach", "spinach leaves", "spinach or amaranth", "spinach powder", "spinach puree",
                "spirulina", "spirulina concentrate", "spirulina powder", "split pea", "spring onion",
                "sprouted alfalfa seeds", "sprouted soybeans", "stalk vegetable", "straw mushroom",
                "stuffed olives", "stuffed poblano peppers", "stuffed tomatoes", "sugar beet", "suillus",
                "summer cep", "summer truffle", "sundried tomatoes", "sweet peas", "tangle",
                "taproot vegetable", "tender garlic", "thongweed", "tomato", "tomato concentrate",
                "tomato cubes", "tomato juice", "tomato pulp", "tomato purée", "tomatoes-in-tomato-juice",
                "tremella fuciformis", "truffle", "tuber", "tuber magnatum", "turnip", "turnip-rape",
                "vegetable", "vegetable blend", "vegetable concentrate", "vegetable extract",
                "vegetable juice", "velvet bolete", "very fine green beans", "vine leaf",
                "vinegar cucumbers", "vinegar gherkin", "violet asparagus", "violet carrot", "wakame",
                "water chestnut", "watercress", "wax bean", "wax gourd", "weeping bolete", "welsh onion",
                "white asparagus", "white asparagus heads", "white cabbage", "white mushroom",
                "white onion", "whole kalamon olive", "wild garlic", "wild rocket", "wood ear", "yacón",
                "yellow bell pepper", "yellow carrot", "yellow foot", "yellow onion", "yomogi"
        };
        for (String v : veggies) CATEGORIES.put(v, FoodCategory.VEGETABLE);







        String[] fruits = {
                "acai berry", "acai juice", "acerola", "acerola juice", "acerola powder", "ajwa dates",
                "akee apple", "american cranberry", "apple", "apple juice", "apple pieces", "apple pulp",
                "apple puree", "apple with orange juice", "apricot", "apricot juice", "apricot juice and puree",
                "apricot pulp", "apricot puree", "babić", "banana", "bastardo", "bearberry", "bergamot",
                "bergeron apricot", "berries", "berry blend", "bilberry", "bitter orange", "black cherry",
                "black grape", "black mulberry", "blackberry", "blackcurrant", "blackcurrant pieces",
                "blatina", "blood orange", "blood orange juice", "blueberry", "blueberry juice", "boabab",
                "bogdanuša", "boysenberry", "boysenberry juice", "bramley apple", "bramley apple purée",
                "cabernet", "cabernet franc", "cabernet sauvignon", "cactus fig", "calamansi", "candied lemon zest", "candied orange peel", "candied papaya", "cantaloupe",
                "cape gooseberry", "carambola", "carignan", "cashew apples", "cavendish banana",
                "charentais melon", "chasselas", "chasselas grape juice", "cherry", "cherry juice",
                "cherry paste", "cherry preparation", "cherry purée", "chickasaw plum", "chinotto",
                "chokeberry", "chokeberry juice", "cider apple juice", "cider apples", "cinnamon apple",
                "cinsaut", "citron", "citrus fruit", "citrus fruit juice", "citrus unshiu", "clementine",
                "clementine pulp", "cloudberry", "concentrated apple juice", "concentrated apple must",
                "concentrated date juice", "concentrated grape juice", "concentrated grape must",
                "concentrated juice", "concentrated lemon juice", "concentrated mango juice",
                "concentrated mango puree", "concentrated pear puree", "concord grape", "concord grape juice",
                "conference pear", "cooked grape must", "cooking apple", "corsican pomelo", "cranberry",
                "custard apple", "damson", "date", "date juice", "date paste", "deglet nour dates",
                "dessert apple", "dewberry", "dobričić", "dog rose", "dog rose fruit", "dried apple",
                "dried apple pieces", "dried apricots", "dried banana", "dried fig", "dried fruits",
                "dried goji berries", "dried organic fig", "dried peach", "durian", "eating orange",
                "elder", "elderberry", "elderberry juice", "fig", "fig paste", "flat peach", "forest berries", "forest berries paste",
                "fruit", "fruit concentrate", "fruit extract", "fruit juice", "fruit juice concentrate",
                "fruit puree", "fruits and nuts", "fruits and vegetables", "fruits preparation",
                "gala apple", "galia melon", "gamay", "gewürztraminer", "glacé cherry",
                "golden delicious apple", "goji", "gooseberry", "gordal olive", "granny apple",
                "granny smith apple juice", "grape", "grape juice", "grape must", "grapefruit",
                "grapefruit juice", "grapefruit or pomelo", "grapefruit pulp", "green apple",
                "greengage", "grenache", "grk bijeli", "grolleau", "gros manseng", "guava",
                "guava puree", "habanero chili", "halkidiki olives", "hass avocado", "honeydew melon",
                "idared apple", "idared red apple", "indian gooseberry", "jackfruit", "japanese plum",
                "kalooteh dates", "kenta dates", "khudri dates", "kiwi", "kumquat", "kumquat juice",
                "lemon", "lemon cells", "lemon juice", "lemon peel", "lemon pulp", "lemon puree",
                "lemon zest", "lime", "lime juice", "lime juice concentrate", "lime pulp", "lingonberry",
                "lingonberry juice", "loganberry", "longan", "lukuma", "lychee", "lychee pulp",
                "malaceous fruit", "malbec", "malvasia", "mandarin", "mandarin peel", "mandarin pulp",
                "mango", "mango apple mix", "mango imported by boat", "mango juice", "mango puree",
                "manzanilla olive", "maraština", "medjool dates", "medlar", "melon", "merlot",
                "mirabelle plum", "mixed fruit", "mixed peel", "mourvèdre", "muscadelle", "muscadet",
                "muscadine grape juice", "muscat grape juice", "muscat of alexandria", "nectarine",
                "nielluccio", "oganic orange zest", "orange", "orange carrot", "orange cells", "orange juice", "orange juice with pulp",
                "orange pulp", "organic apple", "organic apple juice", "organic cherry", "organic dates",
                "organic fig", "organic orange juice", "organic plum", "organic raspberry", "papaya",
                "passionfruit", "peach", "peach juice", "peach pulp", "peach puree", "pear", "pear juice",
                "pear juice and puree", "pear juice concentrate", "pear preparation", "pear puree",
                "pears in cubes", "persimmon", "persimmon juice", "persimmon pulp", "pineapple",
                "pineapple juice", "pineapple purée", "pineapple segments", "pink grapefruit",
                "pink grapefruit juice", "pink lady apple", "pinot meunier", "pinot noir",
                "pitted cherry", "pitted dried apricots", "plantain", "plantain banana", "plavac mali",
                "plavina", "plum", "pomegranate", "pomegranate juice", "pomegranate ruby red",
                "pomelo", "pomelo juice", "pomelo pulp", "pošip", "prune", "prunes from agen",
                "prunes from ente", "prunus species fruit", "pumpkin", "pumpkin juice", "pumpkin pulp",
                "pure grape juice", "pure lemon juice", "pure pineapple juice", "pure pomegranate juice",
                "quince", "rambutan", "raspberry", "raspberry juice", "raspberry puree", "red apple",
                "red currant juice", "red fruits", "red grape", "red grape fruit juice", "red grape juice",
                "red pomelo", "red raspberries", "red seedless grape", "redcurrant", "reconstituted lime juice",
                "rhubarb", "rhubarb juice", "rose hip", "rowanberry", "royal gala apple", "rubus fruticosus",
                "sayer dates", "sea-buckthorn", "sea-buckthorn juice", "sloe", "sour cherry", "sour cherry juice","squeezed apple juice", "squeezed orange juice", "star ruby pomelo", "strawberry",
                "strawberry juice", "strawberry pieces", "strawberry powder", "strawberry puree",
                "sugared cranberries", "sukkary dates", "sultana", "sultana raisin", "susac crni",
                "sweet cherry", "sweet valencia orange", "sylvaner", "syrah", "sémillon", "tamarillo",
                "tamarind", "tangerine", "tangerine juice", "tempranillo grape", "teran",
                "unfiltered apple juice", "vitis vinifera", "vranac", "vugava", "watermelon",
                "watermelon pulp", "welschriesling", "white cherry", "white grape", "white grape juice",
                "white mulberry", "white peach", "white seedless grape", "whitecurrant",
                "whole raspberry", "wild blueberry", "wild cherry", "wild cranberry", "wild lingonberry",
                "wild strawberry", "williams pear puree", "yellow grapefruit", "yuzu", "zinfandel",
                "žlahtina"
        };
        for (String f : fruits) CATEGORIES.put(f, FoodCategory.FRUIT);






        String[] fats = {
                "almond", "almond oil", "almond with skin", "almond without skin", "almonds pieces",
                "animal fat", "apricot kernels", "arbequina olive", "argan oil", "avocado", "avocado oil",
                "babassu", "beef fat", "beef suet", "bitter almond", "black olive", "black olives in oil",
                "black olives oil", "black sesame", "blanches almonds", "brazil nut", "british rapeseed oil",
                "brown flax seeds", "butterfat", "calf fat", "camelina oil", "canola", "canola oil", "canola oil preparation", "caramelized pecan nuts",
                "cashew nut oil", "cashew nut paste", "cashew nuts", "chia", "chia oil", "chia seed",
                "chicken fat", "chicken skin", "chopped hazelnuts", "cocoa butter", "cocoa butter substitute",
                "coconut", "coconut butter", "coconut chips", "coconut fat", "coconut flakes", "coconut oil",
                "coconut paste", "cold pressed hemp oil", "colza oil", "corn oil", "cottonseed",
                "cottonseed fat", "cottonseed oil", "crunchy peanut butter", "deodorized sunflower oil",
                "desiccated coconut", "dried chia seeds", "dried walnuts", "duck fat", "extra virgin coconut oil",
                "extra virgin olive oil", "fat", "fish oil", "flaked almonds", "flax", "flax seed", "fractionated coconut oil",
                "fractionated palm oil", "fresh walnut", "fully hydrogenated fat", "garlic oil",
                "ghee", "golden flax seeds", "goose fat", "grape seed", "grape seed oil",
                "green tea aroma oil", "grilled pistachio nut", "ground almonds", "ground hazelnuts",
                "ground piedmont hazelnut", "hazelnut", "hazelnut kernel", "hazelnut kernels",
                "hazelnut oil", "hazelnut paste", "hemp", "hemp seed", "hemp seed oil", "hemp seed paste",
                "high oleic sunflower", "high oleic sunflower oil", "hydrogenated coconut oil",
                "hydrogenated fat", "hydrogenated oil", "hydrogenated palm fat", "hydrogenated palm oil",
                "hydrogenated soy oil", "illipe oil", "korean pine", "koroneiki", "lard", "lardon",
                "lemon oil", "lime oil", "linseed oil", "macadamia nut", "mango kernel oil", "margarine",
                "mct oil", "milled roasted peanut", "millet seed", "mint oil", "modified palm oil",
                "mustard oil", "non-gmo canola oil", "non-gmo soybean oil", "nonpareil almonds", "nut",
                "oat oil", "oil", "oil and fat", "oilseed", "olive oil", "olive pomace oil", "onion oil", "organic linseed oil", "organic olive oil",
                "organic palm oil", "organic pumpkin seed", "organic pumpkin seed oil", "organic roasted peanut",
                "organic sesame oil", "organic sesame seeds", "organic sunflower oil", "palm",
                "palm and palm kernel oil", "palm and rapeseed", "palm fat", "palm kernel fat", "palm kernel oil",
                "palm kernel oil and fat", "palm oil", "palm oil and fat", "palm olein", "palm or palm kernel oil",
                "palm stearin", "peanut", "peanut butter", "peanut oil", "peanut paste", "peanut with shell",
                "pecan nut", "peppermint oil", "piedmont hazelnut", "piedmont hazelnut paste", "pine nuts",
                "pistachio nuts", "pistachio nuts in shells", "pistachio paste", "pistachio seed oil",
                "pork fat", "poultry fat", "powdered almonds", "pure cocoa butter", "pumpkin seed",
                "pumpkin seed oil", "rapeseed", "rapeseed oil", "raw almonds", "raw blanched almonds",
                "raw cacao butter", "raw cashews", "raw chestnut", "red palm fruit oil", "red pepper seed oil",
                "refined coconut oil", "refined olive oil", "refined olive pomace oil", "refined palm oil",
                "refined palmolein oil", "refined rapeseed oil", "refined sesame oil", "refined soya oil",
                "refined sunflower oil", "refined vegetable oil", "rice bran oil", "roasted almonds",
                "roasted nibbed hazelnuts", "roasted peanut", "roasted pin nuts", "safflower fat",
                "safflower oil", "sal fat", "seed", "seed and berry mix", "sesame", "sesame oil",
                "sesame paste", "sesame seeds", "shea butter", "shea oil", "sheeps fat", "shelled peanut",
                "shelled pistachios", "shorea robusta seed oil", "soya bean","sprouted sunflower seeds", "suet", "sunflower", "sunflower fat", "sunflower oil",
                "sunflower seed", "sunflower seed flour", "sunflower seed paste", "sweet almond",
                "tahini", "tangerine oil", "toasted hazelnuts", "toasted sesame oil", "tree nut",
                "turkey fat", "turkey skin", "unrefined olive oil", "vegetable fat",
                "vegetable margarine", "vegetable oil", "vegetable oil and fat", "vegetable oil blend",
                "vegetable suet", "virgin coconut oil", "virgin hazelnut oil", "virgin olive oil",
                "virgin sesame oil", "wagyu-fat", "walnut", "walnut kernel", "walnut oil",
                "walnuts from grenoble", "white chia seed", "white sesame", "white sesame seeds",
                "whole almonds", "whole walnuts"
        };
        for (String fat : fats) CATEGORIES.put(fat, FoodCategory.FAT);






        String[] spices = {
                "achillea millefolium", "allspice", "angelica", "aniseed", "aniseed oil", "arctostaphylos uva-ursi",
                "aromatic leaf", "asafoetida", "ashwagandha", "barbecue seasoning", "basil", "basil leaf",
                "bay leaf", "bergamot flower", "bergamot leaf", "bird's eye chili", "black and green pepper",
                "black and white pepper", "black cardamom", "black cumin", "black galangal", "black garlic",
                "black mustard seed", "black pepper", "black peppercorns", "black salt", "blackberry leaf",
                "blue fenugreek", "blue poppy seed", "borage", "bourbon vanilla", "bourbon vanilla beans",
                "bourbon vanilla extract", "bourbon vanilla powder", "brown mustard", "brown mustard seed",
                "burnet", "calluna vulgaris","capsicum extract", "caraway", "caraway seed", "cardamom", "cardamom pod", "cardamom seed",
                "cassia", "cayenne pepper", "celery powder", "celery salt", "celery seed", "celtic sea salt",
                "ceylon cinnamon", "chervil", "chili pepper", "chili puree", "chipotle", "chive seasoning",
                "chives", "cinnamon", "cinnamon powder", "citrus oil", "clove", "coarse salt", "coarse sea salt",
                "coriander", "coriander leaf", "coriander paste", "coriander seed", "cracked black pepper",
                "cumin", "cumin seeds", "curing salt", "curry", "curry paste", "dijon mustard", "dill",
                "dill seed", "dried basil", "dried cayenne pepper", "dried fenugreek leaf", "dried lovage",
                "dried marjoram", "dried oregano", "dried rosemary", "english mustard", "espelette chili pepper",
                "evaporated salt", "exhausted vanilla pod", "exhausted vanilla seeds", "fennel seed",
                "fenugreek", "fenugreek leaf", "fenugreek seed", "fleur de sel", "fresh basil", "fresh chervil", "fresh parsley", "fresh rosemary",
                "galangal", "garam masala", "garlic extract", "garlic salt", "genoese basil",
                "ginger", "ginger extract", "ginger paste", "ginger powder", "green cardamom",
                "grey pepper", "grey salt", "grey salt from guérande", "ground vanilla beans",
                "guajillo chili", "herbal pepper", "herbes de provence", "herbs and spices",
                "himalayan pink salt", "himalayan salt", "himalayan sea salt", "horseradish",
                "horseradish paste", "hot paprika", "iodised salt", "iodised sea salt", "juniper",
                "juniper berry", "kaffir lime leaf", "korean chili pepper", "korean chili powder",
                "kosher salt", "lemon pepper", "liquorice root", "long pepper", "lovage",
                "lovage leaves", "lovage root", "mace", "marjoram", "mint", "mint leaf",
                "mixed spices", "mustard seed", "natural spice extract", "natural spices", "nutmeg",
                "nutmeg nut", "olive leaf", "onion powder", "oregano", "organic aniseed", "paprika", "paprika powder",
                "parsley", "pepper", "peppercorns", "peppermint leaf", "peri-peri", "pink peppercorn",
                "pink salt", "pizza seasoning", "poppy", "poppyseed", "processed salt", "ras el hanout",
                "refined salt", "rock salt", "rosemary", "saffron", "sage", "salt", "salt from camargue",
                "salt from guérande", "salt from ile de ré", "salt from kłodawa", "salt from noirmoutier",
                "salt from the alps", "salt substitute", "sea salt", "sea salt flakes", "sichuan pepper",
                "smoked paprika", "smoked salt", "sodium chloride", "spearmint", "spice", "spice extract", "spice paste", "spicy red pepper", "star anise", "strong dijon mustard", "strong mustard",
                "sumac", "summer savory", "sweet woodruff", "sweetmint", "tabasco chile", "tarragon",
                "thai basil", "thyme", "tonka bean", "tulsi", "turmeric", "turmeric powder",
                "unrefined salt", "unrefined sea salt", "vanilla", "vanilla pod", "vanilla powder",
                "vanilla seeds", "verbena", "wasabi", "white pepper", "yellow mustard seed", "za'atar",
                "zaatar"
        };
        for (String s : spices) CATEGORIES.put(s, FoodCategory.SPICE);






        String[] others = {
                "absinthe", "acacia honey", "acacia honey from france", "advocaat", "agave syrup", "ajvar",
                "alcohol", "alcohol vinegar", "alcohol vinegar powder", "aloe vera", "aloe vera juice",
                "amarena cherry coating", "amaretto", "amontillado sherry", "apple cider vinegar", "apple filling",
                "apple jam", "apple preparation", "apple vinegar", "apple wine", "apricot filling", "apricot jam",
                "arabica coffee", "armagnac", "aromatic caramel", "aspic", "baker's yeast", "balsamic vinegar",
                "barbecue sauce", "barley malt syrup", "barley malt vinegar", "barley syrup", "barley wort",
                "beef broth", "beef casing", "beef gelatin", "beer", "beet sugar", "beetroot syrup",
                "belgian chocolate", "belgian dark chocolate", "belgian milk chocolate", "berry coating",
                "berry preparation", "biscuit flavored paste", "biscuit preparation", "black tea",
                "black tea leaf", "black tea mix", "blackberry wine", "blackcurrant jam", "blend of eu honeys",
                "blend of honeys", "blend of non-eu honey", "blended scotch whisky", "blueberry coating",
                "blueberry filling", "bolognese sauce", "brewer's yeast", "brine", "broth", "brown cane sugar",
                "brown cane sugar cube", "brown rice syrup", "brown sugar", "brown sugar syrup",
                "buckwheat honey", "burger sauce", "béarnaise sauce", "cacao glaze", "calvados","camomile", "camomile flower", "campari", "canadian maple syrup", "candy sugar",
                "cane invert syrup", "cane sugar", "cane sugar cube", "cane sugar molasses",
                "cane sugar syrup", "caramel filling", "caramel flavouring", "caramel sauce",
                "caramel syrup", "caramelised rice syrup", "caramelised sugar", "caramelised sugar syrup",
                "carbonated mineral water", "carbonated spring water", "carbonated water", "cava",
                "celery flavouring", "cephalopod ink", "ceylon black tea", "ceylon green tea", "champagne",
                "champignon sauce", "chartreuse liqueur", "cheese flavouring", "cheese sauce",
                "chestnut spread", "chicken broth", "chicken flavouring", "chicken meat extract",
                "chickend fond", "chicory extract", "chili hummus", "chili in vinegar", "chili mayonnaise",
                "chili paste", "chili pepper flavouring", "chinese hibiscus", "chinotto extract", "chocolate",
                "chocolate bar", "chocolate chunk", "chocolate coating", "chocolate cream", "chocolate filling",
                "chocolate flavouring", "chocolate glaze", "chocolate ice cream", "chocolate liqueur",
                "chocolate paste", "chocolate powder", "chocolate sauce", "cider", "cider vinegar",
                "citric flavouring", "citron extract", "citrus flavouring", "clover honey", "cocoa",
                "cocoa bar", "cocoa bean", "cocoa coating", "cocoa filling", "cocoa flavouring", "cocoa nibs",
                "cocoa paste", "cocoa powder", "cocoa solids", "cocoa spread", "coconut blossom syrup",
                "coconut flavouring", "coconut milk", "coconut milk drink", "coconut sugar", "coconut water",
                "coffea robusta", "coffee", "coffee beans", "coffee powder", "coffee preparation", "cognac",
                "cointreau", "cola nut", "cold brew coffee", "compressed baker's yeast",
                "concentrated agave syrup", "condiment", "cooking broth", "cooking water",
                "corevaporated cane syrup", "coriander flavour", "corn dextrose", "corn glucose syrup",
                "corn molasses", "corn starch syrup", "corn syrup", "corn syrup solids", "corn vinegar",
                "cornflower", "crab extract", "crab flavour", "cranberry flavouring", "cream filling",
                "cream flavouring", "cream sauce", "creamy honey", "crustaceans flavouring", "curry sauce",
                "darf everlast", "dark chocolate", "dark chocolate chunks", "dark rum", "dark soy sauce",
                "dark syrup", "deactivated yeast", "decaffeinated black tea", "decaffeinated coffee",
                "decaffeinated green tea", "dehydrated beef stock", "dehydrated broth", "dehydrated glucose syrup",
                "dehydrated pork broth", "dehydrated poultry fond", "dehydrated poultry stock",
                "dehydrated veal fond", "demerara sugar", "dextrose", "dextrose monohydrate", "dill flavouring",
                "distilled vinegar", "distilled water", "dried malt extract", "duck fond", "dulce de leche",
                "eau de vie", "egg flavour", "elderberry extract", "elderberry flavouring", "elderberry syrup",
                "elderflower", "elderflower extract", "equisetum arvense", "espagnole sauce",
                "eucalyptus flavouring", "eucalyptus honey", "eucalyptus oil", "evaporated cane juice",
                "extract of corn malt", "fat reduced cocoa", "fat reduced cocoa powder", "fermented sugar", "filling", "filtered water", "fish gelatine", "fish sauce", "flake salt",
                "flavored poultry fond", "flavour enhancer", "flavouring", "flavouring preparation",
                "flower honey", "fond", "fondant", "forest berry coating", "forest honey", "fructose",
                "fructose syrup", "fruit flavouring", "fruit pectin", "fruit vinegar", "frying oil",
                "garlic flavouring", "garlic vinegar", "german camomile", "gin", "ginger syrup",
                "glaze", "glucose", "glucose powder", "glucose syrup", "glucose-fructose",
                "glucose-fructose syrup", "gluten", "golden syrup", "grape sugar", "grapefruit flavouring",
                "grapefruit syrup", "greek salad flavouring", "green tea", "green tea extract",
                "green tea extract powder", "green tea from china", "green tea leaf", "ground coffee",
                "ground roasted coffee", "ground rye malt", "guarana", "guarana extract",
                "guarana seed extract", "gunpowder tea", "harissa", "harissa mayonnaise", "hazelnut coating",
                "hazelnut filling", "hazelnut praline", "hazelnut spread", "herb", "herb extract", "herbal tea",
                "high fructose corn syrup", "highly fat reduced cocoa", "honey", "honey flavouring",
                "honey from france", "honey from new zealand", "honey from spain",
                "honey from the gâtinais", "honey from the provence", "honey roasted peanut", "honjozo",
                "hop extract", "hop oil", "hop preparation", "hops", "hops flavour", "hops yeast",
                "horseradish flavouring", "hot sauce", "hummus", "ice", "ice cream", "icing sugar",
                "instant coffee", "invert cane sugar", "invert sugar", "invert sugar solution",
                "invert sugar syrup", "irish whiskey", "jaggery", "jam", "jasmine petals", "juice",
                "juniper berry flavouring", "kanzuri", "katsuobushi", "kefir ferments",
                "kefir lactic ferments", "ketchup", "ketchup flavouring", "kirsch", "koji", "kombucha",
                "kombucha culture", "kvass wort", "kvass wort concentrate", "lactic ferments", "laurel extract",
                "laurel flavouring", "lavander honey", "lavender", "lavender flower", "lemon balm",
                "lemon extract", "lemon filling", "lemon flavouring", "lemon marmalade", "lemon pectin",
                "lemon peel extract", "lemon powder", "lemon preparation", "lemon verbena", "lemon vinaigrette",
                "lemonade", "lemongrass", "light soy sauce", "light syrup", "lime flavouring", "limoncello",
                "linden", "linden honey", "lingonberry jam", "liqueur", "liqueur wine", "liquid honey",
                "liquid smoke", "liquid sugar", "liquorice", "liquorice dragées", "liquorice extract",
                "liquorice paste", "liquorice powder", "liquorice root extract", "lobster flavour",
                "lukuma powder", "madeira", "malt extract", "malt flavouring", "malt syrup", "malt vinegar",
                "malted corn syrup", "maltose", "maltose syrup", "mango flavouring", "manuka honey",
                "maple extract", "maple flavouring", "maple sugar", "maple syrup", "maraschino",
                "marc de bourgogne", "marc de champagne", "marigold", "marinade", "marmalade", "marsala wine",
                "marshmallow", "marshmallow paste", "marzipan", "matcha", "mate", "maturing ferments",
                "mayonnaise", "meadowsweet", "meat flavour", "meringue", "microbial culture",
                "microbial rennet", "milk chocolate", "milk chocolate chips", "milk chocolate coating",
                "milk coating", "milk filling", "milk flavouring", "milk spread", "mineral water",
                "mint flavouring", "mirin", "miso", "mix of flower honeys", "mixed condiments",
                "mizuame", "mocha flavouring", "modified cognac", "modified red port", "molasses",
                "monk fruit extract", "mountain honey", "mountain spring water", "mushroom extract",
                "mushroom flavouring", "mushroom sauce", "must", "mustard", "mustard flavouring",
                "mustard sauce", "natural aniseed aroma", "natural apple flavouring", "natural barley extract",
                "natural basil flavouring", "natural casing", "natural clove flavoring",
                "natural cocoa flavouring", "natural coconut water", "natural cola flavouring",
                "natural dill flavouring", "natural extract", "natural fig flavouring", "natural fish flavouring",
                "natural flavouring", "natural herbs flavouring", "natural honey flavouring", "natural hop",
                "natural hops flavour", "natural hops flavouring", "natural lemon extract",
                "natural lemon flavouring", "natural lime flavouring", "natural malt flavouring",
                "natural mango flavouring", "natural maple extract", "natural maple flavouring",
                "natural milk flavouring", "natural mineral water", "natural mint flavouring",
                "natural nut flavouring", "natural onion flavouring", "natural orange extract",
                "natural paprika extract", "natural paprika flavor", "natural peach flavouring",
                "natural pear flavouring", "natural pineapple aroma", "natural pork flavouring",
                "natural rose flavouring", "natural rum flavouring", "natural sage flavouring",
                "natural sheep casing", "natural smoke flavouring", "natural spice flavouring",
                "natural spring water", "natural star anise aroma", "natural sweet wine",
                "natural tea flavouring", "natural thyme flavouring", "natural vanilla",
                "natural vanilla extract", "natural yeast", "nescafe coffee", "nib sugar",
                "non-iodised salt", "non-iodised sea salt", "nonpareils", "nougat", "nougat coating",
                "nougat filling", "nougat flavor paste", "nougat spread", "nuoc-mam", "nutritional yeast",
                "nutritional yeast flake", "olive natural flavouring", "olive paté", "onion extract", "onion flavouring", "onion paster", "orange blossom",
                "orange blossom honey", "orange blossom water", "orange essential oil", "orange extract",
                "orange filling", "orange flavouring", "orange leaves", "orange marmalade", "orange terpene",
                "orange zest", "organic acids", "organic agave syrup", "organic brown sugar",
                "organic cane sugar", "organic ceylon black tea", "organic cocoa beans",
                "organic cocoa paste", "organic coffee", "organic dark chocolate", "organic dextrose",
                "organic green tea", "organic honey", "organic hops", "organic lemon balm",
                "organic liquorice", "organic maltose syrup", "organic maple syrup", "organic mountain honey",
                "organic plant extracts", "organic red wine", "organic rooibos", "organic sugar",
                "organic verbena", "oyster flavour", "packaging gas", "pajarero figs", "palm sugar",
                "panna cotta", "paprika flavouring", "paprika paste", "passion fruit flavouring",
                "pasteurised", "pasteurized orange juice", "pasteurized lemon juice", "peach flavouring",
                "peach paste", "peach syrup", "pear flavouring", "pedro ximenez wine", "pelinkovac",
                "pencillium roqueforti", "penicillium", "penicillium candidum", "penicillium glaucum",
                "pepper extract", "pepper flavouring", "pepper mix", "peppermint", "peppermint flavouring",
                "perilla", "persipan", "pesto", "petit manseng", "petit verdot", "phosphate", "phosphor",
                "phylloquinone", "pine flavouring", "pineapple fibre", "pineapple flavouring",
                "pineapple syrup", "pistachio filling", "pistachio flavor paste", "plant", "plant concentrate",
                "plant extracts", "plum flavouring", "plum spread", "plum sugar", "pollen", "pomace brandy",
                "pomegranate seeds", "pork collagen", "pork collagen protein", "pork fond", "pork gelatin",
                "pork rind", "pork skin", "pork stock", "port", "potassium", "potassium fluoride",
                "potassium iodide", "potato dextrin", "potato fiber", "potato glucose syrup", "potato maltodextrin",
                "poulsard", "poultry broth", "poultry extract", "poultry fond", "poultry skin",
                "pounded red chili pepper", "powdered condiment", "powdered lemon zest", "powdered preparation",
                "praliné", "pre fried onions", "prebiotics", "prepacked almond paste", "prepacked burger sauce",
                "prepacked harissa", "prepacked marzipan", "prepacked mustard sauce", "preparation",
                "prepared sauce", "preservative", "preserved chili pepper", "preserved lemon",
                "probiotic cultures", "processed hops", "propellent gas", "propolis", "protein crisp",
                "provitamin a", "pure maple syrup", "pure vanilla", "pure vanilla extract", "pyridoxamine",
                "pyridoxine", "pyridoxine dipalmitate", "pyridoxine hydrochloride", "quince jelly",
                "quinine", "quinoa seeds", "raising agent", "rakia", "rakia sugar", "rapeseed lecithin",
                "rapeseed protein", "raspberry filling", "raspberry flavouring", "raspberry jam",
                "raspberry leaf", "raspberry liqueur", "raspberry sauce", "ravioli", "raw cacao powder",
                "raw kombucha", "red curry paste", "red miso paste", "red orange marmalade", "red port",
                "red wine", "red wine extract", "red wine from france", "red wine vinegar", "red yeast rice",
                "reduced fat mayonnaise", "reduced iron", "refrigerated panna cotta", "rehydrated soy fiber",
                "release agent", "remoulade sauce", "rennet", "retinol", "retinyl acetate", "retinyl palmitate",
                "rhizopus", "rhizopus culture", "rhodiola rosea", "ribwort plantain", "rice bran", "rice cream",
                "rice extract", "rice glucose syrup", "rice koji", "rice milk", "rice protein", "rice sourdough",
                "rice sourdough powder", "rice syrup", "rice vinegar", "rice wine", "riesling",
                "rkatsiteli", "roasted arabica coffee", "roasted buckwheat kasha", "roasted coffee",
                "roasted coffee beans", "roman camomile", "rooibos", "rose petals", "rose water",
                "roselle flower", "rosemary honey", "rosé wine", "roundnose grenadier", "royal jelly",
                "ruby port", "rum", "rum flavouring", "rye and wheat sourdough", "rye bran", "rye fiber",
                "rye malt extract", "rye sourdough", "rye sourdough powder", "safflower", "safflower concentrate",
                "safflower flower", "sage oil", "sal tree", "salsa", "salt and vinegar flavour",
                "salted butter caramel", "sambal", "sambal oelek", "sauce", "sauternes", "scalding",
                "sciacarello", "scotch whisky", "seafood flavouring", "seaweed extract", "selenium",
                "selenium enriched yeast", "semi-sweet", "semi-sweet chocolate", "sencha", "sequestrant",
                "serrano raw ham", "shallot", "shellfish extract", "shellfish fond", "sherry", "sherry vinegar",
                "shiitake extract", "shrimp flavouring", "shrimp paste", "shrimp powder", "siberian pine",
                "skimmed cocoa powder", "skyr cultures", "smoke", "smoke flavouring", "soapwort",
                "soapwort extract", "sodium", "sodium caseinate", "sodium citrate", "sodium fluoride",
                "sodium hydrogen selenite", "sodium iodate", "sodium iodide", "sodium molybdate",
                "sodium salts of amp", "sodium salts of cmp", "sodium selenate", "sodium selenite",
                "soluble corn fiber", "soluble fibre", "sourdough", "sourdough powder", "sourdough starter",
                "south african hake", "south pacific hake", "soy and sesame sauce", "soy base", "soy bran",
                "soy fiber", "soy milk", "soy preparation", "soy sauce", "soya", "soya bean fiber",
                "soya bean paste", "soya lecithin", "soya oil", "sparkling wine", "spelt bran",
                "spelt sourdough", "spelt sourdough powder", "spirit vinegar", "spring honey", "spring water", "squid ink", "stale wine",
                "starch syrup", "strawberry coating", "strawberry filling", "strawberry flavouring",
                "strawberry jam", "strawberry preparation", "sucrose", "sugar", "sugar beet syrup",
                "sugar coating", "sugar cube", "sugar free chocolate", "sugar syrup", "sugarcane",
                "sugarcane juice", "sundried tomato hummus", "sunflower honey", "sweet and sour sauce",
                "sweet cherry preparation", "sweet lime flavouring", "sweet soy sauce", "syrup",
                "table vinegar", "tamari", "tamarind paste", "tarragon flavouring", "tarragon vinegar",
                "tea", "tea extract", "tea from ceylon", "tea leaf", "tencha", "tequila",
                "teriyaki sauce", "thyme honey", "thyme oil", "tigernut milk", "tomato sauce",
                "torula yeast", "trehalose", "truffle flavouring", "turkey fond", "unrefined cane sugar",
                "unrefined palm sugar", "unrefined sugar", "vanilla extract", "vanilla flavouring",
                "vanilla ice cream", "vanilla preparation", "vanilla sugar", "veal fond",
                "vegetable broth", "vegetable flavouring", "vermouth", "vinaigrette", "vinegar",
                "vodka", "water", "wheat dextrose", "wheat glucose syrup", "wheat or corn dextrose",
                "wheat syrup", "whiskey", "whisky", "white balsamic vinegar", "white cane sugar",
                "white chocolate", "white chocolate coating", "white chocolate powder", "white dashi",
                "white distilled vinegar", "white rum", "white sugar", "white tea", "white vinegar",
                "white wine", "white wine extract", "white wine from cava", "white wine from france",
                "white wine vinegar", "whole brown cane sugar", "whole cane sugar", "whole milk chocolate",
                "wildflower honey", "wine", "wine vinegar", "worcestershire sauce", "wort", "wort extract",
                "xylose", "yeast", "yeast extract", "yeast extract aroma", "yeast extract powder",
                "yeast powder", "yerba mate extract", "yogurt coating"
        };
        for (String o : others) CATEGORIES.put(o, FoodCategory.OTHER);


        addSub("chicken", "turkey", 0.9);
        addSub("turkey", "chicken", 0.9);
        addSub("chicken breast", "turkey breast", 0.9);
        addSub("turkey breast", "chicken breast", 0.9);
        addSub("chicken thigh", "turkey thigh", 0.9);
        addSub("turkey thigh", "chicken thigh", 0.9);
        addSub("duck breast", "chicken breast", 0.7);
        addSub("chicken breast", "duck breast", 0.7);
        addSub("quail", "chicken", 0.6);
        addSub("chicken", "quail", 0.6);
        addSub("beef", "veal", 0.8);
        addSub("veal", "beef", 0.8);
        addSub("minced beef", "minced pork", 0.7);
        addSub("minced pork", "minced beef", 0.7);
        addSub("calf liver", "beef liver", 0.9);
        addSub("beef liver", "calf liver", 0.9);
        addSub("beef liver", "pork liver", 0.8);
        addSub("pork liver", "beef liver", 0.8);
        addSub("beef kidney", "pork kidney", 0.8);
        addSub("pork kidney", "beef kidney", 0.8);
        addSub("chuck steak", "beef knuckle", 0.8);
        addSub("beef knuckle", "chuck steak", 0.8);
        addSub("pork loin", "pork filet mignon", 0.8);
        addSub("pork filet mignon", "pork loin", 0.8);
        addSub("bacon", "smoked bacon", 0.9);
        addSub("smoked bacon", "bacon", 0.9);
        addSub("bacon", "pork belly", 0.8);
        addSub("pork belly", "bacon", 0.8);
        addSub("lamb leg", "mutton leg", 0.8);
        addSub("mutton leg", "lamb leg", 0.8);
        addSub("atlantic cod", "haddock", 0.9);
        addSub("haddock", "atlantic cod", 0.9);
        addSub("atlantic cod", "alaska pollock", 0.8);
        addSub("alaska pollock", "atlantic cod", 0.8);
        addSub("atlantic cod", "hake", 0.8);
        addSub("hake", "atlantic cod", 0.8);
        addSub("haddock", "alaska pollock", 0.8);
        addSub("alaska pollock", "haddock", 0.8);
        addSub("haddock", "hake", 0.8);
        addSub("hake", "haddock", 0.8);
        addSub("atlantic cod", "whiting", 0.8);
        addSub("whiting", "atlantic cod", 0.8);
        addSub("flounder", "sole", 0.9);
        addSub("sole", "flounder", 0.9);
        addSub("flounder", "european plaice", 0.9);
        addSub("european plaice", "flounder", 0.9);
        addSub("halibut", "flounder", 0.8);
        addSub("flounder", "halibut", 0.8);
        addSub("tilapia", "pangas", 0.9);
        addSub("pangas", "tilapia", 0.9);
        addSub("tilapia", "white fish", 0.8);
        addSub("white fish", "tilapia", 0.8);
        addSub("salmon", "trout", 0.8);
        addSub("trout", "salmon", 0.8);
        addSub("atlantic salmon", "wild salmon", 0.9);
        addSub("wild salmon", "atlantic salmon", 0.9);
        addSub("salmon", "farmed rainbow trout", 0.7);
        addSub("farmed rainbow trout", "salmon", 0.7);
        addSub("tuna", "albacore", 0.9);
        addSub("albacore", "tuna", 0.9);
        addSub("yellowfin tuna", "bigeye tuna", 0.9);
        addSub("bigeye tuna", "yellowfin tuna", 0.9);
        addSub("tuna steak", "swordfish", 0.8);
        addSub("swordfish", "tuna steak", 0.8);
        addSub("atlantic mackerel", "chub mackerel", 0.9);
        addSub("chub mackerel", "atlantic mackerel", 0.9);
        addSub("herring", "sardine", 0.8);
        addSub("sardine", "herring", 0.8);
        addSub("anchovy", "european anchovy", 0.9);
        addSub("european anchovy", "anchovy", 0.9);
        addSub("canned tuna", "tuna in brine", 0.9);
        addSub("tuna in brine", "canned tuna", 0.9);
        addSub("canned tuna", "tuna in oil", 0.8);
        addSub("tuna in oil", "canned tuna", 0.8);
        addSub("shrimp", "crayfish", 0.8);
        addSub("crayfish", "shrimp", 0.8);
        addSub("lobster", "american lobster", 0.9);
        addSub("american lobster", "lobster", 0.9);
        addSub("lobster", "spiny lobster", 0.9);
        addSub("spiny lobster", "lobster", 0.9);
        addSub("lobster", "norway lobster", 0.8);
        addSub("norway lobster", "lobster", 0.8);
        addSub("crab", "surimi", 0.7);
        addSub("surimi", "crab", 0.7);
        addSub("scallop", "atlantic bay scallop", 0.9);
        addSub("atlantic bay scallop", "scallop", 0.9);
        addSub("scallop", "queen scallop", 0.9);
        addSub("queen scallop", "scallop", 0.9);
        addSub("squid", "common cuttlefish", 0.8);
        addSub("common cuttlefish", "squid", 0.8);
        addSub("squid", "common octopus", 0.7);
        addSub("common octopus", "squid", 0.7);
        addSub("clam", "cockles", 0.8);
        addSub("cockles", "clam", 0.8);
        addSub("mussel", "blue mussel", 0.9);
        addSub("blue mussel", "mussel", 0.9);
        addSub("egg", "chicken egg", 0.9);
        addSub("chicken egg", "egg", 0.9);
        addSub("free range eggs", "barn eggs", 0.9);
        addSub("barn eggs", "free range eggs", 0.9);
        addSub("boiled egg", "hard-boiled egg", 0.9);
        addSub("hard-boiled egg", "boiled egg", 0.9);
        addSub("liquid whole egg", "egg", 0.8);
        addSub("egg", "liquid whole egg", 0.8);
        addSub("quail eggs", "chicken egg", 0.7);
        addSub("chicken egg", "quail eggs", 0.7);
        addSub("duck egg", "chicken egg", 0.8);
        addSub("chicken egg", "duck egg", 0.8);
        addSub("tofu", "tempeh", 0.7);
        addSub("tempeh", "tofu", 0.7);
        addSub("tofu", "seitan", 0.6);
        addSub("seitan", "tofu", 0.6);
        addSub("chicken breast", "tofu", 0.7);
        addSub("tofu", "chicken breast", 0.6);
        addSub("minced beef", "textured soy protein", 0.8);
        addSub("textured soy protein", "minced beef", 0.6);
        addSub("paneer", "tofu", 0.7);
        addSub("tofu", "paneer", 0.7);
        addSub("chorizo", "merguez", 0.7);
        addSub("merguez", "chorizo", 0.7);
        addSub("dry-cured ham", "dry-cured parma ham", 0.9);
        addSub("dry-cured parma ham", "dry-cured ham", 0.9);
        addSub("dry-cured parma ham", "dry-cured serrano ham", 0.9);
        addSub("dry-cured serrano ham", "dry-cured parma ham", 0.9);
        addSub("bresaola", "dried beef preparation", 0.9);
        addSub("dried beef preparation", "bresaola", 0.9);
        addSub("salami", "cured sausage", 0.9);
        addSub("cured sausage", "salami", 0.9);
        addSub("frankfurter sausages", "sausage", 0.8);
        addSub("sausage", "frankfurter sausages", 0.8);
        addSub("ricotta", "quark", 0.8);
        addSub("quark", "ricotta", 0.8);

        addSub("arborio rice", "carnaroli rice", 0.9);
        addSub("carnaroli rice", "arborio rice", 0.9);
        addSub("arborio rice", "risotto rice", 0.9);
        addSub("risotto rice", "arborio rice", 0.9);
        addSub("carnaroli rice", "risotto rice", 0.9);
        addSub("risotto rice", "carnaroli rice", 0.9);
        addSub("basmati rice", "thai long grain rice", 0.8);
        addSub("thai long grain rice", "basmati rice", 0.8);
        addSub("basmati rice", "long grain white rice", 0.8);
        addSub("long grain white rice", "basmati rice", 0.8);
        addSub("white rice", "long grain white rice", 0.9);
        addSub("long grain white rice", "white rice", 0.9);
        addSub("brown rice", "whole grain rice", 0.9);
        addSub("whole grain rice", "brown rice", 0.9);
        addSub("sushi rice", "round rice", 0.8);
        addSub("round rice", "sushi rice", 0.8);
        addSub("corn starch", "potato starch", 0.9);
        addSub("potato starch", "corn starch", 0.9);
        addSub("corn starch", "arrowroot", 0.9);
        addSub("arrowroot", "corn starch", 0.9);
        addSub("potato starch", "arrowroot", 0.9);
        addSub("arrowroot", "potato starch", 0.9);
        addSub("corn starch", "tapioca starch", 0.8);
        addSub("tapioca starch", "corn starch", 0.8);
        addSub("tapioca starch", "tapioca flour", 0.9);
        addSub("tapioca flour", "tapioca starch", 0.9);
        addSub("tapioca flour", "cassava flour", 0.8);
        addSub("cassava flour", "tapioca flour", 0.8);
        addSub("macaroni", "penne", 0.9);
        addSub("penne", "macaroni", 0.9);
        addSub("spaghetti", "linguine", 0.9);
        addSub("linguine", "spaghetti", 0.9);
        addSub("tagliatelle", "linguine", 0.8);
        addSub("linguine", "tagliatelle", 0.8);
        addSub("glass noodle", "vermicelli", 0.8);
        addSub("vermicelli", "glass noodle", 0.8);
        addSub("glass noodle", "rice noodles", 0.7);
        addSub("rice noodles", "glass noodle", 0.7);
        addSub("soba", "udon", 0.6);
        addSub("udon", "soba", 0.6);
        addSub("wheat flour", "refined wheat flour", 0.9);
        addSub("refined wheat flour", "wheat flour", 0.9);
        addSub("wheat flour", "soft wheat flour", 0.9);
        addSub("soft wheat flour", "wheat flour", 0.9);
        addSub("whole wheat flour", "graham flour", 0.9);
        addSub("graham flour", "whole wheat flour", 0.9);
        addSub("whole wheat flour", "wholemeal flour", 0.9);
        addSub("wholemeal flour", "whole wheat flour", 0.9);
        addSub("wheat flour", "spelt flour", 0.8);
        addSub("spelt flour", "wheat flour", 0.8);
        addSub("semolina", "durum wheat semolina", 0.9);
        addSub("durum wheat semolina", "semolina", 0.9);
        addSub("almond flour", "chestnut flour", 0.6);
        addSub("chestnut flour", "almond flour", 0.6);
        addSub("cannellini beans", "white beans", 0.9);
        addSub("white beans", "cannellini beans", 0.9);
        addSub("pinto beans", "borlotti bean", 0.8);
        addSub("borlotti bean", "pinto beans", 0.8);
        addSub("black bean", "black turtle bean", 0.9);
        addSub("black turtle bean", "black bean", 0.9);
        addSub("brown lentils", "green lentils", 0.8);
        addSub("green lentils", "brown lentils", 0.8);
        addSub("red lentils", "yellow lentils", 0.9);
        addSub("yellow lentils", "red lentils", 0.9);
        addSub("chickpea", "pedrosillano chickpeas", 0.9);
        addSub("pedrosillano chickpeas", "chickpea", 0.9);
        addSub("adzuki bean", "red bean", 0.9);
        addSub("red bean", "adzuki bean", 0.9);
        addSub("split pea", "yellow split pea", 0.8);
        addSub("yellow split pea", "split pea", 0.8);
        addSub("quinoa", "white quinoa", 0.9);
        addSub("white quinoa", "quinoa", 0.9);
        addSub("quinoa", "red quinoa", 0.8);
        addSub("red quinoa", "quinoa", 0.8);
        addSub("bulgur", "couscous", 0.8);
        addSub("couscous", "bulgur", 0.8);
        addSub("buckwheat", "kasha", 0.9);
        addSub("kasha", "buckwheat", 0.9);
        addSub("sweet potato", "yam", 0.8);
        addSub("yam", "sweet potato", 0.8);
        addSub("potato", "russet potatoes", 0.9);
        addSub("russet potatoes", "potato", 0.9);
        addSub("mashed potato", "instant mashed potatoes", 0.7);
        addSub("instant mashed potatoes", "mashed potato", 0.6);
        addSub("breadcrumbs", "panko", 0.8);
        addSub("panko", "breadcrumbs", 0.8);
        addSub("breadcrumbs", "wheat breadcrumbs", 0.9);
        addSub("wheat breadcrumbs", "breadcrumbs", 0.9);
        addSub("pizza base", "pizza crust", 0.9);
        addSub("pizza crust", "pizza base", 0.9);
        addSub("pizza dough", "pizza base", 0.8);
        addSub("pizza base", "pizza dough", 0.8);
        addSub("tortilla", "wheat tortilla", 0.8);
        addSub("wheat tortilla", "tortilla", 0.8);
        addSub("tortilla", "corn tortilla", 0.7);
        addSub("corn tortilla", "tortilla", 0.7);

        addSub("butter", "unsalted butter", 0.9);
        addSub("unsalted butter", "butter", 0.8);
        addSub("butter", "clarified butter", 0.8);
        addSub("clarified butter", "butter", 0.8);
        addSub("butter", "churned butter", 0.9);
        addSub("churned butter", "butter", 0.9);
        addSub("milk", "whole milk", 0.9);
        addSub("whole milk", "milk", 0.9);
        addSub("milk", "semi-skimmed milk", 0.8);
        addSub("semi-skimmed milk", "milk", 0.8);
        addSub("milk", "skimmed milk", 0.7);
        addSub("skimmed milk", "milk", 0.7);
        addSub("whole milk", "semi-skimmed milk", 0.8);
        addSub("semi-skimmed milk", "whole milk", 0.8);
        addSub("milk", "oat milk", 0.6);
        addSub("oat milk", "milk", 0.6);
        addSub("cream", "heavy cream", 0.8);
        addSub("heavy cream", "cream", 0.8);
        addSub("heavy cream", "whipping cream", 0.9);
        addSub("whipping cream", "heavy cream", 0.9);
        addSub("heavy cream", "double cream", 0.9);
        addSub("double cream", "heavy cream", 0.9);
        addSub("sour cream", "crème fraîche", 0.9);
        addSub("crème fraîche", "sour cream", 0.9);
        addSub("sour cream", "smetana", 0.9);
        addSub("smetana", "sour cream", 0.9);
        addSub("crème fraîche", "smetana", 0.9);
        addSub("smetana", "crème fraîche", 0.9);
        addSub("yogurt", "plain yogurt", 0.9);
        addSub("plain yogurt", "yogurt", 0.9);
        addSub("yogurt", "greek yogurt", 0.8);
        addSub("greek yogurt", "yogurt", 0.8);
        addSub("greek yogurt", "plain greek-style yogurt", 0.9);
        addSub("plain greek-style yogurt", "greek yogurt", 0.9);
        addSub("greek yogurt", "skyr", 0.8);
        addSub("skyr", "greek yogurt", 0.8);
        addSub("sour cream", "plain yogurt", 0.7);
        addSub("plain yogurt", "sour cream", 0.7);
        addSub("brie", "camembert", 0.8);
        addSub("camembert", "brie", 0.8);
        addSub("cheddar", "colby cheese", 0.8);
        addSub("colby cheese", "cheddar", 0.8);
        addSub("cheddar", "monterey jack", 0.8);
        addSub("monterey jack", "cheddar", 0.8);
        addSub("gruyère", "emmental", 0.8);
        addSub("emmental", "gruyère", 0.8);
        addSub("gruyère", "comté", 0.8);
        addSub("comté", "gruyère", 0.8);
        addSub("emmental", "comté", 0.7);
        addSub("comté", "emmental", 0.7);
        addSub("mozzarella", "buffalo mozzarella", 0.8);
        addSub("buffalo mozzarella", "mozzarella", 0.8);
        addSub("mozzarella", "low moisture mozzarella", 0.8);
        addSub("low moisture mozzarella", "mozzarella", 0.8);
        addSub("blue cheese", "gorgonzola", 0.9);
        addSub("gorgonzola", "blue cheese", 0.9);
        addSub("blue cheese", "bleu d'auvergne", 0.9);
        addSub("bleu d'auvergne", "blue cheese", 0.9);
        addSub("blue cheese", "blue stilton", 0.8);
        addSub("blue stilton", "blue cheese", 0.8);
        addSub("mascarpone", "cream cheese", 0.8);
        addSub("cream cheese", "mascarpone", 0.8);
        addSub("feta", "halloumi", 0.6);
        addSub("halloumi", "feta", 0.6);

        addSub("onion", "red onion", 0.9);
        addSub("red onion", "onion", 0.9);
        addSub("onion", "white onion", 0.9);
        addSub("white onion", "onion", 0.9);
        addSub("onion", "yellow onion", 0.9);
        addSub("yellow onion", "onion", 0.9);
        addSub("onion", "shallot", 0.8);
        addSub("shallot", "onion", 0.8);
        addSub("shallot", "red shallot", 0.9);
        addSub("red shallot", "shallot", 0.9);
        addSub("spring onion", "welsh onion", 0.9);
        addSub("welsh onion", "spring onion", 0.9);
        addSub("spring onion", "leek", 0.7);
        addSub("leek", "spring onion", 0.7);
        addSub("garlic", "fresh garlic", 0.9);
        addSub("fresh garlic", "garlic", 0.9);
        addSub("tomato", "cherry tomato", 0.8);
        addSub("cherry tomato", "tomato", 0.8);
        addSub("tomato", "organic tomato", 0.9);
        addSub("organic tomato", "tomato", 0.9);
        addSub("crushed tomato", "finely crushed tomato", 0.9);
        addSub("finely crushed tomato", "crushed tomato", 0.9);
        addSub("crushed tomato", "chopped tomato", 0.9);
        addSub("chopped tomato", "crushed tomato", 0.9);
        addSub("tomato purée", "tomato concentrate", 0.9);
        addSub("tomato concentrate", "tomato purée", 0.9);
        addSub("peeled tomatoes", "crushed peeled tomato", 0.9);
        addSub("crushed peeled tomato", "peeled tomatoes", 0.9);
        addSub("bell pepper", "red bell pepper", 0.9);
        addSub("red bell pepper", "bell pepper", 0.9);
        addSub("bell pepper", "green bell pepper", 0.9);
        addSub("green bell pepper", "bell pepper", 0.9);
        addSub("bell pepper", "yellow bell pepper", 0.9);
        addSub("yellow bell pepper", "bell pepper", 0.9);
        addSub("red bell pepper", "yellow bell pepper", 0.8);
        addSub("yellow bell pepper", "red bell pepper", 0.8);
        addSub("jalapeno pepper", "green jalapeno peppers", 0.9);
        addSub("green jalapeno peppers", "jalapeno pepper", 0.9);
        addSub("jalapeno pepper", "hot jalapeno pepper", 0.9);
        addSub("hot jalapeno pepper", "jalapeno pepper", 0.9);
        addSub("poblano chili", "bell pepper", 0.7);
        addSub("bell pepper", "poblano chili", 0.7);
        addSub("mushroom", "white mushroom", 0.9);
        addSub("white mushroom", "mushroom", 0.9);
        addSub("mushroom", "field mushroom", 0.9);
        addSub("field mushroom", "mushroom", 0.9);
        addSub("mushroom", "cultivated mushroom", 0.9);
        addSub("cultivated mushroom", "mushroom", 0.9);
        addSub("white mushroom", "chestnut mushroom", 0.8);
        addSub("chestnut mushroom", "white mushroom", 0.8);
        addSub("white mushroom", "portobello", 0.8);
        addSub("portobello", "white mushroom", 0.8);
        addSub("cep", "boletus", 0.9);
        addSub("boletus", "cep", 0.9);
        addSub("shiitake", "oyster mushroom", 0.8);
        addSub("oyster mushroom", "shiitake", 0.8);
        addSub("truffle", "black truffle", 0.9);
        addSub("black truffle", "truffle", 0.9);
        addSub("truffle", "summer truffle", 0.9);
        addSub("summer truffle", "truffle", 0.9);
        addSub("spinach", "baby spinach", 0.9);
        addSub("baby spinach", "spinach", 0.9);
        addSub("spinach", "chard", 0.8);
        addSub("chard", "spinach", 0.8);
        addSub("kale", "baby kale", 0.9);
        addSub("baby kale", "kale", 0.9);
        addSub("kale", "collard greens", 0.8);
        addSub("collard greens", "kale", 0.8);
        addSub("rocket", "baby arugula", 0.9);
        addSub("baby arugula", "rocket", 0.9);
        addSub("rocket", "wild rocket", 0.9);
        addSub("wild rocket", "rocket", 0.9);
        addSub("lettuce", "iceberg lettuce", 0.9);
        addSub("iceberg lettuce", "lettuce", 0.9);
        addSub("lettuce", "romaine lettuce", 0.9);
        addSub("romaine lettuce", "lettuce", 0.9);
        addSub("lettuce", "batavia", 0.8);
        addSub("batavia", "lettuce", 0.8);
        addSub("cabbage", "white cabbage", 0.9);
        addSub("white cabbage", "cabbage", 0.9);
        addSub("cabbage", "red cabbage", 0.7);
        addSub("red cabbage", "cabbage", 0.7);
        addSub("white cabbage", "pointed cabbage", 0.9);
        addSub("pointed cabbage", "white cabbage", 0.9);
        addSub("chinese cabbage", "pak choi", 0.8);
        addSub("pak choi", "chinese cabbage", 0.8);
        addSub("cabbage", "savoy cabbage", 0.8);
        addSub("savoy cabbage", "cabbage", 0.8);
        addSub("carrot", "baby carrots", 0.9);
        addSub("baby carrots", "carrot", 0.9);
        addSub("carrot", "yellow carrot", 0.8);
        addSub("yellow carrot", "carrot", 0.8);
        addSub("carrot", "parsnip", 0.7);
        addSub("parsnip", "carrot", 0.7);
        addSub("turnip", "rutabaga", 0.8);
        addSub("rutabaga", "turnip", 0.8);
        addSub("radish", "red radish", 0.9);
        addSub("red radish", "radish", 0.9);
        addSub("radish", "daikon", 0.8);
        addSub("daikon", "radish", 0.8);
        addSub("beetroot", "red beetroot", 0.9);
        addSub("red beetroot", "beetroot", 0.9);
        addSub("butternut squash", "acorn squash", 0.9);
        addSub("acorn squash", "butternut squash", 0.9);
        addSub("courgette", "giant squash", 0.7);
        addSub("cucumber", "baby cucumber", 0.9);
        addSub("baby cucumber", "cucumber", 0.9);
        addSub("pickled cucumber", "pickled gherkin", 0.9);
        addSub("pickled gherkin", "pickled cucumber", 0.9);
        addSub("vinegar cucumbers", "vinegar gherkin", 0.9);
        addSub("vinegar gherkin", "vinegar cucumbers", 0.9);
        addSub("cucumber", "gherkin", 0.6);
        addSub("broccoli", "cauliflower", 0.7);
        addSub("cauliflower", "broccoli", 0.7);
        addSub("broccoli", "romanesco broccoli", 0.9);
        addSub("romanesco broccoli", "broccoli", 0.9);
        addSub("cauliflower", "romanesco broccoli", 0.8);
        addSub("romanesco broccoli", "cauliflower", 0.8);
        addSub("green bean", "extra-fine green beans", 0.9);
        addSub("extra-fine green beans", "green bean", 0.9);
        addSub("green bean", "round green beans", 0.9);
        addSub("round green beans", "green bean", 0.9);
        addSub("green bean", "flat bean", 0.8);
        addSub("flat bean", "green bean", 0.8);
        addSub("green bean", "snow pea", 0.7);
        addSub("snow pea", "green bean", 0.7);
        addSub("green peas", "garden peas", 0.9);
        addSub("garden peas", "green peas", 0.9);
        addSub("garden peas", "extra fine garden peas", 0.9);
        addSub("extra fine garden peas", "garden peas", 0.9);
        addSub("seaweed", "wakame", 0.8);
        addSub("wakame", "seaweed", 0.8);
        addSub("seaweed", "kombu", 0.8);
        addSub("kombu", "seaweed", 0.8);
        addSub("seaweed", "nori", 0.8);
        addSub("nori", "seaweed", 0.8);
        addSub("potato", "new potato", 0.9);
        addSub("new potato", "potato", 0.9);
        addSub("asparagus", "green asparagus", 0.9);
        addSub("green asparagus", "asparagus", 0.9);
        addSub("asparagus", "white asparagus", 0.8);
        addSub("white asparagus", "asparagus", 0.8);
        addSub("green asparagus", "white asparagus", 0.7);
        addSub("white asparagus", "green asparagus", 0.7);
        addSub("olive", "green olive", 0.9);
        addSub("green olive", "olive", 0.9);
        addSub("olive", "pitted olive", 0.9);
        addSub("pitted olive", "olive", 0.9);
        addSub("green olive", "pitted green olives", 0.9);
        addSub("pitted green olives", "green olive", 0.9);
        addSub("kalamata olive", "kalamon olive", 0.9);
        addSub("kalamon olive", "kalamata olive", 0.9);
        addSub("butter", "ghee", 0.9);
        addSub("ghee", "butter", 0.9);
        addSub("butter", "margarine", 0.7);
        addSub("margarine", "butter", 0.7);
        addSub("olive oil", "extra virgin olive oil", 0.9);
        addSub("extra virgin olive oil", "olive oil", 0.9);
        addSub("olive oil", "refined olive oil", 0.8);
        addSub("refined olive oil", "olive oil", 0.8);
        addSub("sunflower oil", "refined sunflower oil", 0.9);
        addSub("refined sunflower oil", "sunflower oil", 0.9);
        addSub("canola oil", "rapeseed oil", 0.9);
        addSub("rapeseed oil", "canola oil", 0.9);
        addSub("coconut oil", "extra virgin coconut oil", 0.9);
        addSub("extra virgin coconut oil", "coconut oil", 0.9);
        addSub("sesame oil", "toasted sesame oil", 0.8);
        addSub("toasted sesame oil", "sesame oil", 0.8);
        addSub("lard", "pork fat", 0.9);
        addSub("pork fat", "lard", 0.9);
        addSub("duck fat", "goose fat", 0.9);
        addSub("goose fat", "duck fat", 0.9);
        addSub("peanut butter", "tahini", 0.7);
        addSub("tahini", "peanut butter", 0.7);
        addSub("almond oil", "hazelnut oil", 0.8);
        addSub("hazelnut oil", "almond oil", 0.8);
        addSub("salt", "sea salt", 0.9);
        addSub("sea salt", "salt", 0.9);
        addSub("salt", "fleur de sel", 0.8);
        addSub("fleur de sel", "salt", 0.8);
        addSub("salt", "kosher salt", 0.9);
        addSub("kosher salt", "salt", 0.9);
        addSub("black pepper", "white pepper", 0.8);
        addSub("white pepper", "black pepper", 0.8);
        addSub("black pepper", "peppercorns", 0.9);
        addSub("peppercorns", "black pepper", 0.9);
        addSub("cinnamon", "cinnamon powder", 0.9);
        addSub("cinnamon powder", "cinnamon", 0.9);
        addSub("nutmeg", "nutmeg nut", 0.9);
        addSub("nutmeg nut", "nutmeg", 0.9);
        addSub("paprika", "paprika powder", 0.9);
        addSub("paprika powder", "paprika", 0.9);
        addSub("turmeric", "turmeric powder", 0.9);
        addSub("turmeric powder", "turmeric", 0.9);
        addSub("ginger", "ginger powder", 0.8);
        addSub("ginger powder", "ginger", 0.8);
        addSub("garlic", "garlic powder", 0.7);
        addSub("garlic powder", "garlic", 0.7);
        addSub("onion", "onion powder", 0.7);
        addSub("onion powder", "onion", 0.7);
        addSub("basil", "dried basil", 0.7);
        addSub("dried basil", "basil", 0.7);
        addSub("oregano", "dried oregano", 0.8);
        addSub("dried oregano", "oregano", 0.8);
        addSub("rosemary", "dried rosemary", 0.8);
        addSub("dried rosemary", "rosemary", 0.8);
        addSub("vanilla", "vanilla extract", 0.9);
        addSub("vanilla extract", "vanilla", 0.9);
        addSub("vanilla", "vanilla powder", 0.9);
        addSub("vanilla powder", "vanilla", 0.9);
        addSub("sugar", "white sugar", 0.9);
        addSub("white sugar", "sugar", 0.9);
        addSub("sugar", "cane sugar", 0.9);
        addSub("cane sugar", "sugar", 0.9);
        addSub("sugar", "brown sugar", 0.8);
        addSub("brown sugar", "sugar", 0.8);
        addSub("brown sugar", "demerara sugar", 0.9);
        addSub("demerara sugar", "brown sugar", 0.9);
        addSub("honey", "acacia honey", 0.8);
        addSub("acacia honey", "honey", 0.8);
        addSub("honey", "manuka honey", 0.8);
        addSub("manuka honey", "honey", 0.8);
        addSub("maple syrup", "agave syrup", 0.8);
        addSub("agave syrup", "maple syrup", 0.8);
        addSub("vinegar", "white vinegar", 0.9);
        addSub("white vinegar", "vinegar", 0.9);
        addSub("vinegar", "apple cider vinegar", 0.8);
        addSub("apple cider vinegar", "vinegar", 0.8);
        addSub("balsamic vinegar", "white balsamic vinegar", 0.9);
        addSub("white balsamic vinegar", "balsamic vinegar", 0.9);
        addSub("soy sauce", "tamari", 0.9);
        addSub("tamari", "soy sauce", 0.9);
        addSub("soy sauce", "dark soy sauce", 0.8);
        addSub("dark soy sauce", "soy sauce", 0.8);
        addSub("chicken broth", "poultry broth", 0.9);
        addSub("poultry broth", "chicken broth", 0.9);
        addSub("beef broth", "beef stock", 0.9);
        addSub("beef stock", "beef broth", 0.9);
        addSub("chocolate", "dark chocolate", 0.8);
        addSub("dark chocolate", "chocolate", 0.8);
        addSub("milk chocolate", "chocolate", 0.8);
        addSub("chocolate", "milk chocolate", 0.8);
        addSub("coffee", "instant coffee", 0.7);
        addSub("instant coffee", "coffee", 0.7);
        addSub("tea", "black tea", 0.9);
        addSub("black tea", "tea", 0.9);
        addSub("tea", "green tea", 0.9);
        addSub("green tea", "tea", 0.9);
        addSub("cream", "milk", 0.7);
        addSub("milk", "cream", 0.5);
        addSub("cream", "yogurt", 0.6);
        addSub("yogurt", "cream", 0.5);
        addSub("heavy cream", "milk", 0.6);
        addSub("milk", "heavy cream", 0.5);
        
    }

    /**
     * Método auxiliar privado para añadir sustituciones de forma más limpia en el código.
     */
    private static void addSub(String original, String substitute, double score) {
        SUBSTITUTIONS.add(new SubstitutionRule(original, substitute, score));
    }

    // Devuelve la categoría de un ingrediente
    public static FoodCategory getCategory(String ingredient) {
        return CATEGORIES.getOrDefault(
                ingredient.toLowerCase().trim(), FoodCategory.OTHER);
    }

    // Dados los ingredientes del usuario, devuelve sustitutos posibles
    // para ingredientes que faltan en una receta
    public static List<String> findSubstitutes(String missingIngredient, List<String> userIngredients) {
        List<String> result = new ArrayList<>();
        for (SubstitutionRule rule : SUBSTITUTIONS) {
            if (rule.getOriginal().equalsIgnoreCase(missingIngredient)) {
                if (userIngredients.contains(rule.getSubstitute())) {
                    result.add(rule.getSubstitute());
                }
            }
        }
        return result;
    }

    // Devuelve todas las sustituciones posibles de un ingrediente
    public static List<SubstitutionRule> getSubstitutionRules(String ingredient) {
        List<SubstitutionRule> result = new ArrayList<>();
        for (SubstitutionRule rule : SUBSTITUTIONS) {
            if (rule.getOriginal().equalsIgnoreCase(ingredient)) {
                result.add(rule);
            }
        }
        return result;
    }
    public static List<String> getIngredients() {
        List<String> list = new ArrayList<>(CATEGORIES.keySet());
        Collections.sort(list);
        return list;
    }
}