package fjdb.typinggame;

import com.google.common.collect.Sets;
import fjdb.util.ListUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PuzzleProvider {
    private List<Puzzle> initialPuzzles = new ArrayList<>();
    private final List<Puzzle> usedPuzzles = new ArrayList<>();
    int nextIndex = 0;

    public PuzzleProvider() {
        this.initialPuzzles = Puzzles.puzzles;

        List<Puzzle> loadedPuzzles = PuzzleLoader.loadPuzzles();
        if (!loadedPuzzles.isEmpty()) {
            this.initialPuzzles = loadedPuzzles;
        }
        resetPuzzles();
    }

    private void resetPuzzles() {
        usedPuzzles.addAll(ListUtil.randomiseOrder(initialPuzzles));
    }

    public Puzzle nextPuzzle() {
        if (!usedPuzzles.isEmpty()) {
            return usedPuzzles.remove(0);
        }
        resetPuzzles();
        return nextPuzzle();
    }

    public Puzzle previousPuzzle() {
        if (nextIndex > 0) {
            nextIndex--;
            return initialPuzzles.get(nextIndex);
        }
        return initialPuzzles.get(0);
    }

    private static class PuzzleLoader {

        private static final Set<String> validExtensions = Sets.newHashSet("JPG", "JPEG", "PNG");

        public static final File puzzleDirectory = new File("/Users/francisbostock/Desktop/Puzzles");

        public static List<Puzzle> loadPuzzles() {
            ArrayList<Puzzle> puzzles = new ArrayList<>();

            String property = System.getProperty("user.dir");
            File puzzleFolder = new File(property, "puzzles");
            if (!puzzleFolder.exists()) {
                puzzleFolder = puzzleDirectory;
            }

            if (puzzleFolder.exists()) {
                System.out.println("Loading puzzles from " + puzzleFolder);
                File[] files = puzzleFolder.listFiles(pathname -> {
                    String extension = FilenameUtils.getExtension(pathname.getName());
                    return validExtensions.contains(extension.toUpperCase());
                });

                if (files != null) {
                    for (File file : files) {
                        String puzzleName = FilenameUtils.removeExtension(file.getName());
                        puzzles.add(new Puzzle(puzzleName.toUpperCase(), file));
                    }
                }
            }


            return puzzles;

        }
    }
}
