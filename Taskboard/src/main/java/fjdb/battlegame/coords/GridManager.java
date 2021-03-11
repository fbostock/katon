package fjdb.battlegame.coords;

import fjdb.battlegame.units.Unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by francisbostock on 04/09/2016.
 */
public class GridManager {
    /*
    When querying what locations are next to others, we could either have locations know their adjacent locations
    (so each location has 4 referenced locations), or we just ask the grid to tell us.
    This class is to be used to abstract this, so callers query this class to tell us about the locations.
    How this is calculated will potentially be crucial when employing routing algorithms to work out the best
    path between two locations.
     */

    //x are columns going west to east, y are rows going south to north
    private final Grid grid;

    public GridManager(int columns, int rows) {
        grid = new Grid(columns, rows);
    }

    /**
     * Got the location in row y and column x, that is the eastward position x and the northward position y
     */
    public Location get(int x, int y) {
        return grid.getLocation(x, y);
    }

    //TODO where should exceptions be handled, or should this class return null if the value is out of range?
    public Location getNorth(Location location) {
        return grid.getLocation(location.getX(), location.getY()+1);
    }

    public Location getSouth(Location location) {
        return grid.getLocation(location.getX(), location.getY()-1);
    }

    public Location getEast(Location location) {
        return grid.getLocation(location.getX()+1, location.getY());
    }

    public Location getWest(Location location) {
        return grid.getLocation(location.getX()-1, location.getY());
    }

    public Location getEast(Location location, int x) {
        return grid.getLocation(location.getX()+x, location.getY());
    }

    public Location getWest(Location location, int x) {
        return getEast(location, -x);
    }

    public Location getNorth(Location location, int x) {
        return grid.getLocation(location.getX(), location.getY()+x);
    }

    public Location getSouth(Location location, int x) {
        return getNorth(location, -x);
    }

    public Set<Location> getAdjacent(Location location) {
        Set<Location> locations = new HashSet<>();
        locations.add(getNorth(location));
        locations.add(getEast(location));
        locations.add(getSouth(location));
        locations.add(getWest(location));
        return locations;
    }

    public Set<Location> getAdjacentNotNull(Location location) {
        Set<Location> locations = new HashSet<>();
        locations.add(getNorth(location));
        locations.add(getEast(location));
        locations.add(getSouth(location));
        locations.add(getWest(location));
        locations.remove(Location.NULL);
        return locations;
    }

    public List<Unit> getUnit(Location location) {
        return location.getUnits();
    }

    public List<Unit> getUnits(Set<Location> locations) {
        List<Unit> units = new ArrayList<>();
        for (Location location : locations) {
            units.addAll(location.getUnits());
        }
        return units;
    }
}
