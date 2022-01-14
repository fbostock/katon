package fjdb.compactoidpuzzles;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

//        FileGridGenerator fileGridGenerator0 = new FileGridGenerator(GridFile.makeGrid(fileDirectory + "/puzzleTest_98765.txt"));
//        FileGridGenerator fileGridGenerator0 = new FileGridGenerator(GridFile.makeGrid(fileDirectory + "/puzzle1.txt"));

//        gridAssessment(fileGridGenerator0.makeGrid(tileProducer), false);
//        generateGridsAndSolve();
//        if (true) return;
//        GridFile gridFile = GridFile.makeGrid(fileDirectory + "/puzzle2.txt");
        GridFile gridFile = GridFile.makeGrid(fileDirectory + "/puzzleTest_98765.txt");
        FileGridGenerator fileGridGenerator = new FileGridGenerator(gridFile);
        TileGrid tileGrid = fileGridGenerator.makeGrid(tileProducer);
        System.out.println("THREADS: " + Threading.RUNTIME_THREADS);
        int minSteps = solveGridAndPlotHistogram(tileGrid, 10000);
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


    public static void gridAssessment(TileGrid tileGrid, boolean withUnlimitedBruteForce) {
        //Run MC solver and plot
        int minSteps = solveGridAndPlotHistogram(tileGrid, 1000);

        GameSolver gameSolver = new GameSolver(copy(tileGrid));
        int stepsByCentralSquare = gameSolver.solveByCentralSquare();

        if (withUnlimitedBruteForce) {
            gameSolver = new GameSolver(copy(tileGrid));
            int stepsByBruteForce = gameSolver.solveByBruteForce();
            System.out.println(String.format("Central Square steps: %s\nMC steps %s\nBrute Force best solution: %s steps", stepsByCentralSquare, minSteps, stepsByBruteForce));
        } else {
            System.out.println(String.format("Central Square steps: %s\nMC steps %s", stepsByCentralSquare, minSteps));

        }


    }

    /**
     * Routine which generates random tile grids and attempts to solve them by Monte Carlo. Grids which can be solved quickly
     * are saved down and histograms plotted
     */
    public static void generateGridsAndSolve() {

        int uniqueCode = 98769;
        for (int i = 0; i < 10000; i++) {
            if (generateGridAndSolve(uniqueCode)) {
                uniqueCode++;
            }

        }

    }

    /**
     * Solves the given TileGrid by Monte Carlo, and plots the results in a Histogram.
     *
     * @param tileGrid
     */
    public static void solveGridAndPlotHistogram(TileGrid tileGrid) {
        solveGridAndPlotHistogram(tileGrid, 1000);
    }

    public static int solveGridAndPlotHistogram(TileGrid tileGrid, int tests) {

        List<Integer> data = Lists.newArrayList();


        List<Runnable> jobs = Lists.newArrayList();

        Set<Job> jobResults = Sets.newConcurrentHashSet();

        for (int i = 0; i < tests; i++) {

            jobs.add(new Runnable() {
                @Override
                public void run() {
                    GameSolver gameSolver = new GameSolver(copy(tileGrid));
                    Job job = runRoutine(gameSolver, GameSolver::solveByRandomTile);
                    jobResults.add(job);
                }
            });
        }
        Threading.run(jobs);

        List<Position> bestPositions = null;
        int minSteps = Integer.MAX_VALUE;
        for (Job jobResult : jobResults) {
            int steps = jobResult.steps;
            data.add(steps);
            if (minSteps > steps) {
                minSteps = steps;
                List<Position> selectedPositions = jobResult.bestPositions;
                bestPositions = selectedPositions;
            }
        }

        //TODO have a blocking queue which gets results as they get completed.

        int maxSteps = 0;
        double[] doubleData = new double[data.size()];

        int i = 0;
        for (Integer datum : data) {
            doubleData[i] = datum;
            maxSteps = Math.max(maxSteps, datum);
            i++;
        }

        System.out.println(bestPositions);
        HistogramTools.chartHistogram(doubleData, maxSteps);
        return minSteps;
    }

    private static class Job {
        private final Integer steps;
        private List<Position> bestPositions;

        public Job(Integer steps, List<Position> bestPositions) {
            this.steps = steps;
            this.bestPositions = bestPositions;
        }
    }

    /**
     * Routine which generates a random tile grid and attempts to solve them by Monte Carlo. Grids which can be solved quickly
     * are saved down and histograms plotted
     */
    public static boolean generateGridAndSolve(int uniqueCode) {

        GridGenerator gridGenerator = new GridGenerator(10, 10, 7);
        TileGrid tileGrid = gridGenerator.makeGrid(new TileProducer());

        List<Integer> data = Lists.newArrayList();

        int minSteps = Integer.MAX_VALUE;
        List<Position> bestPositions = null;
        for (int i = 0; i < 1000; i++) {
            GameSolver gameSolver = new GameSolver(copy(tileGrid));
            Job job = runRoutine(gameSolver, GameSolver::solveByRandomTile);
//            System.out.println(steps);
            data.add(job.steps);
            if (minSteps > job.steps) {
                minSteps = job.steps;
                List<Position> selectedPositions = gameSolver.getSelectedPositions();
                bestPositions = selectedPositions;
            }
        }

        if (minSteps <= 6) {
            try {
                GridFile.createFile(fileDirectory.getAbsolutePath() + String.format("/puzzleTest_%s.txt", uniqueCode), tileGrid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(bestPositions);

            int maxSteps = 0;
            double[] doubleData = new double[data.size()];
            for (int i = 0; i < data.size(); i++) {
                Integer integer = data.get(i);
                doubleData[i] = integer;
                maxSteps = Math.max(maxSteps, integer);
            }

            HistogramTools.chartHistogram(doubleData, maxSteps);
            return true;
        } else {
            System.out.println("Failed to find quick solution");
            return false;
        }


    }


    private static Job runRoutine(GameSolver gameSolver, Function<GameSolver, Integer> algorithm) {
        Integer stepsToSolve = algorithm.apply(gameSolver);
        return new Job(stepsToSolve, gameSolver.getSelectedPositions());
    }

    private static TileGrid copy(TileGrid grid) {
        TileGrid tileGrid = new TileGrid(grid._xMax, grid._yMax);
        for (Map.Entry<Position, GameTile> entry : grid.positionsToTiles.entrySet()) {
            tileGrid.Add(entry.getValue(), entry.getKey());
        }
        return tileGrid;
    }

    public static class HistogramTools {

        public static void chartHistogram(double[] data, int bins) {
            HistogramDataset histogramDataset = new HistogramDataset();
            histogramDataset.addSeries("tiles", data, bins);

            JFreeChart histogram = createHistogram("Best values", "", "count", histogramDataset, PlotOrientation.VERTICAL, false, false, false);
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
