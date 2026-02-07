package fjdb.kategames.squardle;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SquardleTools {

    public char[][] createGridFromWord(String word) {
        //the word needs to be found in the grid

        int size = (int) Math.ceil(Math.sqrt(word.length()));
        char[][] grid = new char[size][size];
        int index = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (index < word.length()) {
                    grid[r][c] = word.charAt(index);
                    index++;
                } else {
                    grid[r][c] = ' ';
                }
            }
        }
        return grid;
    }

    public List<int[]> generateRandomPathThroughEntireGrid0(char[][] grid) {
        //stub
        return null;
        //pick a random starting point
        //mark it as visited
        //while there are unvisited cells
        //  get list of unvisited adjacent cells
        //  if there are unvisited adjacent cells
        //      pick one at random
        //      mark it as visited
        //      add it to the path
        //  else
        //      backtrack to the last cell in the path that has unvisited adjacent cells

    }

    public static void main(String[] args) {
        SquardleTools tool = new SquardleTools();
        Path integers = tool.validPath();
//        Path integers1 = tool.rotatePath90Degrees(integers);

        List<Path> paths = tool.getPaths();
        for (Path path : paths) {
            Stream.of(path, path.reflectPathHorizontally(), path.rotate90Degrees(), path.rotate90Degrees().rotate90Degrees(), path.rotate90Degrees().rotate90Degrees().rotate90Degrees(), path.rotate90Degrees().reflectPathHorizontally())
                    .forEach(path1 -> {
                        char[][] grid = tool.gridFromWordAndPath("ABANDONED", path1);
                        if (!tool.existsInGrid(grid, "ABANDONED")) {
                            throw new IllegalStateException("Path failed to produce word in grid path %s grid %s".formatted(path1, Arrays.deepToString(grid)));
                        } else {
                            System.out.printf("Path succeeded: %s%n", path1.getNodes());
                        }
                    });
        }
    }

    public Path validPath() {
        // A B A . 0 1 2
        // O D N   3 4 5
        // N E E . 6 7 8
        return new Path(0, 1, 2, 5, 4, 3, 6, 7, 8);
    }

    public List<Path> getPathsAndtransconfigurations() {
        List<Path> paths = getPaths();
        List<Path> allPaths = new ArrayList<>();
        for (Path path : paths) {
            Stream.of(path, path.reflectPathHorizontally(), path.rotate90Degrees(), path.rotate90Degrees().rotate90Degrees(), path.rotate90Degrees().rotate90Degrees().rotate90Degrees(), path.rotate90Degrees().reflectPathHorizontally())
                    .forEach(allPaths::add);
        }

        return allPaths;
    }

    public List<Path> getPaths() {
        return List.of(
                new Path(0, 1, 2, 5, 4, 3, 6, 7, 8),
                new Path(0, 1, 2, 5, 8, 7, 4, 3, 6),
                new Path(0, 1, 2, 5, 7, 6, 3, 4, 8),
                new Path(0, 1, 2, 4, 6, 3, 7, 8, 5),
                new Path(0, 1, 2, 4, 8, 5, 7, 3, 6),
                new Path(0, 1, 2, 5, 8, 7, 6, 3, 4),
                new Path(3, 4, 0, 1, 2, 5, 8, 7, 6),
                new Path(3, 1, 0, 4, 6, 7, 8, 5, 2),
                new Path(3, 6, 7, 4, 0, 1, 2, 5, 8),
                new Path(4, 3, 0, 1, 2, 5, 8, 7, 6),
                new Path(4, 0, 1, 2, 5, 8, 7, 6, 3)

        );
    }
    // A B A . 0 1 2
    // O D N   3 4 5
    // N E E . 6 7 8


    private int[] rowColumnFromIndex(int index) {
        int column = index % 3;
        int row = index / 3;
        return new int[]{row, column};
    }

    public char[][] gridFromWordAndPath(String word, Path path) {
        char[][] grid = new char[3][3];
        List<Integer> nodes = path.nodes;
        for (int i = 0; i < nodes.size(); i++) {
            int index = nodes.get(i);
            int[] rc = rowColumnFromIndex(index);
            int row = rc[0];
            int col = rc[1];
            grid[row][col] = word.charAt(i);
        }
        return grid;
    }

    public List<String> findMatches(List<String> words, char[][] grid) {
        List<String> foundWords = new ArrayList<>();
        for (String word : words) {
            if (existsInGrid(grid, word)) {
                foundWords.add(word);
            }
        }
        return foundWords;
    }

    public void checkMatches(List<String> words, char[][] grid) {
        if (grid == null || grid.length == 0) {
            for (String word : words) {
                System.out.println(word + ": NOT FOUND (empty grid)");
            }
            return;
        }
        for (String word : words) {
            boolean found = existsInGrid(grid, word);
            System.out.println(word + ": " + (found ? "FOUND" : "NOT FOUND"));
        }
    }

    private boolean existsInGrid(char[][] grid, String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == word.charAt(0)) {
                    boolean[][] visited = new boolean[rows][cols];
                    if (dfs(grid, word, 0, r, c, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Depth-first search from position (row,column) matching word at index
    private boolean dfs(char[][] grid, String word, int index, int row, int column, boolean[][] visited) {
        if (index == word.length()) {
            return true;
        }
        if (row < 0 || column < 0 || row >= grid.length || column >= grid[0].length) {
            return false;
        }
        if (visited[row][column]) {
            return false;
        }
        if (grid[row][column] != word.charAt(index)) {
            return false;
        }
        // If this is the last character and it matches, word is found
        if (index == word.length() - 1) {
            return true;
        }

        visited[row][column] = true;
        // explore all 8 adjacent directions
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                if (dfs(grid, word, index + 1, row + dr, column + dc, visited)) {
                    visited[row][column] = false;
                    return true;
                }
            }
        }
        visited[row][column] = false;
        return false;
    }

    public static class Path {
        private final List<Integer> nodes;

        public Path(Integer... nodes) {
            this(List.of(nodes));
        }

        public Path(List<Integer> nodes) {
            HashSet<Integer> uniqueNodes = new HashSet<>(nodes);
            if (uniqueNodes.size() != nodes.size()) {
                throw new IllegalArgumentException("Path contains duplicate nodes");
            }
            this.nodes = nodes;
        }

        public List<Integer> getNodes() {
            return nodes;
        }

        public Path rotate90Degrees() {
            return new Path(nodes.stream().map(index -> {
                int[] rc = rcFromIndex(index);
                int row = rc[0];
                int col = rc[1];
                //rotate 90 degrees clockwise
                int newRow = col;
                int newCol = 2 - row;
                return newRow * 3 + newCol;
            }).toList());
        }

        public Path reflectPathHorizontally() {
            //reflects a path through a 3x3 grid horizontally
            //input is a list of indices from 0 to 8
            //output is a list of indices from 0 to 8
            return new Path(nodes.stream().map(index -> {
                int[] rc = rcFromIndex(index);
                int row = rc[0];
                int col = rc[1];
                //reflect horizontally
                int newRow = row;
                int newCol = 2 - col;
                return newRow * 3 + newCol;
            }).toList());
        }

        private int[] rcFromIndex(int index) {
            int column = index % 3;
            int row = index / 3;
            return new int[]{row, column};
        }
    }


}
