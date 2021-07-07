package fjdb.mealplanner;

import fjdb.databases.DataItemIF;

public class Dish implements DataItemIF {
    private DishId dishId;
    private final String name;
    private final String description;

    public Dish(DishId dishId, String name, String description) {
        this.dishId = dishId;
        this.name = name;
        this.description = description;
    }

    public Dish(String name, String description) {
        this(DishId.STUB, name, description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public DishId getId() {
        return dishId;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getName(), getDescription());
    }
}
