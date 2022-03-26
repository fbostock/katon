package fjdb.compactoidpuzzles.solvers;

import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;

import java.util.*;

public class SolveByBruteForce extends Solver {

    /*
    Notes:
    The maxSteps variable can adjust based on the best solution. This is adjusted when the current turns (depth of iteration) is 1 i.e. the
    first layer, and can only be decreased from its initial value. Adjusting this helps the algorithm to avoid wasted phase space.

    The lowestByDepth map helps to keep track which route is the best, in order to keep track of the Positions selected to
    get to that solution.
     */
    private final int maxStepValue;
    private boolean start = true;
    private int initialTilesRemaining = 0;

    private int stepCount = 1;
    private boolean adjustMaxSteps;
    private int maxSteps;
    private List<Position> currentBest;
    /*Map of currentTurns (depth of iteration) to lowest number of further depths required to solve grid. The lowest depth is 1.*/
    private Map<Integer, Integer> lowestByDepth = new HashMap<>();

    public SolveByBruteForce(int maxSteps, boolean adjustMaxSteps) {
        this.maxSteps = maxSteps;
        this.maxStepValue = maxSteps == Integer.MAX_VALUE ? 1000000 : Integer.MAX_VALUE;
        this.adjustMaxSteps = adjustMaxSteps;
    }

    public SolveByBruteForce(int maxSteps) {
        this(maxSteps, true);
    }

    public SolveByBruteForce() {
        this(Integer.MAX_VALUE);
    }

    @Override
    public int solveGrid(TileGrid grid) {
        currentBest = new ArrayList<>(grid.countTiles());
        for (int i = 0; i < grid.countTiles(); i++) {
            currentBest.add(null);
        }
        try {
            TileGrid tileGrid = copy(grid);

            int turnCount = iterateThroughGrid(tileGrid, 0);

            positionsSelected.addAll(currentBest.subList(1, Math.min(turnCount, maxSteps)+1));
            System.out.println("BruteForce Best Completed in " + turnCount + " turns, with total steps: " + stepCount);
            stepCount = 0;

            return turnCount;
        } catch (SolvingFailedException ex) {
            System.out.println("Failed to solve within " + maxSteps);
            currentBest.removeIf(Objects::isNull);
            positionsSelected.addAll(currentBest);
        }

        return -1;
    }


    private static HashSet<GameTile> selectTileAndUpdateGrid(TileGrid grid, GameTile nextTile) {
        HashSet<GameTile> removeTileAndNeighbours = grid.removeTileAndNeighbours(nextTile);
        grid.updateQuadrants();
        return removeTileAndNeighbours;
    }


    private int iterateThroughGrid(TileGrid grid, int currentTurns) throws SolvingFailedException {
        if (stepCount > maxStepValue) {
            throw new SolvingFailedException("Step count exceeded " + maxStepValue + ". Remaining start tiles: " + initialTilesRemaining);
        }
        if (stepCount % 10000 == 0) {
            System.out.println("Step count reached " + stepCount + ". Remaining start tiles: " + initialTilesRemaining);
        }

        if (currentTurns >= maxSteps) {
            stepCount++;
            return maxStepValue/2;//return a "large" value just to ensure it is higher than the likely best.
        }
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
                    best = step2;
                    bestPosition = grid.getPosition(selectedGameTile);
                    Integer integer = lowestByDepth.get(currentTurns);
                    if (integer== null || best < integer) {
                        lowestByDepth.put(currentTurns, best);
                        if (adjustMaxSteps && currentTurns ==1) {
                            System.out.println("ADJUSTING to " + best);
                            maxSteps = Math.min(maxSteps, best+1);
                        }
                        currentBest.set(currentTurns, bestPosition);
                    }
                }
            } else {
                stepCount++;
                bestPosition = grid.getPosition(selectedGameTile);
                Integer integer = lowestByDepth.get(currentTurns);
                if (integer== null || integer > 0) {
                    lowestByDepth.put(currentTurns, 0);
                    currentBest.set(currentTurns, bestPosition);
                }
                return 1;
            }

        }

        return best + 1;
    }

}
