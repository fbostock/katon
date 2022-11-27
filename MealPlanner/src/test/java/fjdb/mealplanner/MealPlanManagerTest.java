package fjdb.mealplanner;

import fjdb.mealplanner.swing.MealPlannerTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class MealPlanManagerTest {

    @Ignore
    @Test
    public void mealPlans_can_be_deserialized() throws URISyntaxException, IOException {
        //TODO, load file and deserialize
        URL resource = MealPlanManagerTest.class.getResource("Plan-2021-07-21-2021-08-03");
        File file = new File(resource.toURI());
        MealPlanManager mealPlanManager = new MealPlanManager(file.getParentFile());
        MealPlan mealPlan = mealPlanManager.deserialize(file);
        System.out.println(mealPlan);

        LocalDate startDate = LocalDate.of(2021, 7, 21);
        assertEquals(startDate, mealPlan.getStart());
        assertEquals(LocalDate.of(2021, 8, 3), mealPlan.getEnd());

        DayPlanIF plan = mealPlan.getPlan(startDate);
        assertEquals(MealPlannerTest.stub(), plan.getBreakfast().getDish());
        assertEquals(new Dish("Picnic", ""), plan.getLunch().getDish());
        assertEquals(new Dish("Paella", ""), plan.getDinner().getDish());

        plan = mealPlan.getPlan(startDate.plusDays(3));
        assertEquals(new Dish("Pancakes", ""), plan.getBreakfast().getDish());
        assertEquals(new Dish("Poached eggs", "with smoked salmon, hollandaise"), plan.getLunch().getDish());
        assertEquals(new Dish("Pasta Bake", "With mediteranean veg"), plan.getDinner().getDish());


    }
}