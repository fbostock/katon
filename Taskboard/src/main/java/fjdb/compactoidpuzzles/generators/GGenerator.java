package fjdb.compactoidpuzzles.generators;

import fjdb.compactoidpuzzles.TileGrid;
import fjdb.compactoidpuzzles.TileProducer;

public interface GGenerator {

    public TileGrid makeGrid(TileProducer tileProducer);
}
