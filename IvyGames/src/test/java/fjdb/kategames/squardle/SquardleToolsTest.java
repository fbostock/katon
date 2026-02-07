// java
package fjdb.kategames.squardle;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SquardleToolsTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            action.run();
        } finally {
            System.out.flush();
            System.setOut(oldOut);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testWordFoundHorizontal() {
        char[][] grid = {
                {'A', 'B'},
                {'C', 'D'}
        };
        SquardleTools tool = new SquardleTools();
        String out = captureOutput(() -> tool.checkMatches(List.of("AB"), grid));
        assertTrue(out.contains("AB: FOUND"));
    }

    @Test
    public void testWordNotFound() {
        char[][] grid = {
                {'A', 'B'},
                {'C', 'D'}
        };
        SquardleTools tool = new SquardleTools();
        String out = captureOutput(() -> tool.checkMatches(List.of("AE"), grid));
        assertTrue(out.contains("AE: NOT FOUND"));
    }

    @Test
    public void testDiagonalFound() {
        char[][] grid = {
                {'A', 'X'},
                {'X', 'D'}
        };
        SquardleTools tool = new SquardleTools();
        String out = captureOutput(() -> tool.checkMatches(List.of("AD"), grid));
        assertTrue(out.contains("AD: FOUND"));
    }

    @Test
    public void testLetterReuseNotAllowed() {
        char[][] grid = {
                {'A'}
        };
        SquardleTools tool = new SquardleTools();
        String out = captureOutput(() -> tool.checkMatches(List.of("AA"), grid));
        assertTrue(out.contains("AA: NOT FOUND"));
    }

    @Test
    public void testEmptyGridAndNullGrid() {
        SquardleTools tool = new SquardleTools();

        String outNull = captureOutput(() -> tool.checkMatches(List.of("WORD"), null));
        assertTrue(outNull.contains("WORD: NOT FOUND (empty grid)"));

        char[][] empty = new char[0][];
        String outEmpty = captureOutput(() -> tool.checkMatches(List.of("WORD"), empty));
        assertTrue(outEmpty.contains("WORD: NOT FOUND (empty grid)"));
    }

    @Test
    public void testMultipleWords() {
        char[][] grid = {
                {'C', 'A', 'T'},
                {'X', 'Y', 'Z'},
                {'D', 'O', 'G'}
        };
        SquardleTools tool = new SquardleTools();
        String out = captureOutput(() -> tool.checkMatches(List.of("CAT", "DOG", "FISH"), grid));
        assertTrue(out.contains("CAT: FOUND"));
        assertTrue(out.contains("DOG: FOUND"));
        assertTrue(out.contains("FISH: NOT FOUND"));
    }
    @Test
    public void testMultipleWordsAbandoned() {
        char[][] grid = createGrid("ABANDONED");
        // A  B  A
        // N  D  O
        // N  E  D

        SquardleTools tool = new SquardleTools();
        String out = captureOutput(() -> tool.checkMatches(List.of("BODE", "ABODE", "DONE", "FISH"), grid));
        assertTrue(out.contains("BODE: FOUND"));
        assertTrue(out.contains("ABODE: FOUND"));
        assertTrue(out.contains("DONE: NOT FOUND"));
        assertTrue(out.contains("FISH: NOT FOUND"));
    }

    private char[][] createGrid(String word) {
        //if word length is not a square number, return null
        int len = word.length();
        int size = (int) Math.ceil(Math.sqrt(len));
        if (size * size != len) {
            return null;
        }
        char[][] grid = new char[size][size];
        for (int i = 0; i < size * size; i++) {
            int r = i / size;
            int c = i % size;
            if (i < len) {
                grid[r][c] = word.charAt(i);

            }
        }
        return grid;
    }
}
