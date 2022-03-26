package fjdb.compactoidpuzzles;

import fjdb.compactoidpuzzles.solvers.*;

import java.util.ArrayList;
import java.util.List;

public class GameSolver {


    private final TileGrid grid;
    private final List<Position> positionsSelected = new ArrayList<>();

    public GameSolver(TileGrid grid) {
        this.grid = grid;
    }

    public int solveByCentralSquare() {
        return solve(new SolveByCentralSquare());
    }

    public int solveByRandomTile() {
        return solveByRandomTile(1000);
    }

    /**
     * Randomly selects a tile to solve the grid, to a maximum of maxSteps (inclusive). Set this to a large value
     * to keep solving until the grid is resolved.
     */
    public int solveByRandomTile(int maxSteps) {
        return solve(new SolveByRandomTile(maxSteps));
    }

    public int solveByRandomCentralSquare() {
        return solve(new SolveByRandomCentralSquare());
    }

    public int solveByBruteForce(int maxSteps) {
        return solve(maxSteps > 0 ? new SolveByBruteForce(maxSteps, true) : new SolveByBruteForce());
    }

    public int solveByBruteForceParallelised(int maxSteps) {
        return solve(new SolveByBruteParallelised(maxSteps));
    }


    private int solve(Solver solver) {
        int turns = solver.solveGrid(grid);
        positionsSelected.clear();
        positionsSelected.addAll(solver.getPositionsSelected());
        return turns;
    }

    public List<Position> getSelectedPositions() {
        return positionsSelected;
    }

}
