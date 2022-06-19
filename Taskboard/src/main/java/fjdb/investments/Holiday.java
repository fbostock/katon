package fjdb.investments;

import java.time.LocalDate;
import java.util.Iterator;

public interface Holiday {

    public Iterator<LocalDate> getGoodDays(LocalDate firstInclusive, LocalDate endExclusive);

    public boolean isHoliday(LocalDate date);
}
