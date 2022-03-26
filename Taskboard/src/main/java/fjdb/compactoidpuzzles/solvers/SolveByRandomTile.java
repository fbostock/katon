package fjdb.compactoidpuzzles.solvers;

import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;
import fjdb.util.ListUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SolveByRandomTile extends Solver {

    private int maxSteps;

    public SolveByRandomTile(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public int solveGrid(TileGrid grid) {
        //select a tile at random
        List<GameTile> initialTiles = new ArrayList<GameTile>(grid.tilesToPositions.keySet());
        initialTiles = ListUtil.randomiseOrder(initialTiles);

        int turnCount = 0;
        //less than (but not equal) to maxSteps means turn count will equal maxSteps at last iteration if not ended sooner.
        while (grid.countTiles() > 0 && turnCount < maxSteps) {
            GameTile tile = initialTiles.get(0);
            initialTiles.remove(0);

            Position position = grid.getPosition(tile);
            if (position == null) {
                continue; //tile no longer in grid, so remove a new one
            }
            positionsSelected.add(position);

            HashSet<GameTile> tileAndNeighbours = grid.removeTileAndNeighbours(tile);
            for (GameTile gameTile : tileAndNeighbours) {
                gameTile.destroy();
            }
            grid.updateQuadrants();
            turnCount++;
        }

        if (grid.countTiles() == 0) {
            return turnCount;
//            System.out.println("Completed grid in " + turnCount + " turns");
        } else {
//            System.out.println("Reached turn limit with " + grid.countTiles() + " tiles left");
            return -1;
        }
    }
}
