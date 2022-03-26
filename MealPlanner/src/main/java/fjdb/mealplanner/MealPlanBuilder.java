package fjdb.mealplanner;

import com.google.common.collect.Sets;
import fjdb.util.ListUtil;
import fjdb.util.Pool;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MealPlanBuilder {

    private final Pool<LocalDate, MutableDayPlan> mealPlan = new Pool<>() {
        @Override
        public MutableDayPlan create(LocalDate key) {
            return new MutableDayPlan();
        }
    };
    private final Set<Dish> tempDishes = Sets.newTreeSet();
    private String notes = "";

    public MealPlanBuilder() {
    }

    public MealPlanBuilder(MealPlan plan) {
        List<LocalDate> dates = plan.getDates();
        for (LocalDate date : dates) {
            setDayPlan(date, plan.getPlan(date));
        }
        Set<Dish> tempDishes = plan.getTempDishes();
        this.tempDishes.addAll(tempDishes);
        setNotes(plan.getNotes());
    }

    private void setDayPlan(LocalDate date, DayPlanIF dayPlan) {
        setUnfreeze(date, dayPlan.getUnfreeze());
        setCook(date, dayPlan.getToCook());
        setBreakfast(date, dayPlan.getBreakfast());
        setLunch(date, dayPlan.getLunch());
        setDinner(date, dayPlan.getDinner());
    }

    private MutableDayPlan getPlan(LocalDate date) {
        return mealPlan.get(date);
    }

    public void setBreakfast(LocalDate date, Meal breakfast) {
        getPlan(date).breakfast = breakfast;
    }

    public List<LocalDate> getDates() {
        return mealPlan.getPool().keySet().stream().sorted().collect(Collectors.toList());
    }

    public LocalDate getStart() {
        return ListUtil.first(getDates());
    }

    public void remove(LocalDate date) {
        mealPlan.getPool().remove(date);
    }

    public void setLunch(LocalDate date, Meal lunch) {
        getPlan(date).lunch = lunch;
    }

    public void setDinner(LocalDate date, Meal dinner) {
        getPlan(date).dinner = dinner;
    }

    public void setMeal(LocalDate date, MealType type, Meal meal) {
        switch (type) {
            case BREAKFAST:
                setBreakfast(date, meal);
                break;
            case LUNCH:
                setLunch(date, meal);
                break;
            case DINNER:
                setDinner(date, meal);
                break;
        }
    }

    public void setUnfreeze(LocalDate date, String unfreeze) {
        getPlan(date).unfreeze = unfreeze;
    }

    public void addUnfreeze(LocalDate date, String toUnfreeze) {
        String currentUnfreeze = getPlan(date).unfreeze;
        currentUnfreeze += (currentUnfreeze.isBlank() ? " "  : "") + toUnfreeze.trim();
        setUnfreeze(date, currentUnfreeze);
    }

    public void setCook(LocalDate date, String toCook) {
        getPlan(date).toCook = toCook;
    }

    public void addCook(LocalDate date, String toCook) {
        String cook = getPlan(date).toCook;
        cook += (cook.isBlank() ? " "  : "") + toCook.trim();
        setCook(date, cook);
    }

    public DayPlanIF getDayPlan(LocalDate date) {
        return getPlan(date);
    }

    public void addTempDish(Dish dish) {
        tempDishes.add(dish);
    }

    public void removeTempDish(Dish dish) {
        tempDishes.remove(dish);
    }

    public void clearTempDishes() {
        tempDishes.clear();
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public Set<Dish> getTempDishes() {
        return tempDishes;
    }

    public MealPlan makePlan() {
        Map<LocalDate, MutableDayPlan> map = mealPlan.getPool();
        TreeSet<LocalDate> dates = new TreeSet<>(map.keySet());
        TreeMap<LocalDate, DayPlanIF> treeMap = new TreeMap<>();
        for (LocalDate date : dates) {
            treeMap.put(date, map.get(date).toDayPlan());
        }
        return new MealPlan(treeMap, Sets.newTreeSet(tempDishes), notes);
    }

    private static class MutableDayPlan implements DayPlanIF {

        private String toCook = "";
        private String unfreeze = "";
        private Meal breakfast = Meal.stub();
        private Meal lunch = Meal.stub();
        private Meal dinner = Meal.stub();

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
