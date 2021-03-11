package fjdb.battlegame.coords;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteMaker {
    private final GridManager gridManager;
    Set<Location> isUsed = new HashSet<>();

    public RouteMaker(GridManager gridManager) {
        this.gridManager = gridManager;
    }


    public List<Route> getRoutes(Location start, Location end) {
        return new RouteMakerAlgo(gridManager, true).getRoutes(start, end);
    }

}
