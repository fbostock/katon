package fjdb.kategames.squardle;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class WordFilter {
    public static void main(String[] args) {
        URL resource = WordFilter.class.getResource("sowpods.txt");
        String inputPath = "";
        if (resource != null) {
            inputPath = resource.getPath();
        }
        try {
            // 1. Process and collect words less than 4 letters
//            List<String> shortWords = Files.lines(Paths.get(inputPath))
//                    .map(String::trim)
//                    .filter(word -> !word.isEmpty() && word.length() < 4)
//                    .collect(Collectors.toList());

            // 2. Process and collect words more than 9 letters
            List<String> longWords = Files.lines(Paths.get(inputPath))
                    .map(String::trim)
//                    .filter(word -> word.length() > 3 && word.length() < 10)
                    .filter(word -> word.length() == 9)
                    .collect(Collectors.toList());

            // 3. Write results to files
//            Files.write(Paths.get("short_words.txt"), shortWords);
            Files.write(Paths.get(new File(resource.toURI()).getParent(), "wordList9Letters.txt"), longWords);

            System.out.println("Filtering complete!");
//            System.out.println("Short words found: " + shortWords.size());
            System.out.println("Long words found: " + longWords.size());

        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        } catch (URISyntaxException e) {

            System.err.println("Error with file URI: " + e.getMessage());
        }
    }
}
