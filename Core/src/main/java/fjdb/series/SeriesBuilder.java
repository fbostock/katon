package fjdb.series;

import java.time.LocalDate;
import java.util.List;

public class SeriesBuilder {

    public static <V> TimeSeries<V> makeTimeSeries(List<LocalDate> keys, List<V> values) {
        return makeTimeSeries(keys.toArray(new LocalDate[0]), values.toArray((V[]) (new Object[0])));
    }

    public static <V> TimeSeries<V> makeTimeSeries(LocalDate[] keys, V[] values) {
//TODO perform validation on keys/values. Keys should be monotomic, keys/values should be same size.
        return new TimeSeries<>(keys, values, 0, keys.length);
    }
}
