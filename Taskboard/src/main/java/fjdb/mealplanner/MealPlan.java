package fjdb.mealplanner;

import com.google.common.collect.Lists;
import fjdb.util.ListUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class MealPlan implements Serializable {
    private static final long serialVersionUID = 20210720L;

    public static final String DATE = "Date";
    public static final String UNFREEZE = "Unfreeze";
    public static final String COOK = "Cook";
    public static final String BREAKFAST = "Breakfast";
    public static final String LUNCH = "Lunch";
    public static final String DINNER = "Dinner";

    private final TreeMap<LocalDate, DayPlanIF> mealPlan;

    public MealPlan(TreeMap<LocalDate, DayPlanIF> mealPlan) {
        this.mealPlan = mealPlan;
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

    public void print() {
        for (LocalDate date : getDates()) {
            DayPlanIF plan = getPlan(date);
            System.out.printf("%s %s %s %s %s %s%n", date, plan.getUnfreeze(), plan.getToCook(), plan.getBreakfast(), plan.getLunch(), plan.getDinner());
        }
    }
}
