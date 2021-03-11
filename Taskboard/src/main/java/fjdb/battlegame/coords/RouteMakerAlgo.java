package fjdb.battlegame.coords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteMakerAlgo implements RouteAlgo {

    Set<Location> isUsed = new HashSet<>();
    private GridManager gridManager;
    private boolean quickReturn;

    /**
     * An algorithm that uses a bread-first algorithm for building up routes gradually. This is not necessarily
     * quick but will thoroughly check all locations in a systematic fashion.
     * As it builds up routes, it removes locations from the phase space of available locations which limits
     * the number of results returned.
     * Setting quickReturn will return on the first successful route, thoguh this is not necessarily guaranteed
     * to be the shortest route.
     * @param gridManager
     * @param quickReturn True to return on the first complete route, false to return all possible routes.
     */
    public RouteMakerAlgo(GridManager gridManager, boolean quickReturn) {
        this.gridManager = gridManager;
        this.quickReturn = quickReturn;
    }

    @Override
    public List<Route> getRoutes(Location start, Location end) {
        Route initialRoute = new Route(start);
        List<Route> completedRoutes = new ArrayList<>();
        List<Route> routes = getRoutes(initialRoute, end);

        while(routes.size() > 0) {
            Route route = routes.remove(0);
            if (route.last().equals(end)) {
                completedRoutes.add(route);
                if (quickReturn) {
                    return completedRoutes;
                }
            } else {
                List<Route> newRoutes = getRoutes(route, end);
                routes.addAll(newRoutes);
            }
        }

        return completedRoutes;
    }


    private List<Route> getRoutes(Route initialRoute, Location end) {
        List<Route> routes = new ArrayList<>();
        Location start = initialRoute.last();
        isUsed.add(start);
        Set<Location> locations = gridManager.getAdjacentNotNull(start);
        for (Location location : locations) {
            if (!isUsed.contains(location)){
                Route newRoute = initialRoute.add(location);
                routes.add(newRoute);
                if (!location.equals(end)) {
                    isUsed.add(location);
                }
            }
        }
        return routes;
    }

}
