package fjdb.compactoidpuzzles.generators;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fjdb.compactoidpuzzles.GameTile;
import fjdb.compactoidpuzzles.Position;
import fjdb.compactoidpuzzles.TileGrid;
import fjdb.compactoidpuzzles.TileProducer;
import fjdb.util.ListUtil;

import java.util.*;

/**
 * A tile grid generator which uses large scale shapes and places them in random positions in the grid. The aim is to
 * provide greater likelihood of having regions with matching adjacent tiles, which are likely to be solvable with a
 * few turns.
 */
public class RegionalGenerator implements GGenerator {

    Random random = new Random();

    private final int gridSize;

    public RegionalGenerator(int gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public TileGrid makeGrid(TileProducer tileProducer) {
        TileGrid tileGrid = new TileGrid(10, 10);
        Map<Position, GameTile> gameTiles = new HashMap<>();

        List<Position> initialPositions = Lists.newArrayList();

        int x = -(gridSize) / 2;
        for (int i = 0; i < gridSize; i++) {
            int y = -(gridSize) / 2;
            for (int j = 0; j < gridSize; j++) {
                initialPositions.add(new Position(x, y));
                y++;
            }
            x++;
        }

        List<Position> positions = ListUtil.randomiseOrder(initialPositions);

        while (!positions.isEmpty()) {
            Position position = positions.get(0);
            List<Position> positionsToFill = getPositions(position);
            int type = random.nextInt(4);
            for (Position toFill : positionsToFill) {
                GameTile value = new GameTile();
                value.type = type;
                gameTiles.put(toFill, value);
            }
            positions.removeAll(positionsToFill);
        }

        for (Position position : gameTiles.keySet()) {
            tileGrid.Add(gameTiles.get(position), position);
        }
        return tileGrid;
    }

    private List<Position> getPositions(Position position) {
        List<Position> positions = generatePositions(position);
        positions.removeIf(this::outOfBounds);
        return positions;
    }

    private boolean outOfBounds(Position position) {
//        return position.X < -gridSize / 2 || position.X > gridSize / 2 || position.Y < -gridSize / 2 || position.Y > gridSize / 2;
        return position.X < -gridSize / 2 || position.X >= gridSize / 2 || position.Y < -gridSize / 2 || position.Y >= gridSize / 2;
    }

    private List<Position> generatePositions(Position position) {
        int i = random.nextInt(0, 4);
        if (i == 0) {
            return getSquare(position);
        } else if (i == 1) {
            return getStick(position, random.nextBoolean());
        } else if (i == 2) {
            return getOffsetShape(position, random.nextBoolean());
        } else if (i == 3) {
            return getLShape(position, random.nextBoolean());
        }
        throw new RuntimeException("Random generator out of bounds");
    }

    private List<Position> getSquare(Position position) {
        List<Position> positions = Lists.newArrayList();
        positions.add(position);
        positions.add(position.right());
        Position below = position.below();
        positions.add(below);
        positions.add(below.right());
        return positions;
    }

    private List<Position> getStick(Position position, boolean horizontal) {
        List<Position> positions = Lists.newArrayList();
        positions.add(position);
        if (horizontal) {
            Position right = position.right();
            positions.add(right);
            right = right.right();
            positions.add(right);
            right = right.right();
            positions.add(right);
        } else {
            Position below = position.below();
            positions.add(below);
            below = below.below();
            positions.add(below);
            below = below.below();
            positions.add(below);
        }
        return positions;
    }

    private List<Position> getOffsetShape(Position position, boolean left) {
        List<Position> positions = Lists.newArrayList();
        positions.add(position);
        Position next = position.below();
        positions.add(next);
        next = left ? next.left() : next.right();
        positions.add(next);
        next = next.below();
        positions.add(next);
        return positions;
    }

    private List<Position> getLShape(Position position, boolean left) {
        List<Position> positions = Lists.newArrayList();
        positions.add(position);
        Position next = position.below();
        positions.add(next);
        next = next.below();
        positions.add(next);
        next = left ? next.left() : next.right();
        positions.add(next);
        return positions;
    }


}
