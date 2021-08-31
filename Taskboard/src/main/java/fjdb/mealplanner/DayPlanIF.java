package fjdb.mealplanner;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public interface DayPlanIF extends Serializable {
    String getToCook();

    String getUnfreeze();

    Meal getBreakfast();

    Meal getLunch();

    Meal getDinner();

    default
    List<Meal> getMeals() {
        return Lists.newArrayList(getBreakfast(), getLunch(), getDinner());
    }
}
