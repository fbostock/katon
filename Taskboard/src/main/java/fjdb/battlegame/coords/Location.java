package fjdb.battlegame.coords;

/**
 * Created by francisbostock on 04/09/2016.
 */
public class Location {

    private final int eastCoord;
    private final int nothCoord;

    public Location(int eastCoord, int nothCoord) {
        this.eastCoord = eastCoord;
        this.nothCoord = nothCoord;
    }

    public int getX() {
        return eastCoord;
    }

    public int getY() {
        return nothCoord;
    }

    public static Location NULL  = new Location(-1, -1);

    @Override
    public String toString() {
        return String.format("%s X(East), Y(North): %s, %s", getClass().getSimpleName(), eastCoord, nothCoord);
    }
}
