package fjdb.mealplanner.fx;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.Meal;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;

import javax.sound.sampled.Clip;
import java.util.HashMap;

public class DragUtils {

    public static final Format<Dish> DISH_FORMAT = new Format<>("dragged_dish");
    public static final Format<Meal> MEAL_FORMAT = new Format<>("dragged_meal");

    public static <T> HashMap<DataFormat, Object> makeContent(Format<T> format, T data) {
        HashMap<DataFormat, Object> map = new HashMap<>();
        map.put(format, data);
        return map;
    }

    public static <T> T getContent(Dragboard dragboard, Format<T> dataFormat) {
        return dataFormat.get(dragboard);
    }

    public static class Format<T> extends DataFormat {
        public Format(String strings) {
            super(strings);
        }

        @SuppressWarnings("unchecked")
        public T get(Dragboard dragboard) {
            if (dragboard.hasContent(this)) {
                return (T) dragboard.getContent(this);
            }
            return null;
        }
        @SuppressWarnings("unchecked")
        public T get(Clipboard clipboard) {
            if (clipboard.hasContent(this)) {
                return (T) clipboard.getContent(this);
            }
            return null;
        }


    }

    public static <T> T getContent(Clipboard clipboard, Format<T> dataFormat) {
        return dataFormat.get(clipboard);
    }
}
