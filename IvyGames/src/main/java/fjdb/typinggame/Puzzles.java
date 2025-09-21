package fjdb.typinggame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static fjdb.typinggame.Images.IMAGE_PATH;

/**
 * This class stores a hard-coded list of puzzles
 */
public class Puzzles {

    public static final String PUZZLE_IMAGE_PATH = IMAGE_PATH + "puzzles/";

    public static final List<Puzzle> puzzles  = new ArrayList<>();
    public static Puzzle duck = makePuzzle("Duck", "duck.jpeg");
    public static Puzzle chase = makePuzzle("Chase", "chase.jpeg");
    public static Puzzle marshall = makePuzzle("Marshall", "marshall.jpeg");
    public static Puzzle rubble = makePuzzle("Rubble", "rubble.jpeg");
    public static Puzzle skye = makePuzzle("Skye", "skye.jpeg");
    public static Puzzle ryder = makePuzzle("Ryder", "ryder.jpeg");
    public static Puzzle tracker = makePuzzle("Tracker", "tracker.jpeg");
    public static Puzzle zuma = makePuzzle("Zuma", "zuma.jpeg");


    private static Puzzle makePuzzle(String name, String path) {
        Puzzle puzzle = new Puzzle(name, new File(PUZZLE_IMAGE_PATH, path));
        puzzles.add(puzzle);
        return puzzle;
    }
}
