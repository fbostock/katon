package fjdb.compactoidpuzzles;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PuzzleDisplay extends Application {

    public static void main(String[] args) throws IOException {


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
        flowPane.getChildren().add(drawGrid());
        flowPane.getChildren().add(new Rectangle(100, 1, Color.WHITE));
//        FlowPane flowPane = drawGrid();


        sceneRoot.setCenter(flowPane);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();
    }

    private FlowPane drawGrid() throws IOException {
        FlowPane mainPane = new FlowPane();

        File fileDirectory = new File("/Users/francisbostock/Documents/CompactoidPuzzles");

        GridFile gridFile = GridFile.makeGrid(fileDirectory + "/puzzle1.txt");

        Map<Position, Integer> tiles = gridFile.getTiles();

        Group group = new Group();
//        Rectangle rectangle = new Rectangle(20, 20, 20, 20);
//        group.getChildren().add(rectangle);
//        rectangle = new Rectangle(40, 20, 20, 20);
//        group.getChildren().add(rectangle);
//        rectangle = new Rectangle(20, 40, 20, 20);
//        group.getChildren().add(rectangle);
//        rectangle = new Rectangle(40, 40, 20, 20);
//        group.getChildren().add(rectangle);
//        rectangle = new Rectangle(40, 40, 20, 20);
//        mainPane.getChildren().add(rectangle);

        int xWidth = 40;
        int yWidth = 40;

        for (Map.Entry<Position, Integer> entry : tiles.entrySet()) {
            Rectangle rectangle = new Rectangle(xWidth * (gridFile.getXMax() + entry.getKey().X), yWidth * (entry.getKey().Y), xWidth, yWidth);
            rectangle.setFill(getColor(entry.getValue()));
            rectangle.setStroke(Color.BLACK);
//            rectangle.setWidth();
//            rectangle.setHeight();
            group.getChildren().add(rectangle);
        }


        mainPane.getChildren().add(group);
        return mainPane;
    }

    private Paint getColor(int type) {
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

}
