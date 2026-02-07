package fjdb.mealplanner;

import java.util.List;

public class GeneralNotesImpl {

    List<Meal> meals;
    String notes;

    public GeneralNotesImpl(List<Meal> meals, String notes) {
        this.meals = meals;
        this.notes = notes;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
