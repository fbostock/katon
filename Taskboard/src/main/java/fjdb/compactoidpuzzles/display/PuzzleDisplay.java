package fjdb.compactoidpuzzles.display;

import fjdb.compactoidpuzzles.*;
import fjdb.fxutil.FxUtils;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PuzzleDisplay extends Application {

    /*
    TODOs:
    Check through current 6x6 grids, and pull out reasonable puzzles.
    Run simulations for 7x7 grids, and do the same.
    Brute force: Can we modify algorithm so that for initial grid, each tileset (tiles of adjacent type) is treated as a separate
    problem to be solved, either to run in parallel or otherwise to persist results so that the grid can be solved "gradually"
    rather than simply in one go?
     */

//    private static final File fileDirectory = new File("/Users/francisbostock/Documents/CompactoidPuzzles");


    private static class Data implements Comparable<Data> {
        public Data(int id) {
            this.id = id;
        }

        int id;

        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    '}';
        }

        @Override
        public int compareTo(Data o) {
            System.out.printf("Comparing %s %s%n", id, o.id);
//            return Integer.compare(Math.abs(id), Math.abs(o.id));
            return -1;
        }

        @Override
        public int hashCode() {
            return 31;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return id == data.id;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }

    public static void printPuzzle(GridFile gridFile) {
        launch(new String[0]);
    }

    @Override
    public void start(Stage stage) throws Exception {

        final BorderPane sceneRoot = new BorderPane();
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(new Rectangle(100, 1, Color.WHITE));


        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);

        ScrollPane scrollPane = new ScrollPane(gridsTabs());
        flowPane.getChildren().add(scrollPane);
        flowPane.getChildren().add(new Rectangle(100, 1, Color.WHITE));

        mainTabs.getTabs().add(new Tab("View Puzzles", flowPane));
        mainTabs.getTabs().add(new Tab("Edit Puzzles", getEditableGrid()));
        mainTabs.getTabs().add(new Tab("Edit Puzzles", generatorTab()));
        sceneRoot.setCenter(mainTabs);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();
    }

    private TabPane generatorTab() {
        TabPane generatorTab = new TabPane();

        //add text field to enter grid size
        TextField gridSizeField = FxUtils.getTextField();
        //add field to generate minimum pass level
        TextField minTurnsToComplete = FxUtils.getTextField();
        //add field to generate num of mc attemps to solve grid
        TextField solutionAttempts = FxUtils.getTextField();

        CheckBox plotResults = FxUtils.getCheckBox();

        Button startGeneration = FxUtils.getButton("GO");


        //add button to indicate whether to plot results
        //add button to generate puzzles and save files

        //saved files should go to the PuzzleFileManager, and the other tabs should be updated to look in the folder.

        return generatorTab;
    }

    private TabPane gridsTabs() {
        TabPane puzzleTabs = new TabPane();
        puzzleTabs.setMaxWidth(500);
        puzzleTabs.setSide(Side.TOP);

        List<GridFile> gridFiles = PuzzleFileManager.getInstance().getGridFiles();
        for (GridFile gridFile : gridFiles) {
            TileGrid tileGrid = gridFile.makeTileGrid();
            FlowPane flowPane = new PlayableGrid(tileGrid);

//            FlowPane flowPane = drawGrid(gridFile);
            puzzleTabs.getTabs().add(new Tab(gridFile.getName(), flowPane));
        }
        PuzzleFileManager.getInstance().addListener(gridFile -> {
            puzzleTabs.getTabs().add(new Tab(gridFile.getName(), drawGrid(gridFile)));
        });
        return puzzleTabs;
    }

    private FlowPane drawGrid(TileGrid tileGrid) {
        return drawGrid(tileGrid.getTiles(), tileGrid._xMax);
    }

    private FlowPane drawGrid(GridFile gridFile) {
        return drawGrid(gridFile.getTiles(), gridFile.getXMax());
    }

    private FlowPane drawGrid(Map<Position, Integer> tiles, int xMax) {
        FlowPane mainPane = new FlowPane();

        Group group = new Group();

        int xWidth = 40;
        int yWidth = 40;

        for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
            Rectangle rectangle = new Rectangle(xWidth * (xMax + entry.getKey().X), yWidth * (entry.getKey().Y), xWidth, yWidth);
            rectangle.setFill(getColor(entry.getValue()));
            rectangle.setStroke(Color.BLACK);
            group.getChildren().add(rectangle);
        }
        mainPane.getChildren().add(group);
        return mainPane;
    }


    private static class PlayableGrid extends FlowPane {
        private final TileGrid grid;

        public PlayableGrid(TileGrid grid) {
            super(Orientation.VERTICAL);
            this.grid = grid;
            FlowPane flowPane = new FlowPane();
            drawPlayableGrid(flowPane, GameSolving.copy(grid));
            getChildren().add(flowPane);
            Button reset = new Button("Reset");
            reset.setOnAction(actionEvent -> drawPlayableGrid(flowPane, GameSolving.copy(grid)));
            getChildren().add(reset);
        }

        private void drawPlayableGrid(FlowPane inputPane, TileGrid grid) {
            FlowPane mainPane = new FlowPane();

            Map<Position, Integer> tiles = grid.getTiles();
            Group group = new Group();

            int xWidth = 40;
            int yWidth = 40;

            for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
                Rectangle rectangle = new Rectangle(xWidth * (grid._xMax + entry.getKey().X), yWidth * (entry.getKey().Y), xWidth, yWidth);
                rectangle.setOnMouseClicked(mouseEvent -> {
                    grid.removeTileAndNeighbours(grid.getTile(entry.getKey()));
                    grid.updateQuadrants();
                    drawPlayableGrid(inputPane, grid);
                });
                rectangle.setFill(getColor(entry.getValue()));
                rectangle.setStroke(Color.BLACK);
                group.getChildren().add(rectangle);
            }
            //Add invisible static rectangle, to fixed point in corner to control layout position of grid.
            Rectangle rectangle = new Rectangle(grid._xMax * xWidth / 2.0, -grid._xMax * xWidth / 2.0, 0, 0);
            rectangle.setFill(Color.WHITE);
            Rectangle centre = new Rectangle(xWidth * (grid._xMax + 0.4), yWidth * 0.4, 4, 4);
            centre.setFill(Color.WHITE);

//            Rectangle rectangle1 = new Rectangle(xWidth * (grid._xMax + 1), yWidth * (1), xWidth, yWidth);
//            rectangle1.setFill(Color.WHITESMOKE);
//            group.getChildren().add(rectangle1);
//            Rectangle rectanglem1 = new Rectangle(xWidth * (grid._xMax + 1), yWidth * (-1), xWidth, yWidth);
//            rectanglem1.setFill(Color.PURPLE);
//            group.getChildren().add(rectanglem1);
//            Rectangle rectanglem2 = new Rectangle(xWidth * (grid._xMax + -1), yWidth * (1), xWidth, yWidth);
//            rectanglem2.setFill(Color.CYAN);
//            group.getChildren().add(rectanglem2);


            group.getChildren().add(rectangle);
            group.getChildren().add(centre);
            mainPane.getChildren().add(group);
            inputPane.getChildren().clear();
            inputPane.getChildren().add(mainPane);
        }

    }

    private static Paint getColor(int type) {
        if (type == 0) {
            return Color.BLUE;
        } else if (type == 1) {
            return Color.GREEN;
        } else if (type == 2) {
            return Color.PINK;
        } else if (type == 3) {
            return Color.ORANGE;
        }
        return Color.BLACK;
    }

    private FlowPane getEditableGrid() {
        return new EditablePane();
    }

    private static GameTile makeTile(int tileType) {
        return new GameTile() {{
            this.type = tileType;
        }};
    }

    private static class EditablePane extends FlowPane {
        private static final int gridSize = 6;
        private final ComboBox<Integer> getTileType;
        private final ComboBox<SolverType> solvers;
        private final Group rectangleGroup;
        Map<Position, Integer> tiles = new HashMap<>();
        Map<Position, Rectangle> tilesUI = new HashMap<>();

        public EditablePane() {

            //TODO add a chooser filled with GridFile objects fetch from PuzzleFileManager, and when selected
            //load the corresponding gridFile.

            getTileType = FxUtils.makeCombo(FXCollections.observableArrayList(0, 1, 2, 3));
            getTileType.setValue(0);
            ObservableList<SolverType> solverTypes = FXCollections.observableArrayList(SolverType.values());
            solvers = new ComboBox<>(solverTypes);
            solvers.setValue(solverTypes.get(0));
            FlowPane mainPane = new FlowPane();
            rectangleGroup = new Group();

            int max = 10;


            initGrid(gridSize);
            generateGrid(max);
            mainPane.getChildren().add(rectangleGroup);
            getChildren().add(mainPane);
            getChildren().add(getTileType);
            getChildren().add(solvers);
            TextField params = new TextField();
            Button solve = new Button("Solve Grid");
            TextArea textArea = new TextArea();
            solve.setOnAction(actionEvent -> {
                TileGrid grid = makeTileGrid(max);
                GameSolver gameSolver = new GameSolver(grid);
                int turns = -1;
                switch (solvers.getValue()) {

                    case BRUTE_FORCE -> {
                        int maxSteps = parse(params.getText(), 6);
                        turns = gameSolver.solveByBruteForce(maxSteps);
                        textArea.setText(String.format("Brute Force (max %s): %s turns\nPositions: %s", maxSteps, turns, gameSolver.getSelectedPositions()));
                    }
                    case BRUTE_FORCE_PARALLEL -> {
                        int maxSteps = parse(params.getText(), 6);
                        turns = gameSolver.solveByBruteForceParallelised(maxSteps);
                        textArea.setText(String.format("Brute Force Parallel (max %s): %s turns\nPositions: %s", maxSteps, turns, gameSolver.getSelectedPositions()));
                    }
                    case MC, MC_PLOT -> {
                        int trials = parse(params.getText(), 1000);
                        MCSolver mcSolver = new MCSolver().setTileGrid(grid).setSolutionAttempts(trials).setMinimumPassLevel(Integer.MAX_VALUE);
//                        JobResult jobResult = GameSolving.solveByMonteCarlo(grid, trials, Integer.MAX_VALUE);
                        JobResultExtra jobResult = mcSolver.solveSingle();
                        turns = jobResult.getSteps();
                        textArea.setText(String.format("MC (%s trials): %s turns: %s", trials, turns, jobResult.getBestPositions()));
                        if (solvers.getValue().equals(SolverType.MC_PLOT)) {
                            GameSolving.Histogram histogram = jobResult.getHistogram("");
                            histogram.createFrame();
                        }
                    }
                    case CENTRAL_SQUARE -> {
                        turns = gameSolver.solveByCentralSquare();
                        textArea.setText(String.format("CENTRAL SQUARE: %s turns", turns));
                    }
                    case LARGEST_AREA -> {
                        //TODO implement the solver and wire in
//                            gameSolver.
                    }
                }
                System.out.println("Turns taken: " + turns);
            });
            getChildren().add(params);
            getChildren().add(solve);
            getChildren().add(textArea);

            Button saveGrid = new Button("Save Grid");
            saveGrid.setOnAction(actionEvent -> saveGrid(makeTileGrid(max)));
            Button resetGrid = new Button("Reset Grid");
            resetGrid.setOnAction(ae -> {
                initGrid(gridSize);
            });
            Button rotateGrid = new Button("Rotate Grid");
            rotateGrid.setOnAction(actionEvent -> rotateGrid());
            getChildren().add(saveGrid);
            getChildren().add(resetGrid);
            getChildren().add(rotateGrid);

            ComboBox<GridFile> gridFileComboBox = new ComboBox<>(FXCollections.observableList(PuzzleFileManager.getInstance().getGridFiles()));
//            ComboBox<GridFile> gridFileComboBox = new ComboBox<>(FXCollections.observableList(PuzzleFileManager.get(new File("/Users/francisbostock/Documents/CompactoidPuzzles/Selected/New_20221021/10By10Grids")).getGridFiles()));
            gridFileComboBox.valueProperty().addListener(new ChangeListener<GridFile>() {
                @Override
                public void changed(ObservableValue<? extends GridFile> observableValue, GridFile gridFile, GridFile t1) {
                    loadGrid(gridFileComboBox.getValue());
                }
            });
            getChildren().add(gridFileComboBox);

        }

        private int parse(String text, int defaultValue) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        }

        private void generateGrid(int max) {
            int xWidth = 40;
            int yWidth = 40;

            for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
                Rectangle rectangle = new Rectangle(xWidth * (max + entry.getKey().X), yWidth * (entry.getKey().Y), xWidth, yWidth);
                tilesUI.put(entry.getKey(), rectangle);

                rectangle.setOnMouseClicked(mouseEvent -> {
                    Integer newType = getTileType.getValue();
                    tiles.put(entry.getKey(), newType);
                    rectangle.setFill(getColor(newType));
                });
                rectangle.setFill(getColor(entry.getValue()));
                rectangle.setStroke(Color.BLACK);
                rectangleGroup.getChildren().add(rectangle);
            }
        }

        private TileGrid makeTileGrid(int max) {
            TileGrid tileGrid = new TileGrid(max, max);
            for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
                tileGrid.Add(makeTile(entry.getValue()), entry.getKey());
            }
            return tileGrid;
        }

        private void initGrid(int size) {
            tiles.clear();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Position position = new Position(i - size / 2, j - size / 2);
                    tiles.put(position, 0);//default to blue
                    Rectangle rectangle = tilesUI.get(position);
                    if (rectangle != null) {
                        rectangle.setFill(getColor(0));
                    }
                }
            }
        }

        private void loadGrid(GridFile gridFile) {
            rectangleGroup.getChildren().clear();
            tiles.clear();
            tilesUI.clear();
            int max = 0;
            Map<Position, Integer> tiles = gridFile.getTiles();
            for (Position position : tiles.keySet()) {
                max = Math.max(max, position.X);
            }
            initGrid(max);//assumes grid is square
            this.tiles.putAll(tiles);
            generateGrid(max);
        }

        private void saveGrid(TileGrid tileGrid) {
            HBox box = new HBox();
            box.getChildren().add(new Label("Select title"));
            TextField titleField = new TextField();
            box.getChildren().add(titleField);
            Stage stage = FxUtils.makeDialog(actionEvent -> {
                String text = titleField.getText();
                if (!text.isEmpty()) {
                    try {
                        saveGrid(text, tileGrid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, box);
            stage.show();
        }

        private void rotateGrid() {
            //rotate (x,y)->(-y-1, x) rather than (x,y)->(y, -x-1) because grid drawn positive y downwards.
            Function<Position, Position> rotator = position -> new Position(-position.Y - 1, position.X);
            HashMap<Position, Integer> tilesCopy = new HashMap<>(tiles);
            for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
                Position oldPosition = entry.getKey();
                Position newPosition = rotator.apply(oldPosition);
                tilesCopy.put(newPosition, entry.getValue());
                tilesUI.get(newPosition).setFill(getColor(tiles.get(oldPosition)));
            }
            tiles = tilesCopy;
        }

        private void saveGrid(String name, TileGrid grid) throws IOException {
            PuzzleFileManager.getInstance().createFile(name, grid);
        }

    }

    private enum SolverType {
        BRUTE_FORCE,
        BRUTE_FORCE_PARALLEL,
        MC,
        MC_PLOT,
        CENTRAL_SQUARE,
        LARGEST_AREA
    }

    /*
    1) Check what format we need to write out the tileGrid in C#.
    2) Write a script to import a TileGrid file, then generate the appropriate output for C#.
    3) Work out what puzzles to make. Perhaps 40* 7*7 grids, 40* 8*8, 40 9*9, 40 10*10. They can have different min solutions.

     */
}
