package fjdb.compactoidpuzzles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TileMoveCollection {
    // private readonly List<TileMove> _tileMoves = new List<TileMove>();
    private final List<List<TileMove>> collection = new ArrayList<>();

    public TileMoveCollection() {
    }

    public TileMoveCollection(List<TileMove> tileMoves) {
        // _tileMoves = tileMoves;
        collection.add(tileMoves);
    }

    public int RemoveAll(Predicate<TileMove> match) {

        if (collection.size() > 0) {
            List<TileMove> tileMoves = collection.get(0);
            int count = (int) tileMoves.stream().filter(match).count();
            boolean removeAll = tileMoves.removeIf(match);
            if (tileMoves.size() == 0) {
                collection.remove(0);
            }
            return count;
        }

        return 0;
        // return _tileMoves.RemoveAll(match);
    }

    // public int Count => _tileMoves.Count;
    // public int Count => collection.Count;
    public int Count() {
        int total = 0;
        for (List<TileMove> moves : collection) {
            total += moves.size();
        }
        return total;
    }

    public int CollectionCount() {
        return collection.size();
    }

    public List<TileMove> getCurrentBatch() {
        if (collection.size() > 0) {
            return collection.get(0);
        }
        return new ArrayList<>();
        // return _tileMoves;
    }

    public void addBatch(List<TileMove> tileMoves) {
        collection.add(tileMoves);
        // _tileMoves.AddRange(tileMoves);
    }

    public void addBatch(TileMoveCollection tileMoveCollection) {
        collection.addAll(tileMoveCollection.collection);
        // _tileMoves.AddRange(tileMoveCollection._tileMoves);
    }

    public void clear() {
        for (List<TileMove> tileMoves : collection) {
            tileMoves.clear();
        }
        collection.clear();
    }
}
