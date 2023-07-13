package fjdb.series;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;


public class SeriesTest {

    @Test
    public void test() {
        Key[] keys = new Key[]{new Key(4),new Key(6), new Key(8)};
        String[] values = new String[] {"first", "second", "third"};
        Series<Key, String> series = new Series<>(keys, values, 0, keys.length);

        assertEquals("first", series.get(0));
        assertEquals("second", series.get(1));
        assertEquals("third", series.get(2));
        assertEquals("first", series.get(new Key(4)));
        assertEquals("second", series.get(new Key(6)));
        assertEquals("third", series.get(new Key(8)));
    }

    private static class Key implements Comparable<Key>{
        private int key;

        public Key(int key) {

            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return key == key1.key;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public int compareTo(Key o) {
            return Integer.compare(key, o.key);
        }
    }

}