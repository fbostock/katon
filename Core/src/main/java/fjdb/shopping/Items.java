package fjdb.shopping;

import fjdb.threading.LazyInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Items {

    private static final List<Item> allItems = new ArrayList<>();

    static {
        ofDepartment(Department.fruitVeg, "strawberries", "blueberries", "grapes", "bananas", "cherries", "apples",
                "pears", "lemons", "limes", "plums", "raspberries", "mango", "pineapple", "melon", "orange");
        ofDepartment(Department.fruitVeg, "tomatoes", "avocados", "peppers", "salad", "mushrooms", "asparagus", "mange tout");
        ofDepartment(Department.fruitVeg, "potatoes", "carrots","broccoli","cauliflower", "parsnips", "onions", "spinach", "courgette");
        ofDepartment(Department.meat, "sausages", "bacon", "chicken", "burgers", "steak", "gammon", "beef", "pork", "lamb");
        ofDepartment(Department.meat, "ribs", "black pudding");
        ofDepartment(Department.lunches, "sushi", "katsu curry", "curry", "garlic bread", "olives", "pork pie", "scotch egg",
        "sausage roll");

        ofDepartment(Department.dairy,  "cheese", "margarine", "iced coffee", "yoghurt", "pastry", "puddings");
        ofDepartment(Department.baby, "Elodie snacks", "baby snacks", "ella", "nappies", "swimming nappies", "banana pudding", "smoothie");
        ofDepartment(Department.milkJuiceSauces, "juice", "milk");
        ofDepartment(Department.pastaCansCondiments, "pasta", "rice", "spaghetti", "fusilli", "penne", "couscous", "chickpeas", "lentils", "");
        ofDepartment(Department.toiletries, "loo roll", "toilet roll", "shampoo", "conditioner", "shower gel", "bubble bath");
        ofDepartment(Department.biscuits, "hobnobs", "chocolate digestives", "ginger snaps", "cereal bars", "chocolate", "chocolate bars", "truffles", "sweets", "marshmallows");
        ofDepartment(Department.drinksCrisps, "crisps", "pringles", "coke", "coca cola", "soft drinks", "lemonade", "squash", "nuts");
        ofDepartment(Department.cereals, "cereal", "bran flakes", "weetabix", "porridge", "ready brek", "granola");
        ofDepartment(Department.teaCofee, "");
        ofDepartment(Department.bakingBread, "");
        ofDepartment(Department.frozen, "");
        ofDepartment(Department.booze, "");
        ofDepartment(Department.cleaning, "");

    }



    public static final Item eggs = of("eggs", Department.bakingBread);
//    public static final Item margarine = of("margarine", Department.dairy);
    public static final Item milk = of("milk", Department.milkJuiceSauces);
    public static final Item filler = of("filler", Department.miscellaneous);
//    public static final Item  = of("", Department.);


    public static Item of(String name, Department... departments) {
        Item item = Item.of(name, departments);
        allItems.add(item);
        return item;
    }

    private static void ofDepartment(Department department, String...names) {
        for (String name : names) {
            of(name, department);
        }
    }

    /*Lazy initializer to build an unmodifiable view of the item list. */
    private static LazyInitializer<List<Item>> everything = LazyInitializer.makeInitializer(() -> Collections.unmodifiableList(allItems));

    public static List<Item> getAllItems() {
        return everything.get();
    }
}
