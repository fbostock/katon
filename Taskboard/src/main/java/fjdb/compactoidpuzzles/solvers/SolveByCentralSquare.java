package fjdb.compactoidpuzzles.solvers;

import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;

import java.util.HashSet;

public class SolveByCentralSquare extends Solver {
    @Override
    public int solveGrid(TileGrid grid) {

        //just select the central square

        int turnCount = 0;
        while (grid.countTiles() > 0 && turnCount < 1000) {
            GameTile tile = grid.getTile(0, 0);

            Position position = grid.getPosition(tile);
            positionsSelected.add(position);

            HashSet<GameTile> tileAndNeighbours = grid.removeTileAndNeighbours(tile);
            for (GameTile gameTile : tileAndNeighbours) {
                gameTile.destroy();
            }
            grid.updateQuadrants();
            turnCount++;
        }

        if (grid.countTiles() == 0) {
            System.out.println("CS Completed grid in " + turnCount + " turns");
        } else {
            System.out.println("CS Reached turn limit with " + grid.countTiles() + " tiles left");
        }
        return turnCount;

    }
}
