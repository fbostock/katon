package fjdb.battlegame.coords;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by francisbostock on 04/09/2016.
 */
public class GridManagerTest {

    @Test
    public void access_east_location() {
        GridManager grid = new GridManager(4, 1);
        Location location = grid.get(0, 0);
        Location location2 = grid.get(1, 0);
        assertEquals(location2, grid.getEast(location));
    }

    @Test
    public void access_west_location() {
        GridManager grid = new GridManager(4, 1);
        Location location = grid.get(0, 0);
        Location location2 = grid.get(1, 0);
        assertEquals(location, grid.getWest(location2));
    }

    @Test
    public void access_north_location() {
        GridManager grid = new GridManager(1, 4);
        Location location = grid.get(0, 0);
        Location location2 = grid.get(0, 1);
        assertEquals(location2, grid.getNorth(location));
    }

    @Test
    public void access_south_location() {
        GridManager grid = new GridManager(1, 4);
        Location location = grid.get(0, 0);
        Location location2 = grid.get(0, 1);
        assertEquals(location, grid.getSouth(location2));
    }
}