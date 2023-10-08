package fjdb.calendar;

import fjdb.util.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.testng.collections.Lists;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;


public class WeekendHolidayTest {

    @Test
    public void verifies_weekend_as_holidays() {
        WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
        Assert.assertFalse(weekendHoliday.isHoliday(date(2022, 4, 1)));
        Assert.assertTrue(weekendHoliday.isHoliday(date(2022, 4, 2)));
        Assert.assertTrue(weekendHoliday.isHoliday(date(2022, 4, 3)));
        Assert.assertFalse(weekendHoliday.isHoliday(date(2022, 4, 4)));
        Assert.assertFalse(weekendHoliday.isHoliday(date(2022, 4, 5)));
        Assert.assertFalse(weekendHoliday.isHoliday(date(2022, 4, 6)));
        Assert.assertFalse(weekendHoliday.isHoliday(date(2022, 4, 7)));
    }

    @Test
    public void goodDays_skips_holidays() {
        WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
        LocalDate startInclusive = date(2022, 4, 1);
        LocalDate endExclusive = date(2022, 4, 12);
        Iterator<LocalDate> goodDays = weekendHoliday.getGoodDays(startInclusive, endExclusive);
        List<LocalDate> actual = Lists.newArrayList();
        while (goodDays.hasNext()) {
            actual.add(goodDays.next());
        }

        List<LocalDate> expected = Lists.newArrayList(date(2022, 4, 1),
                date(2022, 4, 4),
                date(2022, 4, 5),
                date(2022, 4, 6),
                date(2022, 4, 7),
                date(2022, 4, 8),
                date(2022, 4, 11)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void goodDays_skips_start_on_holiday() {
        WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
        LocalDate startInclusive = date(2022, 4, 2);
        LocalDate endExclusive = date(2022, 4, 6);
        Iterator<LocalDate> goodDays = weekendHoliday.getGoodDays(startInclusive, endExclusive);
        List<LocalDate> actual = Lists.newArrayList();
        while (goodDays.hasNext()) {
            actual.add(goodDays.next());
        }

        List<LocalDate> expected = Lists.newArrayList(date(2022, 4, 4),
                date(2022, 4, 5));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void goodDays_skips_end_on_holiday() {
        WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
        LocalDate startInclusive = date(2022, 4, 4);
        LocalDate endExclusive = date(2022, 4, 10);
        Iterator<LocalDate> goodDays = weekendHoliday.getGoodDays(startInclusive, endExclusive);
        List<LocalDate> actual = Lists.newArrayList();
        while (goodDays.hasNext()) {
            actual.add(goodDays.next());
        }

        List<LocalDate> expected = Lists.newArrayList(date(2022, 4, 4),
                date(2022, 4, 5),
                date(2022, 4, 6),
                date(2022, 4, 7),
                date(2022, 4, 8));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void goodDays_empty_with_only_holidays() {
        WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
        LocalDate startInclusive = date(2022, 4, 2);
        LocalDate endExclusive = date(2022, 4, 4);
        Iterator<LocalDate> goodDays = weekendHoliday.getGoodDays(startInclusive, endExclusive);
        List<LocalDate> actual = Lists.newArrayList();
        while (goodDays.hasNext()) {
            actual.add(goodDays.next());
        }

        List<LocalDate> expected = Lists.newArrayList();
        Assert.assertEquals(expected, actual);
    }

    private LocalDate date(int year, int month, int day) {
        return DateTimeUtil.date(year, month, day);
    }
}