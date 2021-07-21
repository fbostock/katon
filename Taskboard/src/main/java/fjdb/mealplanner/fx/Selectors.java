package fjdb.mealplanner.fx;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Function;

public class Selectors {

    public static DatePicker getDateSelector(LocalDate initial) {
        return new DatePicker(initial);
    }

    public static DatePicker getDateSelector() {
        return getDateSelector(LocalDate.now());
    }

    public static <T> ComboBox<T> getDropDown(Collection<T> items) {
        return new ComboBox<>(FXCollections.observableArrayList(items));
    }

    public static <T> ComboBox<T> getDropDown(Collection<T> items, Function<T, String> displayer) {
        ComboBox<T> tComboBox = new ComboBox<>(FXCollections.observableArrayList(items));
        //TODO work out how to use the displayer to use a custom function to control how the items are displayed, in case
        //the toString() method is not suitable.
//        tComboBox.setConverter(new StringConverter<T>() {
//            @Override
//            public String toString(T t) {
//                return displayer.apply(t);
//            }
//
//            @Override
//            public T fromString(String s) {
//                return null;
//            }
//        });
        return tComboBox;
    }


}
