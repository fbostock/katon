package fjdb.mealplanner;

public class Dish {
    private final String name;
    private final String description;

    public Dish(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
