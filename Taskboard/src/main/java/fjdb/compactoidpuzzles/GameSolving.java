package fjdb.compactoidpuzzles;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fjdb.compactoidpuzzles.generators.FileGridGenerator;
import fjdb.compactoidpuzzles.solvers.SolveByBruteForce;
import fjdb.threading.Threading;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class GameSolving {

    public static final File fileDirectory = new File("/Users/francisbostock/Documents/CompactoidPuzzles");

    public static void main(String[] args) throws IOException {
        TileProducer tileProducer = new TileProducer();
        generateGridsAndSolve(true, 7);
        if (true) return;

//        FileGridGenerator fileGridGenerator0 = new FileGridGenerator(GridFile.makeGrid(fileDirectory + "/puzzleTest_98765.txt"));
//        FileGridGenerator fileGridGenerator0 = new FileGridGenerator(GridFile.makeGrid(fileDirectory + "/puzzle1.txt"));
        gridAssessments();
//        generateGridsAndSolve(true);
        if (true) return;
//        GridFile gridFile = GridFile.makeGrid(fileDirectory + "/puzzle2.txt");
        GridFile gridFile = GridFile.makeGrid(fileDirectory + "/puzzleTest_98765.txt");
        FileGridGenerator fileGridGenerator = new FileGridGenerator(gridFile);
        TileGrid tileGrid = fileGridGenerator.makeGrid(tileProducer);
        System.out.println("THREADS: " + Threading.RUNTIME_THREADS);
        if (true) {
            return;
        }

        //        gridAssessment(tileGrid, false);
//        if (true) return;


        GameSolver gameSolver = new GameSolver(tileGrid);
        int stepsByCentralSquare = gameSolver.solveByCentralSquare();

        gameSolver = new GameSolver(fileGridGenerator.makeGrid(tileProducer));
        int stepsByRandCentralSquare = gameSolver.solveByRandomCentralSquare();

        gameSolver = new GameSolver(fileGridGenerator.makeGrid(tileProducer));
        int stepsByRandomTile = gameSolver.solveByRandomTile();

//        gameSolver = new GameSolver(fileGridGenerator.makeGrid(tileProducer));
//        int stepsByBruteForce = gameSolver.solveByBruteForce();
//
        SolveByBruteForce solver = new SolveByBruteForce(6);
        int i = solver.solveGrid(fileGridGenerator.makeGrid(tileProducer));

        System.out.println(solver.getPositionsSelected());

        System.out.println("Central Square " + stepsByCentralSquare + " steps");
        System.out.println("Random Central Square " + stepsByRandCentralSquare + " steps");
        System.out.println("Random Tile " + stepsByRandomTile + " steps");
//        System.out.println("Brute Force " + stepsByBruteForce + " steps");
        System.out.println("Brute Force " + i + " steps");


    }

    public static void gridAssessments() {
        TileProducer tileProducer = new TileProducer();
        File[] files = fileDirectory.listFiles();
        for (File file : files) {
            try {
                if (!file.getName().contains("puzzle")) continue;
                GridFile gridFile = GridFile.makeGrid(file.getPath());
                FileGridGenerator fileGridGenerator = new FileGridGenerator(gridFile);
                gridAssessment(file.getName(), fileGridGenerator.makeGrid(tileProducer), true, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void gridAssessment(String title, TileGrid tileGrid, boolean withBruteForce, int maxSteps) {
        //Run MC solver and plot
        JobResult jobResult = generateGridAndSolveSaveAndPlot(tileGrid, 10000, title);

//        int minSteps = solveGridAndPlotHistogram(title, tileGrid, 10000, maxSteps);
        int minSteps = jobResult.getSteps();

        GameSolver gameSolver = new GameSolver(copy(tileGrid));
        int stepsByCentralSquare = gameSolver.solveByCentralSquare();

        if (withBruteForce) {
            gameSolver = new GameSolver(copy(tileGrid));
            int stepsByBruteForce = gameSolver.solveByBruteForce(maxSteps);
            System.out.println(String.format("%s: Central Square steps: %s\nMC steps %s\nBrute Force best solution: %s steps", title, stepsByCentralSquare, minSteps, stepsByBruteForce));
        } else {
            System.out.println(String.format("%s: Central Square steps: %s\nMC steps %s", title, stepsByCentralSquare, minSteps));

        }


    }

    /**
     * Routine which generates random tile grids and attempts to solve them by Monte Carlo. Grids which can be solved quickly
     * are saved down and histograms plotted
     */
    public static void generateGridsAndSolve(boolean multithread, int gridSize) {
        MCSolver mcSolver = new MCSolver().setGridSize(gridSize).setSolutionAttempts(1000).setMinimumPassLevel(5).setSavePath(fileDirectory.getAbsolutePath());
        mcSolver.plotResults(true).setGridGenerations(20);
        mcSolver.solveMany();
    }

    public static JobResultExtra solveByMonteCarlo(TileGrid tileGrid, int nTrials) {
        List<Integer> data = Lists.newArrayList();

        int minSteps = Integer.MAX_VALUE;
        List<Position> bestPositions = null;
        List<Runnable> jobs = Lists.newArrayList();
        Set<JobResult> results = Sets.newConcurrentHashSet();
        for (int i = 0; i < nTrials; i++) {
            GameSolver gameSolver = new GameSolver(copy(tileGrid));
            jobs.add(() -> {
                JobResult job = runRoutine(gameSolver, GameSolver::solveByRandomTile);
                results.add(job);
            });
        }
        Threading.run(jobs);

        for (JobResult result : results) {
            data.add(result.getSteps());
            if (minSteps > result.getSteps()) {
                minSteps = result.getSteps();
                List<Position> selectedPositions = result.getBestPositions();
                bestPositions = selectedPositions;
            }

        }
        return new JobResultExtra(minSteps, bestPositions, data);
    }

    /**
     * Routine which generates a random tile grid and attempts to solve them by Monte Carlo. Grids which can be solved quickly
     * are saved down and histograms plotted
     */
    public static JobResult generateGridAndSolveSaveAndPlot() {
        MCSolver mcSolver = new MCSolver().setSolutionAttempts(1000).setMinimumPassLevel(6).setSavePath(fileDirectory.getAbsolutePath());
        mcSolver.plotResults(true);
        return mcSolver.solveSingle();
    }

    public static JobResult generateGridAndSolveSaveAndPlot(TileGrid grid, int attempts, String label) {
        MCSolver mcSolver = new MCSolver().setSolutionAttempts(attempts).setMinimumPassLevel(6).setSavePath(fileDirectory.getAbsolutePath());
        mcSolver.plotResults(true).setTileGrid(grid).setLabel(label);
        return mcSolver.solveSingle();
    }

    private static JobResult runRoutine(GameSolver gameSolver, Function<GameSolver, Integer> algorithm) {
        Integer stepsToSolve = algorithm.apply(gameSolver);
        return new JobResult(stepsToSolve, gameSolver.getSelectedPositions());
    }

    public static TileGrid copy(TileGrid grid) {
        TileGrid tileGrid = new TileGrid(grid._xMax, grid._yMax);
        for (Map.Entry<Position, GameTile> entry : grid.positionsToTiles.entrySet()) {
            tileGrid.Add(entry.getValue(), entry.getKey());
        }
        return tileGrid;
    }

    public static class HistogramTools {

        public static void chartHistogram(double[] data, int bins, String title) {
            HistogramDataset histogramDataset = new HistogramDataset();
            histogramDataset.addSeries("tiles", data, bins, 0, bins);

            JFreeChart histogram = createHistogram("Best values " + title, "", "count", histogramDataset, PlotOrientation.VERTICAL, false, false, false);
            ChartPanel chartPanel = new ChartPanel(histogram);


            JFrame frame = new JFrame("");
            frame.setPreferredSize(new Dimension(800, 500));
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JPanel panel = new JPanel();
            panel.add(chartPanel);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);

        }

        public static JFreeChart createHistogram(String title, String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
            if (orientation == null) {
                throw new IllegalArgumentException("Null 'orientation' argument.");
            } else {
                NumberAxis xAxis = new NumberAxis(xAxisLabel);
                xAxis.setAutoRangeIncludesZero(true);
                ValueAxis yAxis = new NumberAxis(yAxisLabel);
                XYItemRenderer renderer = new XYBarRenderer();
                if (tooltips) {
                    renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
                }

                if (urls) {
                    renderer.setURLGenerator(new StandardXYURLGenerator());
                }

                XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
                plot.setOrientation(orientation);
                plot.setDomainZeroBaselineVisible(true);
                plot.setRangeZeroBaselineVisible(true);
                JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
//                currentTheme.apply(chart);
                return chart;
            }
        }
    }

}
