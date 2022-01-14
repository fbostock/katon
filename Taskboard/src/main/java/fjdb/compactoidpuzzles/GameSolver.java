package fjdb.compactoidpuzzles;

import fjdb.compactoidpuzzles.solvers.SolveByBruteForce;
import fjdb.util.ListUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameSolver {



    private final TileGrid grid;
    private final List<Position> positionsSelected = new ArrayList<>();

    public GameSolver(TileGrid grid) {
        this.grid = grid;
    }

    public int solveByCentralSquare() {
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

    public int solveByRandomTile() {
        //select a tile at random
        List<GameTile> initialTiles = new ArrayList<GameTile>(grid.tilesToPositions.keySet());
        initialTiles = ListUtil.randomiseOrder(initialTiles);

        int turnCount = 0;
        while (grid.countTiles() > 0 && turnCount < 1000) {
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
//            System.out.println("Completed grid in " + turnCount + " turns");
        } else {
            System.out.println("Reached turn limit with " + grid.countTiles() + " tiles left");
        }

        return turnCount;
    }

    public int solveByRandomCentralSquare() {
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

    public int solveByBruteForce() {
        SolveByBruteForce solveByBruteForce = new SolveByBruteForce();
        int i = solveByBruteForce.solveGrid(grid);
        positionsSelected.clear();
        positionsSelected.addAll(solveByBruteForce.getPositionsSelected());
        return i;
    }


    public List<Position> getSelectedPositions() {
        return positionsSelected;
    }

}
