package fjdb.compactoidpuzzles;

import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridFile {

    private final String _name;
    private final int _xMax;
    private final int _yMax;
    private final Map<Position, Integer> _tiles;
//    private static String mainPath = "Assets/Resources/Grids";

    public GridFile(String name, int xMax, int yMax, Map<Position, Integer> tiles) {
        _xMax = xMax;
        _yMax = yMax;
        _tiles = tiles;
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public int getXMax() {
        return _xMax;
    }

    public int getYMax() {
        return _yMax;
    }

    public Map<Position, Integer> getTiles() {
        return _tiles;
    }

    public static GridFile makeGrid(String path) throws IOException {
        GridBuilder gridBuilder = new GridBuilder();
        File file = new File(path);
        gridBuilder.setName(file.getName().replace("puzzle", ""));
        List<String> readLines = Files.readLines(file, StandardCharsets.UTF_8);
        for (String line : readLines) {
            if (line.contains("Xmax")) //Xmax:x;Ymax:y
            {
                String[] parts = line.split(";");
                String[] strings = parts[0].split(":");
                int xMax = Integer.parseInt(strings[1]);
                strings = parts[1].split(":");
                int yMax = Integer.parseInt(strings[1]);
                gridBuilder.addXmax(xMax).addYmax(yMax);
            } else if (line.contains("tile")) {
                String[] tiles = line.split(";");
                for (String tile : tiles) {
                    String data = tile.substring(5);
                    String[] xyType = data.split(",");

                    gridBuilder.addTile(xyType[0], xyType[1], xyType[2]);
                }
            } else {
                System.out.println("Unknown line: " + line);
            }
        }

        return gridBuilder.make();
    }

    public static GridFile makeGridFile(String name, TileGrid tileGrid) {
        Map<Position, Integer> tiles = new HashMap<>();
        for (Map.Entry<Position, GameTile> entry : tileGrid.positionsToTiles.entrySet()) {
            tiles.put(entry.getKey(), entry.getValue().type);
        }
        return new GridFile(name, tileGrid._xMax, tileGrid._yMax, tiles);
    }

    public static GridFile createFile(String filename, TileGrid grid) throws IOException {
        int gridXMax = grid._xMax;
        int gridYMax = grid._yMax;
        Map<Position, GameTile> gridPositionsToTiles = grid.positionsToTiles;

        String totalPath = filename;
        try (FileWriter streamWriter = new FileWriter(totalPath, false)) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(streamWriter)) {
                bufferedWriter.write("Xmax:" + gridXMax + ";Ymax:" + gridYMax);
                int i = 0;
                for (Map.Entry<Position, GameTile> entry : gridPositionsToTiles.entrySet()) {
                    {
                        if (entry.getValue() == null) continue;
                        if (i % 10 == 0) {
                            bufferedWriter.newLine();
                        } else {
                            bufferedWriter.write(";");
                        }

                        Position position = entry.getKey();
                        bufferedWriter.write("tile:" + position.X + "," + position.Y + "," + entry.getValue().type);
                        i++;
                    }

                }
                System.out.println("file written to " + totalPath);

            }
        }
        return makeGrid(filename);
    }

    public TileGrid makeTileGrid() {
        TileGrid tileGrid = new TileGrid(_xMax, _yMax);
        for (Map.Entry<Position, Integer> entry : _tiles.entrySet()) {
            GameTile gameTile = new GameTile();
            gameTile.type = entry.getValue();
            tileGrid.Add(gameTile, entry.getKey());
        }
        return tileGrid;
    }

    @Override
    public String toString() {
        return _name;
    }

    private static class GridBuilder {
        private String name;
        private int xMax;
        private int yMax;
        private Map<Position, Integer> tiles = new HashMap<>();

        public GridBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public GridBuilder addXmax(int xMax) {
            this.xMax = xMax;
            return this;
        }

        public GridBuilder addYmax(int yMax) {
            this.yMax = yMax;
            return this;
        }

        public GridBuilder addTile(int x, int y, int type) {
            tiles.put(new Position(x, y), type);
            return this;
        }

        public GridBuilder addTile(String x, String y, String type) {
            return addTile(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(type));
        }

        public GridFile make() {
            return new GridFile(name, xMax, yMax, tiles);
        }

    }

}
