package fjdb.battlegame;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

/**
 * Created by Frankie Bostock on 20/08/2017.
 */
public class MainGame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(600, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        addGrid(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void addGrid(GraphicsContext gc) {
        gc.setLineWidth(0.5);
        int xInset = 50;
        int yInset = 50;
        int squareX = 50;
        int squareY = 50;
        for (int i = 0; i < 10; i++) {
            gc.strokeLine(xInset, yInset + i*squareY, 500, yInset + i*squareY);
        }
        for (int i = 0; i < 10; i++) {
            gc.strokeLine(xInset + i*squareX, yInset, xInset + i*squareX, 500);
        }

    }
}
