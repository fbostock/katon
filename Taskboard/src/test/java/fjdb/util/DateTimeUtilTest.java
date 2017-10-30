package fjdb.util;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Created by francisbostock on 27/10/2017.
 */
public class DateTimeUtilTest {

    @Test
    public void construct_localDate() {
        assertEquals(LocalDate.of(2017, 10, 27), DateTimeUtil.date("20171027"));
    }

    @Test
    public void print_localDates() {
        assertEquals("20171027", DateTimeUtil.print(LocalDate.of(2017, 10, 27)));
    }

}