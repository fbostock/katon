package fjdb.kategames.squardle;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class WordGameSolver {

    private List<String> dictionary;

    public WordGameSolver() throws IOException {
        URL resource = WordFilter.class.getResource("wordList.txt");
        String inputPath = "";
        if (resource != null) {
            inputPath = resource.getPath();
        }
        dictionary = Files.lines(Paths.get(inputPath)).toList();

    }

    public List<String> getWords(String sourceWord) {
//        System.out.println("Finding words in: " + sourceWord);

        // 1. Create the Frequency Map (Letter Bank) for the source word
        Map<Character, Integer> sourceMap = getFrequencyMap(sourceWord.toLowerCase());

        ArrayList<String> words = new ArrayList<>();
        try (Stream<String> lines = dictionary.stream()) {
            lines.map(String::trim).map(String::toLowerCase)
                    .filter(word -> word.length() >= 4 && word.length() <= sourceWord.length())
                    .filter(word -> canFormWord(word, sourceMap))
                    .forEach(words::add);

        }
        return words;
    }

    /**
     * Creates a map of characters to their occurrence counts.
     */
    private static Map<Character, Integer> getFrequencyMap(String word) {
        Map<Character, Integer> map = new HashMap<>();
        for (char c : word.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
    }

    /**
     * Checks if a dictionary word can be built using the source word's letters.
     */
    private static boolean canFormWord(String dictionaryWord, Map<Character, Integer> sourceMap) {
        Map<Character, Integer> dictWordMap = getFrequencyMap(dictionaryWord);

        for (Map.Entry<Character, Integer> entry : dictWordMap.entrySet()) {
            char c = entry.getKey();
            int countNeeded = entry.getValue();
            int countAvailable = sourceMap.getOrDefault(c, 0);

            if (countNeeded > countAvailable) {
                return false;
            }
        }
        return true;
    }
}