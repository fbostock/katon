package fjdb.series;

import org.apache.tools.ant.taskdefs.Local;

import java.time.LocalDate;
import java.util.List;

public class TimeSeriesMapBuilder<V> extends MapBuilder<LocalDate, V> {

    public TimeSeriesMapBuilder(Class<V> valueClass) {
        super(LocalDate.class, valueClass);
    }

    @Override
    public TimeSeries<V> make() {
        Pair<List<LocalDate>, List<V>> keysValues = getKeysValues();
        List<LocalDate> keys = keysValues.first();
        List<V> values = keysValues.second();
        return SeriesBuilder.makeTimeSeries(keys, values);
    }
}
