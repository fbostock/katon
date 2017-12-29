package fjdb.battlegame;

import fjdb.battlegame.coords.GridManager;
import fjdb.battlegame.coords.Location;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by francisbostock on 18/11/2017.
 */
public class GraphicsEngine {



    private int _gridSize;
    private int _xInset;
    private int _yInset;
    private GridManager _gridManager;
    private Map<MainGame.Unit, Node> _unitGlyphs = new HashMap<>();

    public GraphicsEngine(int gridSize, GridManager gridManager) {
        _gridSize = gridSize;
        _xInset = gridSize;
        _yInset = gridSize;
        _gridManager = gridManager;
    }

    public void addGrid(GraphicsContext gc, int rows, int columns) {
        NodeFactory.addGrid(gc, _xInset, _yInset, _gridSize, rows, columns);
    }

    public void addUnit(MainGame.Unit unit, Node glyph) {
        _unitGlyphs.put(unit, glyph);
        updateUnit(unit);
    }

    public void updateUnit(MainGame.Unit unit) {
        Location location = unit.getLocation();
        Node node = getNode(unit);
        node.setTranslateX(_xInset + location.getX() * _gridSize);
        node.setTranslateY(_yInset + location.getY() * _gridSize);
    }

    private Node getNode(MainGame.Unit unit) {
        return _unitGlyphs.get(unit);
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


}
