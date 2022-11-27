package fjdb.mealplanner;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DayPlanIF extends Serializable {
    String getToCook();

    String getUnfreeze();

    Meal getBreakfast();

    Meal getLunch();

    Meal getDinner();

    default List<Meal> getMeals() {
        return Lists.newArrayList(getBreakfast(), getLunch(), getDinner());
    }

    default Map<MealType, Meal> getMealsByType() {
        HashMap<MealType, Meal> map = new HashMap<>();
        map.put(MealType.BREAKFAST, getBreakfast());
        map.put(MealType.LUNCH, getLunch());
        map.put(MealType.DINNER, getDinner());
        return map;
    }

    default List<Dish> getDishes() {
        return getMeals().stream().map(Meal::getDish).collect(Collectors.toList());
    }
}
