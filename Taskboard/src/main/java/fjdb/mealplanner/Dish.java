package fjdb.mealplanner;

import fjdb.databases.DataItemIF;

import java.io.Serializable;

//TODO I think we should have a Dish and DishBean, where the DishBean contains the DishId (or is mapped to it in the dao),
//and a Dish can be equated to it.
public class Dish implements DataItemIF, Comparable<Dish>, Serializable {
    private static final long serialVersionUID = 20210720L;

    private final DishId dishId;
    private final String name;
    private final String details;

    public Dish(DishId dishId, String name, String details) {
        this.dishId = dishId;
        this.name = name;
        this.details = details;
    }

    public Dish(String name, String description) {
        this(DishId.STUB, name, description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return toString();
    }

    public String getDetails() {
        return details;
    }

    @Override
    public DishId getId() {
        return dishId;
    }

    @Override
    public String toString() {
        String details = getDetails();
        if (details.isEmpty()) {
            return String.format("%s", getName());
        } else {
            return String.format("%s: %s", getName(), details);
        }
    }

    @Override
    public int compareTo(Dish o) {
        int result = getName().compareTo(o.getName());
        if (result == 0) {
            result = getDescription().compareTo(o.getDescription());
        }
        return result;
    }
}
