package fjdb.series;

import java.time.LocalDate;
import java.util.Arrays;

public class TimeSeries<V> extends Series<LocalDate, V> {


    public TimeSeries(LocalDate[] keys, V[] values, int startIndex, int size) {
        super(keys, values, startIndex, size);
    }

}
