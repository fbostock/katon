package fjdb.mealplanner;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

public class MealPlanTemplates {

    public static MealPlan makePlanFromTemplate(MealPlan templatePlan, LocalDate dateStart) {
        List<LocalDate> dates = templatePlan.getDates();
        TreeMap<LocalDate, DayPlanIF> planMap = new TreeMap<>();

        for (LocalDate date : dates) {
            DayPlanIF plan = templatePlan.getPlan(date);
            planMap.put(dateStart, plan);
            dateStart = dateStart.plusDays(1);
        }
        return new MealPlan(planMap);
    }

    public static MealPlan makePlanFromTemplate(MealPlan templatePlan, LocalDate dateStart, int days) {
        List<LocalDate> dates = templatePlan.getDates();
        TreeMap<LocalDate, DayPlanIF> planMap = new TreeMap<>();
        int dayCount = 0;
        for (LocalDate date : dates) {
            DayPlanIF plan = templatePlan.getPlan(date);
            planMap.put(dateStart, plan);
            dateStart = dateStart.plusDays(1);
            if (dayCount >= days) break;
            dayCount++;
        }
        return new MealPlan(planMap);
    }
}
