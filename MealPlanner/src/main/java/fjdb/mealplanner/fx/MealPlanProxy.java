package fjdb.mealplanner.fx;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.Meal;
import fjdb.mealplanner.MealType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MealPlanProxy {

    LocalDate getStart();
    void addDishToHolder(Meal dish);
    void addDish(Meal meal, LocalDate date, MealType type);
    Set<Meal> getRecentMeals();
}
