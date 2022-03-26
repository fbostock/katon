package fjdb.compactoidpuzzles.solvers;

import com.google.common.collect.Lists;
import fjdb.compactoidpuzzles.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class SolveByBruteParallelised extends Solver {

    private int maxSteps;

    public SolveByBruteParallelised(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public int solveGrid(TileGrid grid) {
//First: break problem into n initial steps, based on different segments in grid

        List<GameTile> uniqueTileSets = getUniqueTileSets(grid);
        List<Callable<JobResult>> jobs = Lists.newArrayList();

        for (GameTile uniqueTileSet : uniqueTileSets) {
            jobs.add(() -> {
                TileGrid copy = copy(grid);
                copy.removeTileAndNeighbours(uniqueTileSet);
                copy.updateQuadrants();
                Position position = grid.getPosition(uniqueTileSet);
                GameSolver gameSolver = new GameSolver(copy);
                int turns = gameSolver.solveByBruteForce(maxSteps-1);
                ArrayList<Position> positions = Lists.newArrayList(position);
                positions.addAll(gameSolver.getSelectedPositions());
                return new JobResult(turns+1, positions);
            });
        }

        List<Future<JobResult>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(jobs.size());
        for (Callable<JobResult> callable : jobs) {
            results.add(executorService.submit(callable));
        }
        executorService.shutdown();

        List<Position> bestPositions = Lists.newArrayList();
        int minSteps = Integer.MAX_VALUE;
        try {
            for (Future<JobResult> result : results) {
                JobResult jobResult = result.get();
                Integer steps = jobResult.getSteps();
                if (steps > 0) {
                    if (steps < minSteps) {
                    minSteps = steps;
                        bestPositions = jobResult.getBestPositions();
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        positionsSelected.clear();
        positionsSelected.addAll(bestPositions);
        return minSteps;
    }

    private static List<GameTile> getUniqueTileSets(TileGrid grid) {
        List<GameTile> uniqueTiles = new ArrayList<>();
        List<GameTile> initialTiles = new ArrayList<GameTile>(grid.tilesToPositions.keySet());
        while (!initialTiles.isEmpty()) {
            GameTile gameTile = initialTiles.get(0);
            uniqueTiles.add(gameTile);
            TileGrid copy = copy(grid);
            HashSet<GameTile> gameTiles = copy.removeTileAndNeighbours(gameTile);
            initialTiles.removeAll(gameTiles);
        }
        return uniqueTiles;
    }
}
