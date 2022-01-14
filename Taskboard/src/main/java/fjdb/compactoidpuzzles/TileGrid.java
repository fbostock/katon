package fjdb.compactoidpuzzles;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * TODO duplicate the c sharp code for TileGrid, the shufflers, and also the monte carlo (and other?) solvers.
 * It should be possible without the TileManager I think.
 */
public class TileGrid {

    public final int _xMax;
    public final int _yMax;
    public Map<GameTile, Position> tilesToPositions = new HashMap<>();
    protected Map<Position, GameTile> positionsToTiles = new HashMap<>();
    private ShuffleAlgorithm _shuffleAlgorithm;
    private HashSet<GameTile> highlightedTiles = new HashSet<GameTile>();

    public TileGrid(int xMax, int yMax) {
        _xMax = xMax;
        _yMax = yMax;
        _shuffleAlgorithm = new ShuffleAlgorithm(this);
    }

    public boolean moveTile(GameTile gameTile, Position newPosition) {
        if (tilesToPositions.containsKey(gameTile)) {
            Position oldPosition = tilesToPositions.get(gameTile);
            if (positionsToTiles.containsKey(newPosition) && positionsToTiles.get(newPosition) != null) {
                System.out.println("Failed to move tile  to " + newPosition);
                return false;
            }

            if (positionsToTiles.containsKey(newPosition)) {
                positionsToTiles.put(newPosition, gameTile);
            } else {
                positionsToTiles.put(newPosition, gameTile);
            }

            tilesToPositions.put(gameTile, newPosition);
            positionsToTiles.put(oldPosition, null);
            return true;
        }

        System.out.println("Failed to move tile as not in grid");
        return false;
    }

    public int countTiles() {
        return tilesToPositions.size();
    }

    public void dispose() {
        for (GameTile tile : tilesToPositions.keySet()) {
            tile.destroy();
        }
        tilesToPositions.clear();
        positionsToTiles.clear();
        // foreach (GameTile gameTile in highlightedTiles) {
        //     gameTile.playHighlightBorder(false);
        // }
        highlightedTiles.clear();
    }

    public boolean Add(GameTile gameTile, int x, int y) {
        Position position = new Position(x, y);
        return Add(gameTile, position);
    }

    public boolean Add(GameTile gameTile, Position position) {
        if (positionsToTiles.get(position) != null) {
            return false;
        } else {
            positionsToTiles.put(position, gameTile);
            tilesToPositions.put(gameTile, position);
        }

//            GameTile tile;
//            if (positionsToTiles.TryGetValue(position, out tile))
//            {
//                if (tile == null)
//                {
//                    positionsToTiles[position] = gameTile;
//                    tilesToPositions.Add(gameTile, position);
//                }
//                else
//                {
//                    // Already a tile in position,
//                    return false;
//                }
//            }
//            else
//            {
//                positionsToTiles.Add(position, gameTile);
//                tilesToPositions.Add(gameTile, position);
//            }

//            if (Math.Abs(position.X) == _xMax - 1 || Math.Abs(position.Y) == _yMax - 1) {
//                highlightedTiles.Add(gameTile);
//                gameTile.playHighlightBorder(true);
//            }
        return true;
    }

    public GameTile getTile(int x, int y) {
        return getTile(new Position(x, y));
    }

    public Position getPosition(GameTile gameTile) {
        return tilesToPositions.get(gameTile);
//            if (tilesToPositions.ContainsKey(gameTile))
//            {
//                return tilesToPositions[gameTile];
//            }
//            return null;
    }

    //        [CanBeNull]
    public GameTile getTile(Position position) {
        return positionsToTiles.get(position);
//            if (positionsToTiles.ContainsKey(position))
//            {
//                return positionsToTiles[position];
//            }
//            return null;
    }

    public HashSet<GameTile> removeTileAndNeighbours(GameTile gameTile) {
        HashSet<GameTile> matchingAdjacentTiles = getMatchingAdjacentTiles(gameTile);
        for (GameTile tile : matchingAdjacentTiles) {
            removeTile(tile);
        }

        return matchingAdjacentTiles;
    }

    public Position removeTile(GameTile gameTile) {
        Position tilePosition = tilesToPositions.get(gameTile);
        tilesToPositions.remove(gameTile);
        positionsToTiles.put(tilePosition, null);
        return tilePosition;
    }

    public void replaceTile(GameTile oldTile, GameTile newTile) {
        Position position = removeTile(oldTile);
        Add(newTile, position);
    }

    private HashSet<GameTile> getMatchingAdjacentTiles(GameTile gameTile) {
        HashSet<GameTile> checkedTiles = Sets.newHashSet(gameTile);
        return getMatchingAdjacentTiles(gameTile, checkedTiles);
    }

    private HashSet<GameTile> getMatchingAdjacentTiles(GameTile gameTile, HashSet<GameTile> checkedTiles) {
        HashSet<GameTile> tiles = Sets.newHashSet(gameTile);
        checkedTiles.add(gameTile);
        HashSet<GameTile> adjacentTiles = getAdjacentTiles(gameTile);
        for (GameTile tile : adjacentTiles) {
            if (!checkedTiles.contains(tile)) {
                checkedTiles.add(tile);
                if (tile.type.equals(gameTile.type)) {
                    // tiles.Add(tile);
                    tiles.addAll(getMatchingAdjacentTiles(tile, checkedTiles));
                }
            }
        }

        return tiles;
    }

    HashSet<GameTile> getAdjacentTiles(GameTile gameTile) {
        HashSet<GameTile> adjacentTiles = new HashSet<GameTile>();
        Position tilesToPosition = tilesToPositions.get(gameTile);
        List<Position> adjacents = tilesToPosition.getAdjacents();
        for (Position adjacent : adjacents) {
            if (positionsToTiles.containsKey(adjacent)) {
                GameTile tile = positionsToTiles.get(adjacent);
                if (tile != null) {
                    adjacentTiles.add(tile);
                }
            }
        }
        return adjacentTiles;
    }

    /**
     * Routine that compresses the grid to fill in all the gaps. Returns a list of TileMoves to indicate what animation movements
     * to carry out.
     */
    public TileMoveCollection updateQuadrants() {
            /*
             TODO
             fill in the gaps in the grid, by working in four quadrants.
             we may want this to return a dictionary of moves (GameTiles to moves), so that each tile only has one target it is moving to.

             In quadrant A, x: >=0, y: >=0, check each row (each y), and for each x see if there is a gap. When there is,
             loop over the rest of the entries in that row, and reassign all the positions at once, starting from where the gap
             occurred.
             Then do quadrant D, then B then C.
             DA
             CB
             TODO create a pool to store position objects (like the java pools).
             TODO create a pool to store tile (GameObjects)
             */
        List<Map<GameTile, TileMove>> newPositions = _shuffleAlgorithm.getAllTileMoves();
        // List<Dictionary<GameTile,TileMove>> newPositions = _shuffleAlgorithm.hemiSphereMoves(true);
        // List<Dictionary<GameTile,TileMove>> newPositions2 = _shuffleAlgorithm.hemiSphereMoves(false);

        //TODO clean this up.
        TileMoveCollection tileMoveCollection = new TileMoveCollection();
        for (int i = 0; i < newPositions.size(); i++) {
            List<TileMove> moves = new ArrayList<TileMove>();
            for (GameTile tile : newPositions.get(i).keySet()) {
                moves.add(newPositions.get(i).get(tile));
            }
            tileMoveCollection.addBatch(moves);
        }

        // HashSet<GameTile> toRemove = new HashSet<GameTile>();
//            for (GameTile tile : highlightedTiles.ToList()) {
//            Position tilePosition = null;
//            tilesToPositions.TryGetValue(tile, out tilePosition);
//            if (tilePosition == null || (Math.Abs(tilePosition.X) != _xMax - 1 && Math.Abs(tilePosition.Y) != _yMax - 1)) {
//                tile.playHighlightBorder(false);
//                highlightedTiles.Remove(tile);
//            }

//        }

        // highlightedTiles.RemoveWhere(tile => toRemove.Contains(tile));

        return tileMoveCollection;
    }


//        private Dictionary<GameTile, TileMove> hemiSphereMoves(bool top)
//        {
//            Dictionary<GameTile, TileMove> newPositions = new Dictionary<GameTile, TileMove>();
//            for (int rowIndex = 0; rowIndex < _yMax; rowIndex++)
//            {
//                int y = top ? rowIndex : -rowIndex;
//                Position firstBlank = new Position(0, y);
//                while (!isBlank(firstBlank))
//                {
//                    firstBlank = firstBlank.right();
//                }
//                //pos is the first blank position. Now, get all the tiles along the rest of the row.
//                List<GameTile> tilesInRow = new List<GameTile>();
//
//
//                Position pos = firstBlank;
//                for (int i = pos.X+1; i < _xMax; i++)
//                {
//                    pos = new Position(i, y);
//                    if (positionsToTiles.ContainsKey(pos))
//                    {
//                        GameTile tile = positionsToTiles[pos];
//                        positionsToTiles[pos] = null;
//                        if (tile != null)
//                        {
//                            tilesInRow.Add(tile);
//                        }
//                    }
//                }
//
//                pos = firstBlank;
//                for (int i = 0; i < tilesInRow.Count; i++)
//                {
//                    GameTile gameTile = tilesInRow[i];
//                    positionsToTiles[pos] = gameTile;
//                    Position oldPosition = tilesToPositions[gameTile];
//                    tilesToPositions[gameTile] = pos;
//                    newPositions.Add(gameTile, new TileMove(gameTile, oldPosition, pos));
//                    pos = pos.right();
//                }
//                tilesInRow.Clear();
//
//                firstBlank = new Position(0, y);
//                while (!isBlank(firstBlank))
//                {
//                    firstBlank = firstBlank.left();
//                }
//
//                pos = firstBlank;
//                for (int i = pos.X-1; i > -_xMax; i--)
//                {
//                    pos = new Position(i, y);
//                    if (positionsToTiles.ContainsKey(pos))
//                    {
//                        GameTile tile = positionsToTiles[pos];
//                        positionsToTiles[pos] = null;
//                        if (tile != null)
//                        {
//                            tilesInRow.Add(tile);
//                        }
//                    }
//                }
//
//                pos = firstBlank;
//                for (int i = 0; i < tilesInRow.Count; i++)
//                {
//                    GameTile gameTile = tilesInRow[i];
//                    positionsToTiles[pos] = gameTile;
//                    Position oldPosition = tilesToPositions[gameTile];
//                    tilesToPositions[gameTile] = pos;
//                    newPositions.Add(gameTile, new TileMove(gameTile, oldPosition, pos));
//                    pos = pos.left();
//                }
//            }
//
//            for (int x = -_xMax; x < _xMax; x++)
//            {
//                Position firstBlank = new Position(x, 0);
//                while (!isBlank(firstBlank))
//                {
//                    firstBlank = top ? firstBlank.above() : firstBlank.below();
//                }
//                //pos is the first blank position. Now, get all the tiles along the rest of the row.
//                List<GameTile> tilesInColumn = new List<GameTile>();
//
//
//                Position pos = firstBlank;
//                for (int rowIndex = Math.Abs(pos.Y)+1; rowIndex < _yMax; rowIndex++)
//                {
//                    int y = top ? rowIndex : -rowIndex;
//                    pos = new Position(x, y);
//                    if (positionsToTiles.ContainsKey(pos))
//                    {
//                        GameTile tile = positionsToTiles[pos];
//                        positionsToTiles[pos] = null;
//                        if (tile != null)
//                        {
//                            tilesInColumn.Add(tile);
//                        }
//                    }
//                }
//
//                pos = firstBlank;
//                for (int i = 0; i < tilesInColumn.Count; i++)
//                {
//                    GameTile gameTile = tilesInColumn[i];
//                    positionsToTiles[pos] = gameTile;
//                    Position oldPosition = tilesToPositions[gameTile];
//                    tilesToPositions[gameTile] = pos;
//                    newPositions[gameTile] = new TileMove(gameTile, oldPosition, pos);
//                    pos = top ? pos.above(): pos.below();
//                }
//
//            }
//
//            return newPositions;
//        }

//        private bool isBlank(Position position)
//        {
//            return !positionsToTiles.ContainsKey(position) || positionsToTiles[position] == null;
//        }
//        private bool hasAnyTiles(List<Position> positions)
//        {
//            foreach (Position position in positions)
//            {
//                if (positionsToTiles.ContainsKey(position))
//                {
//                    return true;
//                }
//
//            }
//
//            return false;
//        }

//        private List<Position> getRing(int l)
//        {
//            List<Position> positions = new List<Position>();
//            for (int i = 0; i <= l; i++)
//            {
//                positions.Add(new Position(i, l));
//            }
//
//            for (int i = l - 1; i >= -l; i--)
//            {
//                positions.Add(new Position(l, i));
//            }
//
//            for (int i = l - 1; i >= -l; i--)
//            {
//                positions.Add(new Position(i, -l));
//            }
//
//            for (int i = -l + 1; i <= l; i++)
//            {
//                positions.Add(new Position(-l, i));
//            }
//
//            for (int i = -l + 1; i < 0; i++)
//            {
//                positions.Add(new Position(i, l));
//            }
//
//            return positions;
//        }

//        private List<Position> waysToFall(Position position)
//        {
//            List<Position> toFall = new List<Position>();
//            int positionY = position.Y;
//            int positionX = position.X;
//            if (positionX == 0)
//            {
//                toFall.Add(new Position(positionX, positionY + (positionY > 0 ? -1 : +1)));
//            }
//            else if (positionY == 0)
//            {
//                toFall.Add(new Position(positionX + (positionX > 0 ? -1 : +1), positionY));
//            }
//            else
//            {
//                if (positionX == positionY || positionX == -positionY)
//                {
//                    toFall.Add(new Position(positionX + (positionX > 0 ? -1 : +1),
//                            positionY + (positionY > 0 ? -1 : +1)));
//                }
//                else
//                {
//                    //x,y = 1,2 falls down or left, with down first, cos 1,1 closer to 0,0 than 0,2
//                    int newX = positionX > 0 ? positionX - 1 : positionX + 1;
//                    int newY = positionY > 0 ? positionY - 1 : positionY + 1;
//                    if (Math.Abs(positionX) > Math.Abs(positionY))
//                    {
//                        toFall.Add(new Position(newX, positionY));
//                        toFall.Add(new Position(positionX, newY));
//                    }
//                    else
//                    {
//                        toFall.Add(new Position(positionX, newY));
//                        toFall.Add(new Position(newX, positionY));
//                    }
//                }
//            }
//
//            return toFall;
//        }

    public List<Integer> nonEmptyRows() {
        return nonEmptyDimension(false);
    }

    public List<Integer> nonEmptyColumns() {
        return nonEmptyDimension(true);
    }

    public List<Integer> nonEmptyDimension(boolean columns) {
        List<Integer> dimensionIndices = new ArrayList<>();
        int iMax = columns ? _xMax : _yMax;
        int jMax = columns ? _yMax : _xMax;
        for (int i = -iMax; i < iMax; i++) {
            for (int j = -jMax; j < jMax; j++) {
                Position position = columns ? new Position(i, j) : new Position(j, i);
                if (positionsToTiles.containsKey(position)) {
                    if (positionsToTiles.get(position) != null) {
                        dimensionIndices.add(i);
                        break;
                    }
                }
            }
        }

        return dimensionIndices;
    }

    /**
     * DEBUGGING: check if the gametile still exists in the grid.
     */
    public void checkDestroyed(GameTile gameTile) {
        if (tilesToPositions.containsKey(gameTile)) {
            Position tilePosition = tilesToPositions.get(gameTile);
            System.out.println("ERROR Tile " + gameTile + " destroyed but still in grid at " + tilePosition);
        }
    }
}



