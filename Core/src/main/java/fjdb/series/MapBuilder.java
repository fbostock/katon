package fjdb.series;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapBuilder<K extends Comparable<? super K>, V> {

    ConcurrentSkipListMap<K, V> data = new ConcurrentSkipListMap<>();
    private Class<K> keyClass;
    private Class<V> valueClass;

    public MapBuilder(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public void put(K key, V value) {
        data.put(key, value);
    }

    protected Pair<List<K>, List<V>> getKeysValues() {
        Set<Map.Entry<K, V>> entries = data.entrySet();
        List<K> keys = Lists.newArrayList();
        List<V> values = Lists.newArrayList();
        for (Map.Entry<K, V> entry : entries) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
        return new Pair<>(keys, values);
    }

    public Series<K, V> make() {
        Pair<List<K>, List<V>> keysValues = getKeysValues();
        List<K> keys = keysValues.first();
        List<V> values = keysValues.second();

        return SeriesBuilder.makeSeries(keys, values, keyClass, valueClass);
    }

}
