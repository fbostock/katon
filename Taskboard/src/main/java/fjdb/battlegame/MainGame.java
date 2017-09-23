package fjdb.battlegame;

import fjdb.graphics.NodeFactory;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
//TODO have a canvas whcih will be a static picture in the background. Then we have nodes on top of that.
        Canvas canvas = new Canvas(600, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int gridSize = 50;
        addGrid(gc, gridSize);
        root.getChildren().add(canvas);
        Node player = addPlayerGlyph(gridSize / 2.0f);
        player.setTranslateX(50);
        player.setTranslateY(50);
        root.getChildren().add(player);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void addGrid(GraphicsContext gc, int gridSize) {
        gc.setLineWidth(0.5);
        int xInset = 50;
        int yInset = 50;
        int squareX = gridSize;
        int squareY = gridSize;
        for (int i = 0; i < 10; i++) {
            gc.strokeLine(xInset, yInset + i*squareY, 500, yInset + i*squareY);
        }
        for (int i = 0; i < 10; i++) {
            gc.strokeLine(xInset + i*squareX, yInset, xInset + i*squareX, 500);
        }

    }


    private Node addPlayerGlyph(float radius) {
        return NodeFactory.dropShadow(radius);
    }
    /*
    TODO
    Add a PlayerGlyph which takes the form of two concentric squares.
    Add an enemy glyph which takes the form of a black circle.
    Create an animation which moves the player glyph across the board.

    Use Location objects to associate the glyphs with particular positions.
    Add mouse listener to the "board" to extract the location, and map that to the Location object

    Add the ability to click on a glyph,

     */

}
