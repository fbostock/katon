package fjdb.series;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public class SeriesBuilder {

    public static <K extends Comparable<? super K> , V> Series<K, V> makeSeries(List<K> keys, List<V> values, Class<K> classKey, Class<V> classValue) {
        return makeSeries(Iterables.toArray(keys, classKey), Iterables.toArray(values, classValue));
    }

    @Deprecated
    public static <K extends Comparable<? super K> , V> Series<K, V> makeSeries(List<K> keys, List<V> values) {
        return makeSeries(keys.toArray((K[])(new Comparable[0])), values.toArray((V[]) (new Object[0])));
    }

    public static <K extends Comparable<? super K>, V> Series<K, V> makeSeries(K[] keys, V[] values) {
        return new Series<>(keys, values, 0, keys.length);
    }


    public static <V> TimeSeries<V> makeTimeSeries(List<LocalDate> keys, List<V> values) {
        return makeTimeSeries(keys.toArray(new LocalDate[0]), values.toArray((V[]) (new Object[0])));
    }

    public static <V> TimeSeries<V> makeTimeSeries(LocalDate[] keys, V[] values) {
//TODO perform validation on keys/values. Keys should be monotomic, keys/values should be same size.
        return new TimeSeries<>(keys, values, 0, keys.length);
    }

    public static <V> TimeSeries<V> makeEmpty() {
        return SeriesBuilder.makeTimeSeries(Lists.newArrayList(), Lists.newArrayList());
    }
}
