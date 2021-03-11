package fjdb.maths.collatz;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class Pool<K, V> {

    Map<K, V> _map = new ConcurrentHashMap();

    public V get(K key) {
        V v = _map.get(key);
        if (v == null) {
            _map.computeIfAbsent(key, this::create);
        }
        return _map.get(key);
    }

    public static <K, V> Pool<K, V> makePool(Function<K, V> function) {
            return new Pool<K, V>() {
                @Override
                public V create(K key) {
                    return function.apply(key);
                }
            };
    }

    public Map<K, V> getPool() {
        return _map;
    }

    public abstract V create(K key);

}
