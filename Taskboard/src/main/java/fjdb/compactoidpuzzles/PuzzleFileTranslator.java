package fjdb.compactoidpuzzles;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fjdb.compactoidpuzzles.display.PuzzleFileManager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PuzzleFileTranslator {

    public static void main(String[] args) {

        PuzzleFileManager puzzleFileManager = PuzzleFileManager.get(new File("/Users/francisbostock/Documents/CompactoidPuzzles/Selected/New_20221021/8By8Grids"));
        List<GridFile> gridFiles = puzzleFileManager.getGridFiles();
        int i = 0;

        List<String> suffixList = Lists.newArrayList();
        for (GridFile gridFile : gridFiles) {
            int moves = extractMinMoves(gridFile);
            String suffix = moves + "_1_" + i++;
            System.out.println(makeCSharpOutput(suffix, gridFile));
            suffixList.add(suffix);
//            if (i > 8) break;
        }

        String suffixArray ="{";
        String join = Joiner.on(",").join(suffixList.stream().map(s -> "\"" + s + "\"").collect(Collectors.toList()));
suffixArray += join;
        suffixArray += "}";

        System.out.println(suffixArray);
    }

    private static int extractMinMoves(GridFile gridFile) {
        String name = gridFile.getName();
        Matcher matcher = Pattern.compile(".*([0-9]+)\\.txt").matcher(name);
        if (matcher.matches()) {
            String group = matcher.group(1);
            return Integer.parseInt(group);
        }
        return -1;
    }

    private static String makeCSharpOutput(String suffix, GridFile gridFile) {
//        String contents = "public static string get() {\n";//TODO create unique method name
        String contents = "case \"" + suffix + "\" : return ";//TODO create unique method name
        contents += String.format("\"Xmax:%s;Ymax:%s\\n\" +\n", gridFile.getXMax(), gridFile.getYMax());
        Map<Position, Integer> tiles = gridFile.getTiles();

        List<String> collect = tiles.entrySet().stream().map(e -> tileSegment(e.getKey(), e.getValue())).collect(Collectors.toList());
        List<List<String>> entriesList = Lists.partition(collect, 10);

        List<String> lines = Lists.newArrayList();
        for (List<String> entries : entriesList) {
            lines.add("\t\"" + Joiner.on(";").join(entries));
        }

        contents += Joiner.on("\\n\" + \n").join(lines) + "\";\n";

        contents += "\n";
        return contents;
    }

    private static String tileSegment(Position position, Integer type) {
        return String.format("tile:%s,%s,%s", position.X, position.Y, type);
    }
}


/*
public static string get3_1() {
            return
                "Xmax:10;Ymax:10\n" +
                "tile:-2,-3,1;tile:0,-1,3;tile:2,1,1;tile:-2,-2,0;tile:0,0,3;tile:2,2,3;tile:-2,-1,0;tile:0,1,0;tile:-2,0,0;tile:0,2,1\n" +
                "tile:-2,1,0;tile:-2,2,1;tile:1,-3,1;tile:1,-2,0;tile:-1,-3,1;tile:1,-1,0;tile:-1,-2,0;tile:1,0,0;tile:-3,-3,3;tile:-1,-1,3\n" +
                "tile:1,1,0;tile:-3,-2,1;tile:-1,0,3;tile:1,2,1;tile:-3,-1,1;tile:-1,1,0;tile:-3,0,1;tile:-1,2,1;tile:-3,1,1;tile:-3,2,3\n" +
                "tile:2,-3,3;tile:2,-2,1;tile:0,-3,1;tile:2,-1,1;tile:0,-2,0;tile:2,0,1";
        }


We'll define a new suffix to retrieve puzzles. Existing ones are off form 6_3, where 6 is the gold moves, 3 is a mere identifier.
New ones will be prepended n_ (new),


        public static Game puzzleN(int minMoves, int n) {
            return new PuzzleGame(minMoves + "_" + n, minMoves);
        }
 */