package fjdb.investments;

import java.util.Arrays;

public class Series<T extends Comparable<? super T>, V> {
    //TODO this object should be something that encapsulates an an ordered series of keys to values.
    //The TimeSeries would then be a version typed on a calendrical, e.g. LocalDate or LocalDateTime.

  T[] keys;
  V[] values;
    private int startIndex;
    private int size;

    public Series(T[] keys, V[] values, int startIndex, int size) {
        this.keys = keys;
        this.values = values;
        this.startIndex = startIndex;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public V get(T key) {
        int index = Arrays.binarySearch(keys, key);
        return values[index];
    }

    public V get(int index) {
        return values[index];
    }

    public Series<T, V> subsequence(int startInclusive, int endExclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return new Series<>(keys, values, startIndex + startInclusive, startIndex + startInclusive + endExclusive);
    }

    //TODO add methods to crea

    public Series<T, V> start(int endExclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return new Series<>(keys, values, startIndex, startIndex + endExclusive);
    }

    public Series<T, V> end(int startInclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return new Series<>(keys, values, startIndex + startInclusive, size);
    }


}
