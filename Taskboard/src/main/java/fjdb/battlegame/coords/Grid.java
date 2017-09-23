package fjdb.battlegame.coords;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisbostock on 04/09/2016.
 */
public class Grid {

    private final int rows;
    private final int columns;
    List<List<Location>> locations = new ArrayList<>();
//    Map<Location, >

    public Grid(int columns, int rows) {
        this.rows = rows;
        this.columns = columns;

        for (int i = 0; i < rows; i++) {
            ArrayList<Location> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add(new Location(j, i));
            }
            locations.add(row);
        }

    }


    public Location getLocation(int x, int y) {
        if (x < 0 || x >= columns) {
            return Location.NULL;
//            throw new IllegalArgumentException(String.format("Request column %s outside of range (%s columns)", x, columns));
        }
        if (y < 0 || y >= rows) {
            return Location.NULL;
//            throw new IllegalArgumentException(String.format("Request row %s outside of range (%s rows)", y, rows));
        }

        return locations.get(y).get(x);
    }


}
