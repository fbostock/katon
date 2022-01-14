package fjdb.compactoidpuzzles;

public class GridGenerator implements GGenerator {

    private final int xMax;
    private final int yMax;
    private final int startingTiles;

    public GridGenerator(int xMax, int yMax, int startingTiles) {
        this.xMax = xMax;
        this.yMax = yMax;
        this.startingTiles = startingTiles;
    }

    @Override
    public TileGrid makeGrid(TileProducer tileProducer) {
        int x = -startingTiles/2;
        TileGrid grid = new TileGrid(xMax, yMax);
        for (int i = 0; i < startingTiles; i++) {
            int y = -startingTiles/2;
            for (int j = 0; j < startingTiles; j++) {
                GameTile gameTile = tileProducer.makeTile(x, y);
                grid.Add(gameTile, x, y);
                y++;
            }
            x++;
        }
        return grid;
    }
}
