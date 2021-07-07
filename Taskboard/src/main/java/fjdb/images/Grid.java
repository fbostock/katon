package fjdb.images;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by francisbostock on 31/05/2016.
 */
public class Grid<T> implements Iterable<T> {

    private int x;
    private int y;
    private List<List<T>> objects;

    public Grid(int x, int y) {
        this.x = x;
        this.y = y;
        objects = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            ArrayList<T> e = new ArrayList<>();
            for (int j = 0; j < y; j++) {
                e.add(null);
            }
            objects.add(e);
        }
    }

    public T put(int x, int y, T value) {
        List<T> ts = objects.get(x);
        T previous = ts.get(y);
        ts.set(y, value);
        return previous;
    }

    public T get(int x, int y) {
       return objects.get(x).get(y);
    }

    public int getNonnullElements() {
        int count = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                T t = get(i, j);
                if (t != null) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getWidth() {
        return x;
    }

    public int getHeight() {
        return y;
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<>() {
            int x = 0;
            int y = 0;
            boolean hasNext = true;
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                T t = get(x, y);
                x++;
                if (x >= getWidth()) {
                    x = 0;
                    y++;
                }
                if (y >= getHeight()) {
                    hasNext = false;
                }
                return t;
            }
        };
    }
}
