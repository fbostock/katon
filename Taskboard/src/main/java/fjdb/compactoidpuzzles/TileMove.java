package fjdb.compactoidpuzzles;

public class TileMove {

    private final GameTile _tile;
    private final Position _previousPosition;
    private final Position _newPosition;
    private final Vector2 targetPosition;

    public TileMove(GameTile tile, Position previousPosition, Position newPosition) {
        _tile = tile;
        _previousPosition = previousPosition;
        _newPosition = newPosition;
        targetPosition = new Vector2(newPosition.X, newPosition.Y);
    }

    public GameTile getTile() {
        return _tile;
    }


    public Position previousPosition() {
        return _previousPosition;
    }

    public Position newPosition() {
        return _newPosition;
    }

    public Vector2 getTargetPosition() {
        return targetPosition;
    }
}
