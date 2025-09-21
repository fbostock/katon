package fjdb.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypedMap {

    private final Map<Object, Object> content = new ConcurrentHashMap<>();

    public <T> void put(TypedKey<T> key, T value) {
        content.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(TypedKey<T> key) {
        return (T) content.get(key);
    }


}
