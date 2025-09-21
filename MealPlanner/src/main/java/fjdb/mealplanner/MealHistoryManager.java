package fjdb.mealplanner;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;

import java.time.LocalDate;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class MealHistoryManager {

    private final TreeMultimap<Dish, LocalDate> dishHistories = TreeMultimap.create();

    public TreeSet<String> getAllDishes() {
        return allDishes;
    }

    private final TreeSet<String> allDishes = new TreeSet<>();

    private LocalDate currentDate;

    public MealHistoryManager(LocalDate currentDate, List<MealPlan> mealPlans) {
        this.currentDate = currentDate;
        for (MealPlan mealPlan : mealPlans) {
            loadMeals(mealPlan);
        }
    }


    private void loadMeals(MealPlan mealPlan) {
        if (mealPlan.getStart().isAfter(currentDate)) return;
        List<LocalDate> dates = mealPlan.getDates();
        for (LocalDate date : dates) {
            if (date.isBefore(currentDate)) {
                DayPlanIF plan = mealPlan.getPlan(date);
                populate(plan.getBreakfast(), date);
                populate(plan.getLunch(), date);
                populate(plan.getDinner(), date);
            } else {
                return;
            }
        }
    }

    private void populate(Meal meal, LocalDate date) {
        allDishes.add(meal.toString());
        allDishes.add(meal.getNotes());
        allDishes.add(meal.getDish().getName());
        allDishes.add(meal.getDish().getDescription());

        if (!Meal.isStub(meal)) {
            Dish dish = meal.getDish();
            if (!Dish.isStub(dish)) {
                dishHistories.put(dish, date);
            } else {
                int x = 0;
            }
        }
    }

    public List<LocalDate> getDates(Dish dish) {
        NavigableSet<LocalDate> dates = dishHistories.get(dish);
        return Lists.newArrayList(dates);
    }
}
