package fjdb.mealplanner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fjdb.util.ListUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class MealPlan implements Serializable {
    private static final long serialVersionUID = 20210720L;

    public static final String DATE = "Date";
    public static final String UNFREEZE = "Unfreeze";
    public static final String COOK = "Cook";
    public static final String BREAKFAST = "Breakfast";
    public static final String LUNCH = "Lunch";
    public static final String DINNER = "Dinner";

    private final TreeMap<LocalDate, DayPlanIF> mealPlan;
    /*@Deprecated. Use tempMeals */
    private final Set<Dish> tempDishes = new HashSet<>();
    private Set<Meal> tempMeals = new HashSet<>();
    private final String notes;

    public MealPlan(TreeMap<LocalDate, DayPlanIF> mealPlan) {
        this(mealPlan, "", Sets.newHashSet());
    }

    public MealPlan(TreeMap<LocalDate, DayPlanIF> mealPlan, Set<Dish> tempDishes, String notes) {
        this.mealPlan = mealPlan;
        this.tempDishes.addAll(tempDishes);
        this.notes = notes;
    }

    public MealPlan(TreeMap<LocalDate, DayPlanIF> mealPlan, String notes, Set<Meal> tempMeals) {
        this.mealPlan = mealPlan;
        this.tempMeals.addAll(tempMeals);
        this.notes = notes;
    }

    public int getLength() {
        return mealPlan.size();
    }

    public List<LocalDate> getDates() {
        return Lists.newArrayList(new TreeSet<>(mealPlan.keySet()));
    }

    public LocalDate getStart() {
        return ListUtil.first(getDates());
    }

    public LocalDate getEnd() {
        return ListUtil.last(getDates());
    }

    public DayPlanIF getPlan(LocalDate date) {
        return mealPlan.get(date);
    }

    public String getName() {
        return String.format("Plan-%s-%s", getStart(), getEnd());
    }

    public Set<Meal> getTempDishes() {
        if (tempMeals == null) {
            tempMeals = new HashSet<>();
            tempDishes.forEach(d->tempMeals.add(new Meal(d, "")));
            System.out.println("TempMeals is null due to deserialized meal plan. Creating tempMeals from temp dishes (" + tempDishes.size() + ")");
        }

        return tempMeals;
    }

    public String getNotes() {
        return notes;
    }

    public void print() {
        for (LocalDate date : getDates()) {
            DayPlanIF plan = getPlan(date);
            System.out.printf("%s %s %s %s %s %s%n", date, plan.getUnfreeze(), plan.getToCook(), plan.getBreakfast(), plan.getLunch(), plan.getDinner());
        }
    }

    @Override
    public String toString() {
        return String.format("Plan from %s to %s", mealPlan.firstKey(), mealPlan.lastKey());
    }
}
