package fjdb.battlegame.coords;

import fjdb.battlegame.units.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisbostock on 04/09/2016.
 */
public class Location {

    //TODO may have different implementations of Locations, e.g. passable ones, blocked ones (can't have units) etc.
    private final int eastCoord;
    private final int nothCoord;
    private List<Unit> units = new ArrayList<>();

    public Location(int eastCoord, int nothCoord) {
        this.eastCoord = eastCoord;
        this.nothCoord = nothCoord;
    }

    public static boolean isNull(Location location) {
        return location == NULL;
    }

    public int getX() {
        return eastCoord;
    }

    public int getY() {
        return nothCoord;
    }

    public static Location NULL  = new Location(-1, -1){
        @Override
        public String toString() {
            return "NULL Location";
        }
    };

    @Override
    public String toString() {
        return String.format("%s X(East), Y(North): %s, %s", getClass().getSimpleName(), eastCoord, nothCoord);
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void remove(Unit unit) {
        units.remove(unit);
    }
}
