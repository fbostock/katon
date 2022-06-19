package fjdb.investments;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Iterator;

public class WeekendHoliday implements Holiday {

    @Override
    public Iterator<LocalDate> getGoodDays(LocalDate firstInclusive, LocalDate endExclusive) {
        return new Iterator<>() {
            LocalDate currentDate = isHoliday(firstInclusive) ? nextDate(firstInclusive) : firstInclusive;

            @Override
            public boolean hasNext() {
                return currentDate.isBefore(endExclusive);
            }

            @Override
            public LocalDate next() {
                LocalDate date = currentDate;
                currentDate = nextDate(currentDate);
                return date;
            }

            private LocalDate nextDate(LocalDate date) {
                do {
                    date = date.plusDays(1);
                } while (isHoliday(date));
                return date;
            }

        };

    }

    @Override
    public boolean isHoliday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }


}
