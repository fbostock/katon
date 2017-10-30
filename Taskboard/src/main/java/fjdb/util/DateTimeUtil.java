package fjdb.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by francisbostock on 27/10/2017.
 */
public class DateTimeUtil {

    public static LocalDate date(String yyyyMMdd) {
        return LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String print(LocalDate date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static LocalDate date(Date date) {
        if (date == null) return null;
        return date.toLocalDate();
    }

    public static Date makeDate(LocalDate date) {
           return Date.valueOf(date);
    }
}
