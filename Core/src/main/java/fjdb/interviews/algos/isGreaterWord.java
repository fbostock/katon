package fjdb.interviews.algos;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class isGreaterWord {

    public static void main(String[] args) throws IOException {
        readFile();
    }

    private static void readFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/Users/francisbostock/Desktop/input.txt")));

        int T = Integer.parseInt(bufferedReader.readLine().trim());

        File file = new File("/Users/francisbostock/Desktop/output.txt");
        List<String> expected = Files.readLines(file, Charset.defaultCharset());
        List<String> results = Lists.newArrayList();

        IntStream.range(0, T).forEach(TItr -> {
            try {
                String w = bufferedReader.readLine();

                String result = biggerIsGreater(w);
                results.add(result);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        bufferedReader.close();

        for (int i = 0; i < results.size(); i++) {
            String s = results.get(i);
            String expectedResult = expected.get(i);
            if (!s.equals(expectedResult)) {
                System.out.println("AARRGGHH");

            }
        }
        //rebjvsszebhehuojrkkhszxltyqfdvayusylgmgkdivzlpmmtvbsavxvydldmsym //input
        //rebjvsszebhehuojrkkhszxltyqfdvayusylgmgkdivzlpmmtvbsavxvydlmdmsy //my output
        //rebjvsszebhehuojrkkhszxltyqfdvayusylgmgkdivzlpmmtvbsavxvydldmyms //expecged output
    }


    public static String biggerIsGreater(String w) {

        if (!w.toLowerCase().equals(w)) {
            throw new RuntimeException();
        }

        char letterToMove = 'a';
        int index = -1;
        for (int i = w.length() - 1; i >= 0; i--) {
            char letter = w.charAt(i);
            for (int j = i - 1; j >= 0; j--) {
                char l = w.charAt(j);
                if (l < letter) {
                    if (j > index) {
                        index = j;
                        letterToMove = letter;
                    }
                }
            }



        }

        if (index <0) return "no answer";

        String start = w.substring(0, index);
        start += letterToMove;
        List<Character> chars = new ArrayList<>();

        char[] end = w.substring(index).toCharArray();
        for (char c : end) {
            chars.add(c);
        }
        chars.remove((Character) letterToMove);
        Collections.sort(chars);
        for (char a : chars) {
            start += a;
        }
        return start;


    }

}


