package fjdb.calendar;

import java.time.LocalDate;

public class NoHoliday implements Holiday {

    @Override
    public boolean isHoliday(LocalDate date) {
        return false;
    }
}
