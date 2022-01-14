package fjdb.compactoidpuzzles;

import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ShuffleAlgorithm
{
    private final TileGrid _grid;
    private final boolean shiftVerticalFirst = true;

    public ShuffleAlgorithm(TileGrid grid)
    {
        _grid = grid;
    }

    private Position firstBlankM(Position initial,
                                 Function<Position, Position> next)
    {
        // TileGrid.Position firstBlank = new TileGrid.Position(0, y);
        Position firstBlank = initial;
        while (!isBlank(firstBlank))
        {
            firstBlank = next.apply(firstBlank);
        }

        return firstBlank;
    }

    private List<GameTile> remainingInRow(Position firstBlank, boolean left)
    {
        List<GameTile> tilesInRow = new ArrayList<>();
        Position pos = firstBlank;
        for (int i = Math.abs(pos.X) + 1; i < _grid._xMax; i++)
        {
            int x = left ? i : -i;
            pos = new Position(x, pos.Y);
            if (_grid.positionsToTiles.containsKey(pos))
            {
                GameTile tile = _grid.positionsToTiles.get(pos);
                _grid.positionsToTiles.put(pos,  null);
                if (tile != null)
                {
                    tilesInRow.add(tile);
                }
            }
        }

        return tilesInRow;
    }

    private List<GameTile> remainingInColumn(Position firstBlank, boolean up)
    {
        List<GameTile> tilesToMove = new ArrayList<>();
        Position pos = firstBlank;
        for (int i = Math.abs(pos.Y) + 1; i < _grid._yMax; i++)
        {
            int y = up ? i : -i;
            pos = new Position(pos.X, y);
            if (_grid.positionsToTiles.containsKey(pos))
            {
                GameTile tile = _grid.positionsToTiles.get(pos);
                _grid.positionsToTiles.put(pos,  null);
                if (tile != null)
                {
                    tilesToMove.add(tile);
                }
            }
        }
        return tilesToMove;
    }

    private void updatePositions(Position firstBlank, Function<Position, Position> next, List<GameTile> tilesInRow, Map<GameTile, TileMove> newPositions)
    {
        Position pos = firstBlank;
        for (int i = 0; i < tilesInRow.size(); i++)
        {
            GameTile gameTile = tilesInRow.get(i);
            _grid.positionsToTiles.put(pos,  gameTile);
            Position oldPosition = _grid.tilesToPositions.get(gameTile);
            _grid.tilesToPositions.put(gameTile,  pos);
            if (newPositions.containsKey(gameTile))
            {
                newPositions.put(gameTile, new TileMove(gameTile, oldPosition, pos));
            }
            else
            {

                newPositions.put(gameTile, new TileMove(gameTile, oldPosition, pos));
            }
            pos = next.apply(pos);
        }
    }

    public List<Map<GameTile, TileMove>> hemiSphereMoves(boolean top)
    {
        if (shiftVerticalFirst)
        {
            return hemiSphereMovesVerticalFirst(top);
        }
        else
        {
            return hemiSphereMovesHorizonalFirst(top);
        }
    }

    public List<Map<GameTile, TileMove>> getAllTileMoves()
    {
        List<Map<GameTile,TileMove>> moves = new ArrayList<>();

        Map<GameTile,TileMove> tileMoves = shiftVerticalFirst ? shuffleVertical(true) : shuffleLateral(true);
        Map<GameTile,TileMove> tileMoves2 = shiftVerticalFirst ? shuffleVertical(false) : shuffleLateral(false);
        for (Map.Entry<GameTile, TileMove> entry : tileMoves2.entrySet()) {
            tileMoves.put(entry.getKey(), entry.getValue());
        }

        moves.add(tileMoves);

        tileMoves = shiftVerticalFirst ? shuffleLateral(true) : shuffleVertical(true);
        tileMoves2 = shiftVerticalFirst ? shuffleLateral(false) : shuffleVertical(false);
        for (Map.Entry<GameTile, TileMove> entry : tileMoves2.entrySet()) {
            tileMoves.put(entry.getKey(), entry.getValue());
        }
        moves.add(tileMoves);

        return moves;
    }

    public List<Map<GameTile, TileMove>> hemiSphereMovesHorizonalFirst(boolean top)
    {
        Map<GameTile, TileMove> newPositions = new HashMap<>();
        for (int rowIndex = 0; rowIndex < _grid._yMax; rowIndex++)
        {
            int y = top ? rowIndex : -rowIndex;
            Position firstBlank = firstBlankM(new Position(0, y), Position::right);

            //firstBlank is the first blank position. Now, get all the tiles along the rest of the row.
            List<GameTile> tilesInRow = new ArrayList<>();

            tilesInRow.addAll(remainingInRow(firstBlank, true));
            updatePositions(firstBlank, Position::right, tilesInRow, newPositions);
            tilesInRow.clear();

            firstBlank = firstBlankM(new Position(0, y), Position::left);

            tilesInRow.addAll(remainingInRow(firstBlank, false));

            updatePositions(firstBlank, Position::left, tilesInRow, newPositions);
        } //end of loop over rows

        for (int x = -_grid._xMax; x < _grid._xMax; x++)
        {
            Position firstBlank = firstBlankM(new Position(x, 0), position -> top ? position.above() : position.below());

            //pos is the first blank position. Now, get all the tiles along the rest of the row.
            List<GameTile> tilesInColumn = new ArrayList<>();

            tilesInColumn.addAll(remainingInColumn(firstBlank, top));

            updatePositions(firstBlank, position -> top ? position.above() : position.below(), tilesInColumn, newPositions);
        }

        List<Map<GameTile,TileMove>> movesList = new ArrayList<>();
        movesList.add(newPositions);
        return movesList;
        // return newPositions;
    }

    public List<Map<GameTile, TileMove>> hemiSphereMovesVerticalFirst(boolean top)
    {
        List<Map<GameTile,TileMove>> movesList = new ArrayList<>();

        Map<GameTile, TileMove> newPositions = new HashMap<>();
        for (int x = -_grid._xMax; x < _grid._xMax; x++)
        {
            Position firstBlank = firstBlankM(new Position(x, 0),
                    position -> top ? position.above() : position.below());

            //pos is the first blank position. Now, get all the tiles along the rest of the row.
            List<GameTile> tilesInColumn = new ArrayList<>();

            tilesInColumn.addAll(remainingInColumn(firstBlank, top));

            updatePositions(firstBlank, position -> top ? position.above() : position.below(), tilesInColumn, newPositions);
        }

        movesList.add(newPositions);
        newPositions = new HashMap<>();


        for (int rowIndex = 0; rowIndex < _grid._yMax; rowIndex++)
        {
            int y = top ? rowIndex : -rowIndex;
            Position firstBlank = firstBlankM(new Position(0, y), Position::right);

            //firstBlank is the first blank position. Now, get all the tiles along the rest of the row.
            List<GameTile> tilesInRow = new ArrayList<>();

            tilesInRow.addAll(remainingInRow(firstBlank, true));
            updatePositions(firstBlank, Position::right, tilesInRow, newPositions);
            tilesInRow.clear();

            firstBlank = firstBlankM(new Position(0, y), Position::left);

            tilesInRow.addAll(remainingInRow(firstBlank, false));

            updatePositions(firstBlank, Position::left, tilesInRow, newPositions);
        } //end of loop over rows

        movesList.add(newPositions);

        // return newPositions;
        return movesList;
    }

    private Map<GameTile, TileMove> shuffleVertical(boolean top)
    {
        Map<GameTile, TileMove> newPositions = new HashMap<>();
        for (int x = -_grid._xMax; x < _grid._xMax; x++)
        {
            Position firstBlank = firstBlankM(new Position(x, 0),
                    position -> top ? position.above() : position.below());

            //pos is the first blank position. Now, get all the tiles along the rest of the row.
            List<GameTile> tilesInColumn = new ArrayList<>();

            tilesInColumn.addAll(remainingInColumn(firstBlank, top));

            updatePositions(firstBlank, position -> top ? position.above() : position.below(), tilesInColumn, newPositions);
        }

        return newPositions;
    }

    private Map<GameTile, TileMove> shuffleLateral(boolean top)
    {
        Map<GameTile, TileMove> newPositions = new HashMap<GameTile, TileMove>();

        for (int rowIndex = 0; rowIndex < _grid._yMax; rowIndex++)
        {
            int y = top ? rowIndex : -rowIndex;
            Position firstBlank = firstBlankM(new Position(0, y), position -> position.right());

            //firstBlank is the first blank position. Now, get all the tiles along the rest of the row.
            List<GameTile> tilesInRow = new ArrayList<>();

            tilesInRow.addAll(remainingInRow(firstBlank, true));
            updatePositions(firstBlank, position -> position.right(), tilesInRow, newPositions);
            tilesInRow.clear();

            firstBlank = firstBlankM(new Position(0, y), position -> position.left());

            tilesInRow.addAll(remainingInRow(firstBlank, false));

            updatePositions(firstBlank, position -> position.left(), tilesInRow, newPositions);
        } //end of loop over rows

        return newPositions;
    }


    private boolean isBlank(Position position)
    {
        return !_grid.positionsToTiles.containsKey(position) || _grid.positionsToTiles.get(position) == null;
    }
}
