package fjdb.battlegame;

import fjdb.battlegame.coords.GridManager;
import fjdb.battlegame.coords.Location;
import fjdb.graphics.NodeFactory;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Created by Frankie Bostock on 20/08/2017.
 */
public class MainGame extends Application {

    private static final int GRID_SCALE = 50;
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
        int rows = 12;
        int columns = 12;
        GridManager gridManager = new GridManager(columns, rows);
        GraphicsEngine engine = new GraphicsEngine(GRID_SCALE, gridManager);
        engine.addGrid(gc, rows, columns);
        root.getChildren().add(canvas);


        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        engine.getLocationAtPoint(t.getSceneX(), t.getSceneY());
                    }
                });



        Location location00 = gridManager.get(0, 0);
        Location location35 = gridManager.get(3, 5);


        Player player = new Player(NodeFactory.playerGlyph(GRID_SCALE/2.0f));
        Node playerGlypth = player.getGlyph();
        player.setLocation(location00);
        engine.updateUnit(player);

        Enemy enemy = new Enemy(NodeFactory.enemyGlyph(GRID_SCALE/2.0f));
        Node enemyGlypth = enemy.getGlyph();
        enemy.setLocation(location35);
        engine.updateUnit(enemy);
        root.getChildren().add(playerGlypth);
        root.getChildren().add(enemyGlypth);

        playerGlypth.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        engine.getLocationAtPoint(t.getSceneX(), t.getSceneY());
                    }
                });

        enemyGlypth.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        engine.getLocationAtPoint(t.getSceneX(), t.getSceneY());
                    }
                });


        primaryStage.setScene(new Scene(root));
        primaryStage.show();

//TODO associate glyphs to locations on the grid, which will then set their position (translation)
//        correctly i.e. in the centre of the grid square

    }




    private Node addPlayerGlyph(float radius) {
        return NodeFactory.playerGlyph(radius);
    }

    private Node addEnemyGlyph(float radius) {
        return NodeFactory.enemyGlyph(radius);
    }
    /*
    TODO
    Create an animation which moves the player glyph across the board.

    Use Location objects to associate the glyphs with particular positions.
    Add mouse listener to the "board" to extract the location, and map that to the Location object

    Add the ability to click on a glyph,


alt+command+t
     */

    public static abstract class Unit {
        private Location _location;
        private Node _glyph;

        public Unit(Node glyph) {
            _glyph = glyph;
        }

        public void setLocation(Location location) {
            _location = location;
        }

        public Location getLocation() {
            return _location;
        }

        public Node getGlyph() {
            return _glyph;
        }

        public Position getPosition() {
            return new Position(_glyph.getTranslateX(), _glyph.getTranslateY());
        }

    }

    public static class Player extends Unit {
        public Player(Node glyph) {
            super(glyph);
        }

    }

    public static class Enemy extends Unit{

        public Enemy(Node glyph) {
            super(glyph);
        }
    }

    private static class GraphicsEngine {

        private int _gridSize;
        private int _xInset;
        private int _yInset;
        private GridManager _gridManager;

        public GraphicsEngine(int gridSize, GridManager gridManager) {
            _gridSize = gridSize;
            _xInset = gridSize;
            _yInset = gridSize;
            _gridManager = gridManager;
        }

        private void addGrid(GraphicsContext gc, int rows, int columns) {
            NodeFactory.addGrid(gc, _xInset, _yInset, _gridSize, rows, columns);
        }

        public void updateUnit(Unit unit) {
            Location location = unit.getLocation();
            Node node = unit.getGlyph();
            node.setTranslateX(_xInset + location.getX() * _gridSize);
            node.setTranslateY(_yInset + location.getY() * _gridSize);
        }

        public Location getLocationAtPoint(double sceneX, double sceneY) {
            /*
            (sceneX - xInset) / gridSize will give the location
             */
            double xPos = (sceneX - _xInset) / _gridSize;
            double yPos = (sceneY - _yInset) / _gridSize;
            Location location = _gridManager.get((int) xPos, (int) yPos);
            System.out.println(location);
            return location;
        }
    }

    private static class Position {
        private double _x;
        private double _y;

        public Position(double x, double y) {
            _x = x;
            _y = y;
        }

        public double getX() {
            return _x;
        }

        public double getY() {
            return _y;
        }
    }
}
