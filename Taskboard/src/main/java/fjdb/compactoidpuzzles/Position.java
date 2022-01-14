package fjdb.compactoidpuzzles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Position {
    public final int X;
    public final int Y;

    public Position(int x, int y) {
        X = x;
        Y = y;
    }

//    public Position getPosition(Vector2 vector) {
//        if (Vector2.up.Equals(vector)) {
//            return above();
//        } else if (Vector2.left.Equals(vector)) {
//            return left();
//        } else if (Vector2.down.Equals(vector)) {
//            return below();
//        } else if (Vector2.right.Equals(vector)) {
//            return right();
//        }
//
//        return null;
//    }

    public Position above() {
        return new Position(X, Y + 1);
    }

    public Position below() {
        return new Position(X, Y - 1);
    }

    public Position left() {
        return new Position(X - 1, Y);
    }

    public Position right() {
        return new Position(X + 1, Y);
    }

    public List<Position> getAdjacents() {
        List<Position> adjacents = new ArrayList<Position>();
        adjacents.add(above());
        adjacents.add(right());
        adjacents.add(below());
        adjacents.add(left());
        return adjacents;
    }


    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position position = (Position) other;
            return this.X == position.X && this.Y == position.Y;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y);
    }

    @Override
    public String toString() {
        return "(" + X + "," + Y + ")";
    }
}
