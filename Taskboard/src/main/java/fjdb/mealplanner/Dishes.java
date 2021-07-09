package fjdb.mealplanner;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A static source of pre-defined dish objects
 */
public class Dishes {

    //TODO to be replaced with a pool (need to build a pool)
    private static final Map<Dish, Dish> global = new ConcurrentHashMap<>();

    //Breakfasts
    public static final Dish PANCAKES = of("Pancakes", "");
    public static final Dish POACHED = of("Poached Eggs", "");
    public static final Dish SCRAMBLED = of("Scrambled Eggs", "");
    public static final Dish FRYUP = of("Fry up", "");
    //TODO a soup should be a DishWithOptions
    public static final Dish SOUP = of("Soup", "");
    public static final Dish HUMMUS_WRAP = of("Hummus wrap", "");

    //Lunches
    public static final Dish POACHED_EGGS = of("Poached eggs", "with smoked salmon, hollandaise");

    //Dinners
    public static final Dish PAELLA = of("Paella", "");

    public static final Dish BANGERS = of("Bangers & Mash", "");

    public static final Dish BURGERS = of("Burgers", "");

    public static final Dish CHILLI = of("Chilli", "Chilli with rice");
    public static final Dish FISH_PIE = of("Fish Pie", "");
    public static final Dish LASAGNE = of("Lasagne", "Meat lasagne");
    public static final Dish VEG_LASAGNE = of("Lasagne Veg", "Vegetable lasagne");
    public static final Dish MOUSAKKA = of("Mousakka", "");
    public static final Dish MEGA_MAC = of("Mega mac", "Bacon, cauliflower");
    public static final Dish RISOTTO = of("Risotto", "");



    //General dishes, e.g. items which require further definition
    public static final Dish BBQ = of("BBQ", "");
    public static final Dish ROAST = of("ROAST", "");
    public static final Dish PICNIC = of("Picnic", "");
    public static final Dish TAKEAWAY = of("Takeaway", "");

    public static Dish of(String name, String description) {
        Dish key = new Dish(name, description);
        global.put(key, key);
        return global.get(key);
    }

    public static List<Dish> getAll() {
        //TODO define an ordering for Dishes.
        return Lists.newArrayList(global.keySet());
    }
}
