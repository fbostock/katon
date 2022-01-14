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

    private final int _xMax;
    private final int _yMax;
    private final Map<Position, Integer> _tiles;
//    private static String mainPath = "Assets/Resources/Grids";

    public GridFile(int xMax, int yMax, Map<Position, Integer> tiles) {
        _xMax = xMax;
        _yMax = yMax;
        _tiles = tiles;
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
        List<String> readLines = Files.readLines(new File(path), StandardCharsets.UTF_8);
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

    public static GridFile makeGridFile(TileGrid tileGrid) {
        Map<Position, Integer> tiles = new HashMap<>();
        for (Map.Entry<Position, GameTile> entry : tileGrid.positionsToTiles.entrySet()) {
            tiles.put(entry.getKey(), entry.getValue().type);
        }
        return new GridFile(tileGrid._xMax, tileGrid._yMax, tiles);
    }

    public static void createFile(String filename, TileGrid grid) throws IOException {
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
//                                    bufferedWriter.flush();
//                    bufferedWriter.close();
                System.out.println("file written to " + totalPath);

            }
        }
    }

    private static class GridBuilder {
        private int xMax;
        private int yMax;
        private Map<Position, Integer> tiles = new HashMap<>();

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
            return new GridFile(xMax, yMax, tiles);
        }

    }

    public static void writeFile(String path) throws IOException {
        String totalPath = path;
        FileWriter streamWriter = new FileWriter(totalPath, true);
        BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);
        bufferedWriter.write("Xmax:11;Ymax:11");
        bufferedWriter.newLine();
        bufferedWriter.write("tile:0,0,2;tile:1,1,1");
        bufferedWriter.newLine();
        bufferedWriter.close();
        System.out.println("file written to " + totalPath);
    }

}
