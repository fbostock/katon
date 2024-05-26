package fjdb.calendar;

import java.time.LocalDate;
import java.util.Iterator;

public interface Holiday {

    default
    Iterable<LocalDate> getGoodDaysIterable(LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
        return () -> getGoodDays(start, includeStart, end, includeEnd);
    }

    default Iterator<LocalDate> getGoodDays(LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
        return new Iterator<>() {
            LocalDate currentDate = (!includeStart || isHoliday(start)) ? nextDate(start) : start;


            @Override
            public boolean hasNext() {
                return includeEnd ? !currentDate.isAfter(end) : currentDate.isBefore(end);
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

    default
    public Iterator<LocalDate> getGoodDays(LocalDate firstInclusive, LocalDate endExclusive) {
        return getGoodDays(firstInclusive, true, endExclusive, false);
    }

    public boolean isHoliday(LocalDate date);

    default long daysBetween(LocalDate from, LocalDate to) {
        return from.datesUntil(to).filter(d -> !isHoliday(d)).count();
    }

    default LocalDate next(LocalDate date) {
        LocalDate localDate = date.plusDays(1);
        while (isHoliday(localDate)) {
            localDate = localDate.plusDays(1);
        }
        return localDate;
    }

    default LocalDate previous(LocalDate date) {
        LocalDate localDate = date.minusDays(1);
        while (isHoliday(localDate)) {
            localDate = localDate.minusDays(1);
        }
        return localDate;
    }


}
