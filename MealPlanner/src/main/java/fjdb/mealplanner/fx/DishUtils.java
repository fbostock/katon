package fjdb.mealplanner.fx;

import com.google.common.collect.Lists;
import fjdb.mealplanner.Dish;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DishUtils {

    public static List<Dish> getDishMatches(String dishDetails, List<Dish> dishes) {
        Map<String, Dish> map = dishes.stream().collect(Collectors.toMap(dish -> dish.getName().toLowerCase(), d -> d));
        return getDishMatches(dishDetails, map);
    }

    public static List<Dish> getDishMatches(String dishDeails, Map<String, Dish> dishes) {
        List<Dish> candidates = Lists.newArrayList();
        String searchString = dishDeails.toLowerCase();
        for (String dishName : dishes.keySet()) {
            if (searchString.contains(dishName)) {
                candidates.add(dishes.get(dishName));
            }
        }
        return candidates;
    }
}
