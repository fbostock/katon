package fjdb.databases.columns;

import fjdb.util.DateTimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DateTimeColumn extends AbstractColumn<LocalDateTime, Timestamp> {

    public DateTimeColumn(String dbName) {
        super(dbName, "TIMESTAMP", LocalDateTime.class);
    }

    @Override
    public LocalDateTime get(ResultSet rs, int index) throws SQLException {
        return DateTimeUtil.makeTime(rs.getTimestamp(index));
    }

    @Override
    public Timestamp dbElement(LocalDateTime input) {
        return DateTimeUtil.makeTime(input);
    }
}
