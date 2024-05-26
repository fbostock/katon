package fjdb.calendar;

import java.time.LocalDate;

public class NoHoliday implements Holiday {

    @Override
    public boolean isHoliday(LocalDate date) {
        return false;
    }

    public static void main(String[] args) {
        NoHoliday noHoliday = new NoHoliday();
        long daysBetween = noHoliday.daysBetween(LocalDate.of(1985, 1, 30), LocalDate.of(2024, 1, 30));
        System.out.println(daysBetween);
    }
}
