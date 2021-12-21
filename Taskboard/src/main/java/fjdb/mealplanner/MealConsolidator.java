package fjdb.mealplanner;

import fjdb.mealplanner.loaders.CompositeDishLoader;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MealConsolidator {

    private final MealPlanManager mealPlanManager;
    private boolean attemptRepair = false;

    public static void main(String[] args) {
        MealConsolidator mealConsolidator = new MealConsolidator();
        mealConsolidator.attemptRepair = true;
        mealConsolidator.run();


    }

    public MealConsolidator() {
        String currentUsersHomeDir = System.getProperty("user.home");
        File mealPlansFolder = new File(currentUsersHomeDir, "MealPlans");
        mealPlanManager = new MealPlanManager(mealPlansFolder);
        mealPlanManager.load();
    }

    public void run() {
        List<MealPlan> mealPlans = mealPlanManager.getAllMealPlans(true);

        for (MealPlan mealPlan : mealPlans) {
            parse(mealPlan);
            break;
        }

        System.out.println("Done");

    }

    private void parse(MealPlan mealPlan) {

        MealPlanBuilder builder = new MealPlanBuilder(mealPlan);
        boolean repairDone = false;

        List<LocalDate> dates = mealPlan.getDates();
        for (LocalDate date : dates) {

            DayPlanIF plan = mealPlan.getPlan(date);
            Map<MealType, Meal> meals = plan.getMealsByType();
            for (Map.Entry<MealType, Meal> mealEntry : meals.entrySet()) {
                MealType type = mealEntry.getKey();
                Meal meal = mealEntry.getValue();
                if (!Meal.isStub(meal)) {
                    Dish dish = meal.getDish();
                    if (Dish.isStub(dish)) {
                        System.out.printf("%s %s %s%n", date, dish, meal.getDescription());
                        if (attemptRepair) {
                            repairDone = repairPlan(builder, date, type, meal) || repairDone;
                        }
                    } else {
                        //Not stub, so it's fine...I think

                    }
                }

            }

        }

        if (repairDone) {
            mealPlanManager.addMealPlan(builder.makePlan());
        }
        System.out.printf("Finished plan starting on %s%n", mealPlan.getStart());
    }

    private boolean repairPlan(MealPlanBuilder builder, LocalDate date, MealType type, Meal meal) {
        //TODO attempt to find matching dish in the meal,
        List<Dish> dishes = new CompositeDishLoader(DaoManager.PRODUCTION).getDishes();


        return false;
    }
}
