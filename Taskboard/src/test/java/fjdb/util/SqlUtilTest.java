package fjdb.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by francisbostock on 30/10/2017.
 */
public class SqlUtilTest {

    @Test
    public void make_question_marks() {
        assertEquals("(?)", SqlUtil.makeQuestionMarks(1));
        assertEquals("(?,?)", SqlUtil.makeQuestionMarks(2));
        assertEquals("(?,?,?)", SqlUtil.makeQuestionMarks(3));
    }

    @Test
    public void make_question_marks_for_zero_size() {
        assertEquals("", SqlUtil.makeQuestionMarks(0));
        assertEquals("", SqlUtil.makeQuestionMarks(-5));
    }
}