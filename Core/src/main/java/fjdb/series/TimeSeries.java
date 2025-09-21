package fjdb.series;

import java.time.LocalDate;
import java.util.Arrays;

public class TimeSeries<V> extends Series<LocalDate, V> {


    public TimeSeries(LocalDate[] keys, V[] values, int startIndex, int size) {
        super(keys, values, startIndex, size);
    }

    //TODO generic way to build this using Series base class
    public TimeSeries<V> timeSubsequence(int startInclusive, int endExclusive) {
        return new TimeSeries<>(keys, values, startIndex + startInclusive, startIndex + (endExclusive - startInclusive));
    }

    //TODO generic way to build this using Series base class
    public TimeSeries<V> timeSubsequence(LocalDate startInclusive, LocalDate endExclusive) {
        int startKey = indexOf(startInclusive);
        int endKey = indexOf(endExclusive);
        if (endKey>=0) {
            if (startKey >=0) {
                return new TimeSeries<>(keys, values, startKey, endKey - startKey);
            } else {
                return new TimeSeries<>(keys, values, 0, endKey);
            }
        } else {
            int effectiveEnd = effectivePosition(endKey);
            if (effectiveEnd==0) return null;//TODO return empty sequence, as we're requesting data before the start
            if (effectiveEnd >= size) {
                if (startKey >=0) {
                    return new TimeSeries<>(keys, values,  startKey, keys.length-startKey);
                } else {
                    return this;//trying to get data starting before and ending after this series.
                }
            } else {
                if (startKey>=0) {
                    //end is a point within the series whose key is missing, so go from start to insertion point of the key
                    return new TimeSeries<>(keys, values,  startKey, keys.length-effectiveEnd-startKey);
                } else {
                    //end is within, but start is before series, so go from series start up to insertion point.
                    return new TimeSeries<>(keys, values,  0, keys.length-effectiveEnd);
                }
            }
        }
        //TODO handle start date not in sequence.
        //TODO add lots of tests, particularly covering subsequences.

    }
}
