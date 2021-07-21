package fjdb.mealplanner;

import java.io.Serializable;

public interface DayPlanIF extends Serializable {
    String getToCook();

    String getUnfreeze();

    Meal getBreakfast();

    Meal getLunch();

    Meal getDinner();
}
