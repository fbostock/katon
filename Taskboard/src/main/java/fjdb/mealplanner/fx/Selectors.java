package fjdb.mealplanner.fx;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class Selectors {

    public static DatePicker getDateSelector(LocalDate initial) {
        return new DatePicker(initial);
    }

    public static DatePicker getDateSelector() {
        return getDateSelector(LocalDate.now());
    }

}
