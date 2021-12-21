package fjdb.mealplanner.fx;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.MealType;

import java.time.LocalDate;

public interface MealPlanProxy {

    LocalDate getStart();
    void addDishToHolder(Dish dish);
    void addDish(Dish dish, LocalDate date, MealType type);

}
