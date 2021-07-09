package fjdb.mealplanner;

import com.google.common.collect.Lists;
import fjdb.util.ListUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class MealPlan {

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

    public void print() {
        for (LocalDate date : getDates()) {
            DayPlanIF plan = getPlan(date);
            System.out.printf("%s %s %s %s %s %s%n", date, plan.getUnfreeze(), plan.getToCook(), plan.getBreakfast(), plan.getLunch(), plan.getDinner());
        }
    }
}
