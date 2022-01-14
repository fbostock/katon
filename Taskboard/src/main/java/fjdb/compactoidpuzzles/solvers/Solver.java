package fjdb.compactoidpuzzles.solvers;

import com.google.common.collect.Lists;
import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;

import java.util.List;
import java.util.Map;

public abstract class Solver {

    protected List<Position> positionsSelected = Lists.newArrayList();

    public abstract int solveGrid(TileGrid grid);

    public List<Position> getPositionsSelected() {
        return positionsSelected;
    }

    protected static TileGrid copy(TileGrid grid) {
        TileGrid tileGrid = new TileGrid(grid._xMax, grid._yMax);
        for (Map.Entry<GameTile, Position> entry : grid.tilesToPositions.entrySet()) {
            tileGrid.Add(entry.getKey(), entry.getValue());
        }
        return tileGrid;
    }

}
