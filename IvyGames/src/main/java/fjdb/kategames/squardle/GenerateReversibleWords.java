package fjdb.kategames.squardle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GenerateReversibleWords {

    private final Set<String> dictionary;

    public static void main(String[] args) throws IOException {
        GenerateReversibleWords generator = new GenerateReversibleWords();
        TreeSet<String> orderedWords = new TreeSet<>();
        for (String word : generator.dictionary) {
            String reversed = new StringBuilder(word).reverse().toString();
            if (generator.dictionary.contains(reversed)) {
                orderedWords.add(word);
                orderedWords.add(reversed);
            }
        }

        Path out = Paths.get("reversibleWords.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            writer.write("Total reversible words found: " + orderedWords.size());
            writer.newLine();
            for (String w : orderedWords) {
                writer.write(w);
                writer.newLine();
            }
        }

        System.out.println("Wrote reversible words to: " + out.toAbsolutePath());
    }

    public GenerateReversibleWords() throws IOException {
        // Try to load resource "wordList.txt" from classpath (src/main/resources)
        String resourceName = "wordList.txt";
        InputStream in = GenerateReversibleWords.class.getResourceAsStream(resourceName);
        if (in == null) {
            // try with leading slash
            in = GenerateReversibleWords.class.getResourceAsStream("/" + resourceName);
        }
        if (in == null) {
            // If resource not found on classpath, try to load from current working directory
            Path p = Paths.get(resourceName);
            if (Files.exists(p)) {
                List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
                dictionary = new HashSet<>(lines.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet()));
                return;
            }
            throw new IOException("Resource not found on classpath and file not found: " + resourceName);
        }

        try (InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            List<String> lines = new java.io.BufferedReader(isr).lines()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            dictionary = new HashSet<>(lines);
        }
    }
}
