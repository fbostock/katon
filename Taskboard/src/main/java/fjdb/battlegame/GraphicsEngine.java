package fjdb.battlegame;

import fjdb.battlegame.coords.GridManager;
import fjdb.battlegame.coords.Location;
import fjdb.battlegame.units.Player;
import fjdb.battlegame.units.Unit;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by francisbostock on 18/11/2017.
 */
public class GraphicsEngine {

    private static final int DELAY = 200;
    private Unit selectedUnit;

    private int _gridSize;
    private int _xInset;
    private int _yInset;
    private GridManager _gridManager;
    private Group _root;
    private Canvas _canvas;
    GameEngine _gameEngine;
    private static final ExecutorService service = Executors.newFixedThreadPool(10);
    public Map<Unit, NodeFactory.Glyph> _unitGlyphs = new HashMap<>();
    TextField playerCountDisplay = new TextField();
    TextField enemyCountDisplay = new TextField();
    int playerCount = 0;
    int enemyCount = 0;



    public GraphicsEngine(int gridSize, GridManager gridManager, Group root, Canvas canvas, GameEngine gameEngine) {
        _gridSize = gridSize;
        _xInset = gridSize;
        _yInset = gridSize;
        _gridManager = gridManager;
        _root = root;
        _canvas = canvas;
        _gameEngine = gameEngine;

        _canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        Location locationAtPoint = getLocationAtPoint(t.getSceneX(), t.getSceneY());
                        if (Location.isNull(locationAtPoint)) {
                            clearSelected();
                        } else {
                            if (getSelectedUnit()!= null) {
                                //TODO submit this to the gameEngine, to calcualte the route, and request the graphics engine
                                //to update the location accordingly. It would then handle cancellations.
                                _gameEngine.updateUnitLocation(getSelectedUnit(), locationAtPoint);
                            }
                        }
                    }
                });
        _root.getChildren().add(canvas);

        playerCountDisplay.setText(String.valueOf(0));
        enemyCountDisplay.setText(String.valueOf(0));
        playerCountDisplay.setTranslateX(5);
        playerCountDisplay.setTranslateY(5);
        enemyCountDisplay.setTranslateX(200);
        enemyCountDisplay.setTranslateY(5);

        _root.getChildren().add(playerCountDisplay);
        _root.getChildren().add(enemyCountDisplay);
    }

    public void addGrid(GraphicsContext gc, int rows, int columns) {
        NodeFactory.addGrid(gc, _xInset, _yInset, _gridSize, rows, columns);
    }

    public void addUnit(Unit unit, NodeFactory.Glyph glyph) {
        _unitGlyphs.put(unit, glyph);
        glyph.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        setSelectedUnit(unit);
                        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
                        getLocationAtPoint(t.getSceneX(), t.getSceneY());
                    }
                });
        _root.getChildren().add(glyph);
        updateUnit(unit);
        updateCounts(unit, true);
    }

    private void updateCounts(Unit unit, boolean add) {
        int change = add? 1 : -1;
        if (unit instanceof Player) {
            playerCount+=change;
            playerCountDisplay.setText(String.valueOf(playerCount));
        } else {
            enemyCount+=change;
            enemyCountDisplay.setText(String.valueOf(enemyCount));
        }
    }

    public void removeUnit(Unit unit) {
        NodeFactory.Glyph glyph = _unitGlyphs.remove(unit);
        Platform.runLater(()-> {
            _root.getChildren().remove(glyph);
            updateCounts(unit, false);
        });
        //TODO 28May2018 - anything else to do?
    }

    public void updateUnit(Unit unit) {
        Location location = unit.getLocation();
        NodeFactory.Glyph node = getNode(unit);
        node.setSelected(unit.isSelected());
        Coord coord = new Coord(location);
//        node.setTranslateX(coord.getX());
//        node.setTranslateY(coord.getY());

        TranslateTransition tt = new TranslateTransition();
        tt.setNode(node);

        tt.fromXProperty().set(node.getTranslateX());
        tt.fromYProperty().set(node.getTranslateY());

        tt.toXProperty().set(coord.getX());
        tt.toYProperty().set(coord.getY());

//        tt.fromXProperty().bind(node.translateXProperty().
//                set(r.getLayoutBounds().getWidth() + 20));
//        tt.fromYProperty().bind(r.translateYProperty().
//                add((r.getLayoutBounds().getHeight() -
//                        text.getLayoutBounds().getHeight()) / 2));

//        tt.toXProperty().bind(r.translateXProperty().
//                subtract(text.getLayoutBounds().getWidth() + 20));
//        tt.toYProperty().bind(tt.fromYProperty());
        tt.setDuration(new Duration(DELAY));
        tt.setInterpolator(Interpolator.LINEAR);
//        tt.setAutoReverse(true);
//        tt.setCycleCount(Timeline.INDEFINITE);
        tt.playFromStart();
     }

     public void animateUnit(Unit unit, Location start, Location end) {
        //TODO get the engine to perform a smooth transition of the unit from start to end. This may have to provide
//         feedback so the engine knows it can do other things
         //OR...perhaps it can check on a field of the unit to know if it can continue updating?
     }

    private NodeFactory.Glyph getNode(Unit unit) {
        return _unitGlyphs.get(unit);
    }




    public void setSelectedUnit(Unit newUnit) {
        clearSelected();
        selectedUnit = newUnit;
        newUnit.setSelected(true);
        updateUnit(newUnit);
//        System.out.println(String.format("%s %s", t.getSceneX(), t.getSceneY()));
//        getLocationAtPoint(t.getSceneX(), t.getSceneY());

    }

    public void highlightLocation(Location location) {

    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void clearSelected() {
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
            updateUnit(selectedUnit);
            selectedUnit = null;
        }
    }

    public Location getLocationAtPoint(double sceneX, double sceneY) {
            /*
            (sceneX - xInset) / gridSize will give the location
             */
        double xPos = (sceneX - _xInset) / _gridSize;
        double yPos = (sceneY - _yInset) / _gridSize;
        xPos = xPos < 0.0 ? -1.0 : xPos;
        yPos = yPos < 0.0 ? -1.0 : yPos;
        Location location = _gridManager.get((int) xPos, (int) yPos);
        System.out.println(String.format("%s %s %s", xPos, yPos, location));
        return location;
    }

    private class Coord {
        final double x;
        final double y;
        public Coord(Location location) {
            x = _xInset + location.getX() * _gridSize;
            y = _yInset + location.getY() * _gridSize;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
