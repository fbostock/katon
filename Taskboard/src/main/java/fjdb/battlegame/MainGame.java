package fjdb.battlegame;

import fjdb.battlegame.coords.GridManager;
import fjdb.battlegame.coords.Location;
import fjdb.battlegame.units.Enemy;
import fjdb.battlegame.units.Player;
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

    /*

    Make selected glyphs appear highlighted.
      When a unit becomes selected, need to update glyph.

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
        //A canvas which will be a static picture in the background. Then nodes exist on top
        Canvas canvas = new Canvas(700, 700);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int rows = 12;
        int columns = 12;
        GridManager gridManager = new GridManager(columns, rows);

        GameEngine gameEngine = new GameEngine(gridManager);
        GraphicsEngine graphicsEngine = new GraphicsEngine(GRID_SCALE, gridManager, root, canvas, gameEngine);
        gameEngine.setGraphicsEngine(graphicsEngine);
        graphicsEngine.addGrid(gc, rows, columns);

        Location location00 = gridManager.get(0, 0);
        Location location35 = gridManager.get(3, 5);

        Player player = new Player();
        NodeFactory.Glyph playerGlyph = NodeFactory.playerGlyph(player, GRID_SCALE / 2.0f);

        player.setLocation(location00);
        graphicsEngine.addUnit(player, playerGlyph);

        Enemy enemy = new Enemy(NodeFactory.enemyGlyph(GRID_SCALE / 2.0f));
        NodeFactory.Glyph enemyGlypth = enemy.getGlyph();
        enemy.setLocation(location35);
        graphicsEngine.addUnit(enemy, enemyGlypth);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        gameEngine.start();

    }




    /*
    TODO
    Create an animation which moves the player glyph across the board.

    Use Location objects to associate the glyphs with particular positions.
    Add mouse listener to the "board" to extract the location, and map that to the Location object

    Add the ability to click on a glyph,


alt+command+t
     */



    /*
    Potential route building efficiencies
    - Differentiate between routes based purely on the terrain, and routes which are based on what is currently passable.
    - cache main routes between points, so when looking for alternative routes, it has a starting point.
    Still need to decide if the routeAlgo interface should getRoute or getRoutes.

    Other algorithms to try:
     - when checking through A to B, as a first pass do not let the algo check locations which would take the route
     further away from A and B, e.g. if A=1,1 and B = 3,4, only let the algo check locations 1<= X <=3, 1<= Y <=4,

     - An algorithm which first tries as direct as line as possible, then gradually works backwards checking all
     points along the route for possibilities.
     */



}
