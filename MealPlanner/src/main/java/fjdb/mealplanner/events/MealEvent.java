package fjdb.mealplanner.events;

import fjdb.util.TypedKey;
import fjdb.util.TypedMap;

public class MealEvent {

    public static String SERVER_EVENT = "serverEVent";

    private final String type;
    private final TypedMap typedMap = new TypedMap();

    public MealEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isType(String type) {
        return this.type.equals(type);
    }

    public <T> void addType(TypedKey<T> key, T value) {
        typedMap.put(key, value);
    }

    public <T> T get(TypedKey<T> key) {
        return typedMap.get(key);
    }
}
