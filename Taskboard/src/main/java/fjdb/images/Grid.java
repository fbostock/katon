package fjdb.images;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisbostock on 31/05/2016.
 */
public class Grid<T> {

    private int x;
    private int y;
    private List<List<T>> objects;

    public Grid(int x, int y) {
        this.x = x;
        this.y = y;
        objects = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            objects.add(new ArrayList<T>());
        }
    }

    public T get(int x, int y) {
       return objects.get(x).get(y);
    }

}
