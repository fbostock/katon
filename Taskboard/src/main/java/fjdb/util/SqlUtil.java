package fjdb.util;

import com.google.common.base.Joiner;

import java.util.Arrays;

/**
 * Created by francisbostock on 30/10/2017.
 */
public class SqlUtil {

    public static String makeQuestionMarks(int size) {
        if (size <=0) {
            return "";
        }
        String[] strings = new String[size];
        Arrays.fill(strings, "?");
        return "(" + Joiner.on(",").join(strings) + ")";
    }
}
