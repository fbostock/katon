package fjdb.mealplanner;

public class DayPlan implements DayPlanIF {

    private final String toCook;
    private final String unfreeze;
    private final Meal breakfast;
    private final Meal lunch;
    private final Meal dinner;

    public DayPlan(String toCook, String unfreeze, Meal breakfast, Meal lunch, Meal dinner) {
        this.toCook = toCook;
        this.unfreeze = unfreeze;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
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
}
