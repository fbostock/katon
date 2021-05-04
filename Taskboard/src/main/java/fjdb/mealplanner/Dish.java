package fjdb.mealplanner;

public class Dish {
    private final String name;
    private final String description;

    public Dish(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Copy constructor
     * @param oldDish
     */
    public Dish(Dish oldDish) {
        this.name = oldDish.name;
        this.description = oldDish.description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
