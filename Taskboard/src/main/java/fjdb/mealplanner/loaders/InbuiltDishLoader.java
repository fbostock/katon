package fjdb.mealplanner.loaders;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.Dishes;

import java.util.List;

public class InbuiltDishLoader implements DishLoader {

    public List<Dish> getDishes() {
        return Dishes.getAll();
    }
}
