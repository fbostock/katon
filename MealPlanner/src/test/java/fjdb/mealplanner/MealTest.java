package fjdb.mealplanner;

import fjdb.mealplanner.swing.MealPlannerTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class MealTest {

    @Test
    public void stub_meals_are_stubs() {
        Meal stub = Meal.stub();
        assertTrue(Meal.isStub(stub));

        Meal meal = new Meal(MealPlannerTest.stub(), "");
        assertTrue(Meal.isStub(meal));

        Meal nonStub = new Meal(new Dish("MyDish", ""), "");
        assertFalse(Meal.isStub(nonStub));
        nonStub = new Meal(new Dish("MyDish", ""), "Some notes");
        assertFalse(Meal.isStub(nonStub));
    }
}