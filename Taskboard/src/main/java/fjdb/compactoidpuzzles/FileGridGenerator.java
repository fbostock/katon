package fjdb.compactoidpuzzles;

import java.util.Map;

public class FileGridGenerator implements GGenerator {

    private final GridFile _gridFile;

    public FileGridGenerator(GridFile gridFile)
    {
        _gridFile = gridFile;
    }

    public TileGrid makeGrid(TileProducer tileProducer)
    {

        TileGrid grid = new TileGrid(_gridFile.getXMax(), _gridFile.getYMax());
        Map<Position,Integer> tiles = _gridFile.getTiles();
        for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
            Position position = entry.getKey();
            GameTile makeTile = tileProducer.makeTile(position.X, position.Y, entry.getValue());
            grid.Add(makeTile, position);
        }
        return grid;
    }

}
