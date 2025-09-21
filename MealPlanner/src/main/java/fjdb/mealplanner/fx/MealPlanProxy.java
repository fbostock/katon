package fjdb.mealplanner.fx;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.Meal;
import fjdb.mealplanner.MealType;

import java.time.LocalDate;

public interface MealPlanProxy {

    LocalDate getStart();
    void addDishToHolder(Dish dish);
    void addDish(Meal meal, LocalDate date, MealType type);

}
