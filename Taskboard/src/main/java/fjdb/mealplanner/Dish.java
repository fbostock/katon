package fjdb.mealplanner;

import fjdb.databases.DataItemIF;
import fjdb.mealplanner.swing.MealPlannerTest;

import java.io.Serializable;
import java.util.Objects;

//TODO I think we should have a Dish and DishBean, where the DishBean contains the DishId (or is mapped to it in the dao),
//and a Dish can be equated to it.
public class Dish implements DataItemIF, Comparable<Dish>, Serializable {
    private static final long serialVersionUID = 20210720L;

    private final String name;
    private final String details;

    public Dish(String name, String description) {
        this.name = name;
        this.details = description;
    }

    public static boolean isStub(Dish dish) {
        return MealPlannerTest.stub().equals(dish);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(name, dish.name) &&
                Objects.equals(details, dish.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, details);
    }
}
