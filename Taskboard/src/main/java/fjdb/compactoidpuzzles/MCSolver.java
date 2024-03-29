package fjdb.compactoidpuzzles;

import com.google.common.collect.Lists;
import fjdb.compactoidpuzzles.generators.GridGenerator;
import fjdb.compactoidpuzzles.generators.RegionalGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MCSolver {

    private final static int DEFAULT_GRIDSIZE = 6;
    private final static int DEFAULT_SOLUTION_ATTEMPTS = 1000;
    /*Minimum attempts to consider an acceptable solution. Default means any value would be acceptable*/
    private final static int DEFAULT_MIN_PASS = 6;

    String savePath;
    boolean plotResults;
    boolean saveResults;
    String labelForPlottingAndSaving = LocalDateTime.now().toString();
    /*Number of grids to generate and solve*/
    int gridGenerations = 1;
    int solutionAttemps = DEFAULT_SOLUTION_ATTEMPTS;
    int minimumPassLevel = DEFAULT_MIN_PASS;
    int gridSize = DEFAULT_GRIDSIZE;

    TileGrid inputGrid;

    //TODO object to generate filenames when saving.
    //FileNameGenerator<String> generator...
    public MCSolver setSavePath(String path) {
        savePath = path;
        saveResults = path != null && !path.isBlank();
        return this;
    }

    public MCSolver plotResults(boolean plot) {
        plotResults = plot;
        return this;
    }

    public MCSolver setLabel(String label) {
        labelForPlottingAndSaving = label;
        return this;
    }

    /**
     * Sets the number of grids to generate and solve. Not relevant when solving a provided TileGrid.
     *
     * @return
     */
    public MCSolver setGridGenerations(int generations) {
        gridGenerations = generations;
        return this;
    }

    public MCSolver setSolutionAttempts(int attempts) {
        solutionAttemps = attempts;
        return this;
    }

    public MCSolver setMinimumPassLevel(int level) {
        minimumPassLevel = level;
        return this;
    }

    public MCSolver setGridSize(int size) {
        gridSize = size;
        return this;
    }

    public MCSolver setTileGrid(TileGrid grid) {
        inputGrid = grid;
        return this;
    }

    private TileGrid getGrid() {
        return inputGrid == null ? generateGrid() : inputGrid;
    }

    private TileGrid generateGrid() {
//        GridGenerator gridGenerator = new GridGenerator(Math.max(gridSize, 10), Math.max(gridSize, 10), gridSize);
        RegionalGenerator gridGenerator = new RegionalGenerator(gridSize);
        return gridGenerator.makeGrid(new TileProducer());
    }

    public JobResultExtra solveSingle() {
        return solveSingle1("0");
    }

    private JobResultExtra solveSingle1(String trialNumber) {
        TileGrid tileGrid = getGrid();
        JobResultExtra jobResult = GameSolving.solveByMonteCarlo(tileGrid, solutionAttemps);

        if (jobResult.getSteps() <= minimumPassLevel) {


            System.out.println(jobResult.getBestPositions());

            boolean saveCondition = false;
            int minBin = -1;
            if (plotResults) {
//                List<Integer> data = Lists.newArrayList();
//                int maxSteps = 0;
//                data.addAll(jobResult.getData());
//                double[] doubleData = new double[data.size()];
//                for (int i = 0; i < data.size(); i++) {
//                    Integer integer = data.get(i);
//                    doubleData[i] = integer;
//                    maxSteps = Math.max(maxSteps, integer);
//                }
//                GameSolving.Histogram histogram = new GameSolving.Histogram(doubleData, maxSteps, String.valueOf(labelForPlottingAndSaving + "_" + trialNumber));
                GameSolving.Histogram histogram = jobResult.getHistogram(labelForPlottingAndSaving + "_" + trialNumber);
                minBin = histogram.getMinBin();
                double minBinValue = histogram.getBinValue(minBin);
                double nextMinBinValue = histogram.getBinValue(minBin + 1);
                double nextNextMinBinValue = histogram.getBinValue(minBin + 2);
                if (5 * minBinValue < nextMinBinValue || 5 * nextMinBinValue < nextNextMinBinValue) {
                    saveCondition = true;
                    System.out.println("will save this one");
                }
                if (saveCondition)
                    histogram.createFrame();
//                GameSolving.HistogramTools.chartHistogram(doubleData, maxSteps, String.valueOf(labelForPlottingAndSaving + "_" + trialNumber));

            }

            if (saveCondition && saveResults) {
                try {
                    GridFile.createFile(savePath + String.format("/puzzleTest_%s.txt", labelForPlottingAndSaving + "_" + trialNumber+ "_"+ minBin), tileGrid);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Failed to find quick solution");
        }
        return jobResult;
    }

    public List<JobResult> solveMany() {
        //TODO perform solution.

        List<JobResult> results = Lists.newArrayList();
        for (int trial = 0; trial < gridGenerations; trial++) {
            JobResult jobResult = solveSingle1("" + trial);
            results.add(jobResult);
        }
        return results;
    }
    //TODO allow user to specify a GGenerator

    /**
     * Convenience method for solving a grid with the given number of attempts
     *
     * @return The best result, including minimum steps to solve and the solution.
     */
    public static JobResultExtra solve(TileGrid grid, int attempts) {
        return new MCSolver().setSolutionAttempts(attempts).setTileGrid(grid).solveSingle();
    }

}
