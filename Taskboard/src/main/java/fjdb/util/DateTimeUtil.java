package fjdb.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by francisbostock on 27/10/2017.
 */
public class DateTimeUtil {

    public static DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static DateTimeFormatter DASHED_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter SLASHED_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static LocalDate date(String yyyyMMdd) {
        return date(yyyyMMdd, DateTimeUtil.yyyyMMdd);
    }

    public static LocalDate date(String yyyyMMdd, DateTimeFormatter formatter) {
        return LocalDate.parse(yyyyMMdd, formatter);
    }


    public static LocalDate date(int yyyyMMdd) {
        return date(String.valueOf(yyyyMMdd));
    }

    public static LocalDate date(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    public static String print(LocalDate date) {
        if (date == null) return null;
        return date.format(yyyyMMdd);
    }

    public static LocalDate date(Date date) {
        if (date == null) return null;
        return date.toLocalDate();
    }

    public static Date makeDate(LocalDate date) {
           return Date.valueOf(date);
    }
}
