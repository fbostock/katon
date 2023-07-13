package fjdb.interviews;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Play {

    public static void main(String[] args) {

//        Pattern pattern = Pattern.compile("([a-zA-Z\\d]+)(?=\\s)(.*?\\s(\\1))*", Pattern.CASE_INSENSITIVE);
//        Pattern pattern = Pattern.compile("(?<![a-zA-Z\\d])([a-zA-Z\\d]+)(?=\\s)((.*?\\1(?![a-zA-Z\\d]))+)", Pattern.CASE_INSENSITIVE);
//        Pattern pattern = Pattern.compile("(?<![a-zA-Z\\d])([a-zA-Z\\d]+)(\\s+(\\1(?=\\s)))+", Pattern.CASE_INSENSITIVE);
        Pattern pattern = Pattern.compile("(?<![a-zA-Z\\d])([a-zA-Z\\d]+)(\\s+(\\1(?![a-zA-Z\\d])))+", Pattern.CASE_INSENSITIVE);
//(?![a-zA-Z\d])
//        String input = "Hello hello hEllo world World";
//        String input = "Hello hello";
//        String input = "Goodbye bye bye world world world in inthe";
//        String input = "tap taptap For fOr for forfor";
//        String input = "taptap of kirethe the hte hTe hte";
//        String input = "tim tamtim tam tam ta tam tam";
        String input = "a a a a a a a a a a a a a a a a";

        Matcher matcher = pattern.matcher(input);
        while(matcher.find()) {
input =             input.replaceAll(matcher.group(), matcher.group(1));
        }

        System.out.println(input);
    }
}
