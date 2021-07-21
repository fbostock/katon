package fjdb.mealplanner;

import com.google.common.collect.ImmutableSet;
import fjdb.util.Pool;

import java.util.Objects;
import java.util.Set;

public class DishTag implements Comparable<DishTag> {
    //TODO A DishTag will be like "Lunch", "Breakfast", "Batch", "Vegan", "Meat-free", "Pasta"
    //Tags would be things you can search or categorise dishes by if you're trying to find something
    //suitable for a particular meal/occasion e.g. "we need a vegetarian dish", "a pasta dish", "a fish dish"
    private static final Pool<String, DishTag> tags = new Pool<>() {

        @Override
        public DishTag create(String key) {
            return new DishTag(key);
        }
    };

    public static final DishTag BREAKFAST = of("Breakfast");
    public static final DishTag LUNCH = of("Lunch");
    public static final DishTag DINNER = of("Dinner");
    public static final DishTag PASTA = of("Pasta");
    public static final DishTag VEGAN = of("Vegan");
    public static final DishTag MEATFREE = of("MeatFree");
    /*Dishes Ivy can eat*/
    public static final DishTag IVY = of("Ivy");
    /*Dishes Ivy really likes*/
    public static final DishTag IVY_FAVOURITE = of("Ivy Favourite");

    private final String label;

    public static Set<DishTag> getTags() {
        return ImmutableSet.copyOf(tags.getPool().values());
    }

    public static DishTag of(String label) {
        return tags.get(label);
    }

    private DishTag(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int compareTo(DishTag o) {
        return getLabel().compareTo(o.getLabel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishTag dishTag = (DishTag) o;
        return Objects.equals(label, dishTag.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
