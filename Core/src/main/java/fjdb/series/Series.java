package fjdb.series;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class Series<T extends Comparable<? super T>, V> {
    //TODO this object should be something that encapsulates an an ordered series of keys to values.
    //The TimeSeries would then be a version typed on a calendrical, e.g. LocalDate or LocalDateTime.

    T[] keys;
    V[] values;
    private int startIndex;
    private int size;

    public Series(List<T> keys, List<V> values, int startIndex, int size) {

    }

    public Series(T[] keys, V[] values, int startIndex, int size) {
        this.keys = keys;
        this.values = values;
        this.startIndex = startIndex;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public V get(T key) {
        int index = indexOf(key);
        return index < 0 ? null : values[index];
    }

    public V get(int index) {
        return values[index + startIndex];
    }

    public T getKey(int index) {
        return keys[index + startIndex];
    }

    public Series<T, V> subsequence(int startInclusive, int endExclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return new Series<>(keys, values, startIndex + startInclusive, startIndex + (endExclusive - startInclusive));
    }

    //TODO BUG this fails if startExclusive or endExclusive is not in the list.
    public Series<T, V> subsequence(T startInclusive, T endExclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        int startKey = indexOf(startInclusive);
        int endKey = indexOf(endExclusive);
        return new Series<>(keys, values,  startKey, endKey-startKey);
    }
    //TODO add methods to crea

    public Series<T, V> start(int endExclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return new Series<>(keys, values, startIndex, startIndex + endExclusive);
    }

//    public Series<T, V> start(T endExclusive) {
//
//    }

        public Series<T, V> end(int startInclusive) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return new Series<>(keys, values, startIndex + startInclusive, size-startInclusive);
    }

    public Series<T, V> end(T startKey) {
        //TODO validation on start/end index
        //TODO add tests for a subsequence of a subsequence
        return end(indexOf(startKey));
    }

    //TODO make this a view of the keys, minimizing object allocation.
    //TODO This is very inefficient if the sublist is very small.
    public List<T> getKeys() {
        return Arrays.asList(keys).subList(startIndex, startIndex + size);
    }

    public int indexOf(T key) {
        return Arrays.binarySearch(keys, key);
    }

    public T firstKey() {
        return keys[startIndex];
    }

    public T lastKey() {
        return keys[startIndex + size - 1];
    }

    public V first() {
        return values[startIndex];
    }

    public V last() {
        return values[startIndex + size - 1];
    }
    //TODO implement a SeriesIterator object, which can return nextKey, nextItem, move

    public SeriesIterator<T, V> iterator() {
        return new SeriesIteratorImpl<>(this);
    }

    public static interface SeriesIterator<K, V> {

        public boolean moveNext() throws NoSuchElementException;

        public K currentKey();

        public V curentValue();

        default void forEachValueRemaining(Consumer<? super V> action) {
            Objects.requireNonNull(action);
            while (moveNext())
                action.accept(curentValue());
        }

        default void forEachKeyRemaining(Consumer<? super K> action) {
            Objects.requireNonNull(action);
            while (moveNext())
                action.accept(currentKey());
        }

        default void forEachPairRemaining(Consumer<PairI<? super K, ? super K>> action) {
            Objects.requireNonNull(action);
            //fly weight pattern
            MutablePair<K, Object> pair = new MutablePair<>(null, null);
            while (moveNext()) {
                action.accept(pair.setBoth(currentKey(), curentValue()));
            }
        }
    }

    public static class SeriesIteratorImpl<K extends Comparable<? super K>, V> implements SeriesIterator<K, V> {

        private final int startIndex;
        private int currentIndex;
        private Series<K, V> series;

        public SeriesIteratorImpl(Series<K, V> series) {

            this.series = series;
            startIndex = series.startIndex;
            currentIndex = startIndex - 1;
        }

        @Override
        public boolean moveNext() {
            currentIndex++;
            return (currentIndex < series.size+startIndex);
        }

        @Override
        public K currentKey() {
            return series.keys[currentIndex];
        }

        @Override
        public V curentValue() {
            return series.values[currentIndex];
        }
    }
}
