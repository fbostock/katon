package fjdb.battlegame.coords;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Route implements Iterable<Location> {

    public static final Route NULL  = new Route(Location.NULL) {
        @Override
        public int size() {
            return 0;
        }
    };

    private List<Location> locations = new ArrayList<>();

    public Route(Location location) {
        locations.add(location);
    }

    public Route(Route parent, Location location) {
        locations.addAll(parent.locations);
        locations.add(location);
    }

    public Route add(Location location) {
          return new Route(this, location);
    }

    protected List<Location> get() {
        return locations;
    }

    public Location first() {
        return locations.get(0);
    }

    public Location last() {
        return locations.get(locations.size()-1);
    }

    public int size() {
        return locations.size();
    }

    @Override
    public String toString() {
        StringBuilder label = new StringBuilder();
        for (Location location : locations) {
            label.append(String.format("(%s,%s)", location.getX(), location.getY()));
        }
        return String.format("Route(%s): %s", size(), label);
    }

    @Override
    public Iterator<Location> iterator() {
        return locations.iterator();
    }
}
