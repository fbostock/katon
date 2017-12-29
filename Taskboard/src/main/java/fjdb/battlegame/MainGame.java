package fjdb.battlegame;

import fjdb.battlegame.coords.GridManager;
import fjdb.battlegame.coords.Location;
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

    /*
    GameEngine
    - Units


    Graphics Engine
    -Glyphs, each have units





     */




    private static final int GRID_SCALE = 50;
    public static void main(String[] args) {
        launch(args);
    }

    /*
    Use the selected Node to mark a player glyph as selected and when another space is selected, execute a move to that location.
    Check for a right-click trigger or a click outside the game area, and if so clear the selected node.

     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
//TODO have a canvas which will be a static picture in the background. Then we have nodes on top of that.
        Canvas canvas = new Canvas(700, 700);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int rows = 12;
        int columns = 12;
        GridManager gridManager = new GridManager(columns, rows);
        GraphicsEngine engine = new GraphicsEngine(GRID_SCALE, gridManager);
        engine.addGrid(gc, rows, columns);
        root.getChildren().add(canvas);

        final Unit[] selectedNode = {null};

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        Location locationAtPoint = engine.getLocationAtPoint(t.getSceneX(), t.getSceneY());
                        if (Location.isNull(locationAtPoint)) {
                            selectedNode[0] = null;
                        } else {
                            if (selectedNode[0]!= null) {
                                selectedNode[0].setLocation(locationAtPoint);
                                engine.updateUnit(selectedNode[0]);
                            }
                        }
                    }
                });


        Location location00 = gridManager.get(0, 0);
        Location location35 = gridManager.get(3, 5);


        //TODO continue refactor of removing glyphs from units, and instead have units in glyph as a model for the glyph.
        Player player = new Player();
        Node playerGlyph = addPlayerGlyph(player, GRID_SCALE / 2.0f);

        player.setLocation(location00);
        engine.addUnit(player, playerGlyph);

        Enemy enemy = new Enemy(addEnemyGlyph(GRID_SCALE/2.0f));
        Node enemyGlypth = enemy.getGlyph();
        enemy.setLocation(location35);
        engine.addUnit(enemy, enemyGlypth);
        root.getChildren().add(playerGlyph);
        root.getChildren().add(enemyGlypth);

        //TODO the event handlers should be registered somewhere else...but where?
        playerGlyph.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        selectedNode[0] = player;
                        player.setSelected();
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        engine.getLocationAtPoint(t.getSceneX(), t.getSceneY());
                    }
                });

        enemyGlypth.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        selectedNode[0] = enemy;
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        engine.getLocationAtPoint(t.getSceneX(), t.getSceneY());
                    }
                });


        primaryStage.setScene(new Scene(root));
        primaryStage.show();

//TODO associate glyphs to locations on the grid, which will then set their position (translation)
//        correctly i.e. in the centre of the grid square

    }




    private Node addPlayerGlyph(Player player, float radius) {
        return NodeFactory.playerGlyph(player, radius);
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

        public void setSelected() {
        }
    }

    public static class Player extends Unit {
        public Player() {
            //TODO remove glyph from unit
            super(null);
        }

    }

    public static class Enemy extends Unit{

        public Enemy(Node glyph) {
            super(glyph);
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
