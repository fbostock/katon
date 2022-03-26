package fjdb.compactoidpuzzles.solvers;

import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;

import java.util.HashSet;
import java.util.Random;

public class SolveByRandomCentralSquare extends Solver {
    @Override
    public int solveGrid(TileGrid grid) {

        //select a tile at random from the inner 9 tiles
        // List<GameTile> initialTiles = new List<GameTile>(grid.tilesToPositions.Keys);
        // initialTiles = ListUtil.randomiseOrder(initialTiles);
        Random random = new Random();

        int turnCount = 0;
        while (grid.countTiles() > 0 && turnCount < 1000) {
            Position position = new Position(random.nextInt(-1, 2), random.nextInt(-1, 2));
            GameTile tile = grid.getTile(position);
            if (tile == null) {
                continue;
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
            System.out.println("123 Completed grid in " + turnCount + " turns");
        } else {
            System.out.println("123 Reached turn limit with " + grid.countTiles() + " tiles left");
        }

        return turnCount;
    }
}
