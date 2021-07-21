package fjdb.mealplanner;

import fjdb.mealplanner.swing.MealPlannerTest;

import java.io.Serializable;
import java.time.LocalDate;

public class Meal implements Serializable {
    private static final long serialVersionUID = 20210720L;

    //TODO we may want another object between Dish and Meal, encapsulating the Dish and some notes.
    //Then when editing a meal in the table, we would edit that object, and afterwards we can generate the meal
    //objects which know their meal type and date. At the moment, it is unclear whether we need this information -
    //I put it in as I thought it would be useful. It could be if we have a collection of historical meals and we're identifying
    //on what dates we had them, but that may not be the best approach for organising/gathering that data.
    private final Dish dish;
    private final String notes;
    private final MealType type;
    private final LocalDate date;

    public static Meal stub(MealType type) {
        return new Meal(new MealPlannerTest.StubDish(), type, null, "");
    }

    /*
    TODO an instance of a dish to be used for a meal on a given date, with any notes.
    - Should it know its date? The dayplan/mealplan will know its date.
    - Should it know its type? The dayplan/mealplan will know its type.
    - Leaning towards removing them. Let's see if we use them.
     */
    public Meal(Dish dish, MealType type, LocalDate date, String notes) {
        this.dish = dish;
        this.notes = notes;
        this.type = type;
        this.date = date;
    }

    public Dish getDish() {
        return dish;
    }

    public String getNotes() {
        return notes;
    }

    //TODO remove type and date from meal - it just wraps a Dish and notes.
    //TODO modify the editor the breakfast/lunch dinner so that it is a free text field to edit, which we will
    //attempt to interpret as a Dish and notes. If we can't parse the Dish, we should use a NULL Dish object, which
    //should be handled accordingly e.g. display should be blank. 
    public MealType getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return dish.getName();
    }
}
