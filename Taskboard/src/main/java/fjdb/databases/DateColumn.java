package fjdb.databases;

import fjdb.util.DateTimeUtil;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DateColumn extends AbstractColumn<LocalDate, Date> {

    public DateColumn(String dbName) {
        super(dbName);
    }

    @Override
    public LocalDate get(ResultSet rs, int index) throws SQLException {
        return DateTimeUtil.date(rs.getDate(index));
    }

    @Override
    public Date dbElement(LocalDate input) {
        return DateTimeUtil.makeDate(input);
    }
}
