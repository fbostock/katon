package fjdb.kategames.squardle;

import java.util.List;

public record Puzzle(String word, int id, char[] letters, List<String> wordlist) {

    public String drawOutput() {
        /*
        {
      id: 1,
      initialLetters: [
        "c","a","t",
        "e","r","s",
        "o","n","e"
      ],
      wordList: new Set([
        "stone", "rates", "ears", "tone", "cart", "care", "eons", "scar"
      ])
    },
 */



        return """
        {
          id: %d,
          initialLetters: [
            "%s","%s","%s",
            "%s","%s","%s",
            "%s","%s","%s"
          ],
          wordList: new Set([
            %s
          ])
        },
        """.formatted(
              id,
              letters[0], letters[1], letters[2],
              letters[3], letters[4], letters[5],
              letters[6], letters[7], letters[8],
              String.join(", ", wordlist.stream().map(s -> "\"" + s + "\"").toList())
      );

    }


}
