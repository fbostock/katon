package fjdb.mealplanner;

import fjdb.util.Pool;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class MealPlanBuilder {

    private final Pool<LocalDate, MutableDayPlan> mealPlan = new Pool<>() {
        @Override
        public MutableDayPlan create(LocalDate key) {
            return new MutableDayPlan();
        }
    };

    private MutableDayPlan getPlan(LocalDate date) {
        return mealPlan.get(date);
    }

    public void setBreakfast(LocalDate date, Meal breakfast) {
        getPlan(date).breakfast = breakfast;
    }

    public void setLunch(LocalDate date, Meal lunch) {
        getPlan(date).lunch = lunch;
    }

    public void setDinner(LocalDate date, Meal dinner) {
        getPlan(date).dinner = dinner;
    }

    public void setUnfreeze(LocalDate date, String unfreeze) {
        getPlan(date).unfreeze = unfreeze;
    }

    public void setCook(LocalDate date, String toCook) {
        getPlan(date).toCook = toCook;
    }

    public DayPlanIF getDayPlan(LocalDate date) {
        return getPlan(date);
    }

    public MealPlan makePlan() {
        Map<LocalDate, MutableDayPlan> map = mealPlan.getPool();
        TreeSet<LocalDate> dates = new TreeSet<>(map.keySet());
        TreeMap<LocalDate, DayPlanIF> treeMap = new TreeMap<>();
        for (LocalDate date : dates) {
            treeMap.put(date, map.get(date).toDayPlan());
        }
        return new MealPlan(treeMap);
    }

    private static class MutableDayPlan implements DayPlanIF {

        private  String toCook = "";
        private  String unfreeze = "";
        private  Meal breakfast = Meal.stub(MealType.BREAKFAST);
        private  Meal lunch = Meal.stub(MealType.LUNCH);
        private  Meal dinner = Meal.stub(MealType.DINNER);

        public MutableDayPlan() {

        }

        @Override
        public String getToCook() {
            return toCook;
        }

        @Override
        public String getUnfreeze() {
            return unfreeze;
        }

        @Override
        public Meal getBreakfast() {
            return breakfast;
        }

        @Override
        public Meal getLunch() {
            return lunch;
        }

        @Override
        public Meal getDinner() {
            return dinner;
        }

        public DayPlanIF toDayPlan() {
            return new DayPlan(getToCook(), getUnfreeze(), getBreakfast(), getLunch(), getDinner());
        }
    }
}
