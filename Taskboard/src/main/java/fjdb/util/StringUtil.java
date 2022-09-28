package fjdb.util;

import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * Pad out a string with trailing whitespace, returning a string no greater than max length.
     * If the input string is greater than max, the input string is returned.
     */
    public static String pad(String input, int max) {
        int length = input.length();
        if (length < max) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < max - length; i++) {
                builder.append(" ");
            }
            return input + builder.toString();
        }
        return input;
    }

    /**
     * Count the number of occurrences of search in string.
     */
    public static int count(String string, String search) {
        Pattern regex = Pattern.compile(search);
        Matcher matcher = regex.matcher(string);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public static boolean containsLowerCase(String value) {
        return contains(value, i -> Character.isLetter(i) && Character.isLowerCase(i));
    }

    public static boolean containsUpperCase(String value) {
        return contains(value, i -> Character.isLetter(i) && Character.isUpperCase(i));
    }

    public static boolean containsNumber(String value) {
        return contains(value, Character::isDigit);
    }

    private static boolean contains(String value, IntPredicate predicate) {
        return value.chars().anyMatch(predicate);
    }
}
