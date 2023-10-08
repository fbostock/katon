package fjdb.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekendHoliday implements Holiday {

    public static WeekendHoliday WEEKEND = new WeekendHoliday();

    private WeekendHoliday() {
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

}
