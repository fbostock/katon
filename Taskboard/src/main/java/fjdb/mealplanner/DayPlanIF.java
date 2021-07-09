package fjdb.mealplanner;

public interface DayPlanIF {
    String getToCook();

    String getUnfreeze();

    Meal getBreakfast();

    Meal getLunch();

    Meal getDinner();
}
