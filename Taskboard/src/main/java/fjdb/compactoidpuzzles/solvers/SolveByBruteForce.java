package fjdb.compactoidpuzzles.solvers;

import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SolveByBruteForce extends Solver {

    private static final int maxStepValue = 1000000;
    /*
    TODO complete refactor, and test this class. Remove code from GameSolver.
    Modify this class so that it has a limit of how many steps down to try.

    TODO amend the maxSteps variable in the iterateThroughGrid method for the primary level i.e. once we have a found a solution,
    we only continue searching for solutions no greater than it.
    Note: this won't have any performance improvement for cases we've looked at, since we usually know the best solution by MC.
     */
    private static boolean start = true;
    private static int initialTilesRemaining = 0;

    private int stepCount = 1;
    private final int maxSteps;
    private List<Position> currentBest;
    private List<Position> savedBest;


    public SolveByBruteForce(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public SolveByBruteForce() {
        this(Integer.MAX_VALUE);
    }

    @Override
    public int solveGrid(TileGrid grid) {
        savedBest = new ArrayList<>(grid.countTiles());
        currentBest = new ArrayList<>(grid.countTiles());
        for (int i = 0; i < grid.countTiles(); i++) {
            currentBest.add(null);
            savedBest.add(null);
        }
        try {
            TileGrid tileGrid = copy(grid);

            int turnCount = iterateThroughGrid(tileGrid, 0);
            savedBest.set(1, currentBest.get(1));
            savedBest.remove(0);
            savedBest.removeIf(Objects::isNull);

            positionsSelected.addAll(savedBest);
            System.out.println("BruteForce Best Completed in " + turnCount + " turns, with total steps: " + stepCount);
            stepCount = 0;
            return turnCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());

            savedBest.set(1, currentBest.get(1));
            savedBest.remove(0);
            savedBest.removeIf(Objects::isNull);

            positionsSelected.addAll(savedBest);
        }

        return -1;
    }


    private static HashSet<GameTile> selectTileAndUpdateGrid(TileGrid grid, GameTile nextTile) {
        HashSet<GameTile> removeTileAndNeighbours = grid.removeTileAndNeighbours(nextTile);
        grid.updateQuadrants();
        return removeTileAndNeighbours;
    }


    private int iterateThroughGrid(TileGrid grid, int currentTurns) throws Exception {
        if (stepCount > maxStepValue) {

            throw new Exception("Step count exceeded 100000. Remaining start tiles: " + initialTilesRemaining);
        }
        if (stepCount % 10000 == 0) {
            System.out.println("Step count reached " + stepCount + ". Remaining start tiles: " + initialTilesRemaining);
        }

        if (currentTurns >= maxSteps) {
            stepCount++;
            return maxStepValue;//return a "large" value just to ensure it is higher than the likely best.
        }
//        if (currentTurns > maxSteps) return Integer.MAX_VALUE;
        currentTurns++;

        boolean output = false;
        if (start) {
            output = true;
            initialTilesRemaining = grid.countTiles();
            start = false;
        }
        List<GameTile> gameTiles = new ArrayList<>(grid.tilesToPositions.keySet());
        int best = Integer.MAX_VALUE;

        Position bestPosition = null;
        //here we want to iterate over all the tiles in separate blocks. e.g. pick a tile, run the solveStep for the tile,
        //and remove those removed from the gameTiles collection (as the resulting grid will be the same for each tile).
        while (gameTiles.size() > 0) {
            if (output) {
                initialTilesRemaining = gameTiles.size();
            }
            TileGrid tileGrid = copy(grid);
            GameTile selectedGameTile = gameTiles.get(0);
            HashSet<GameTile> hashSet = selectTileAndUpdateGrid(tileGrid, selectedGameTile);
            gameTiles.removeAll(hashSet);

            if (tileGrid.countTiles() > 0) {
                int step2 = iterateThroughGrid(tileGrid, currentTurns);
                if (step2 < best) {
                    best = Math.min(best, step2);
                    for (int i = currentTurns; i < currentBest.size(); i++) {
                        savedBest.set(i, currentBest.get(i));
                    }
                    bestPosition = grid.getPosition(selectedGameTile);
                }
            } else {
                stepCount++;
                bestPosition = grid.getPosition(selectedGameTile);
                currentBest.set(currentTurns, bestPosition);
                for (int i = currentTurns; i < currentBest.size(); i++) {
                    savedBest.set(i, currentBest.get(i));
                }
                return 1;
                //TODO finished iteration
            }

        }

        currentBest.set(currentTurns, bestPosition);
        return best + 1;
    }

}
