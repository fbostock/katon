package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringColumn extends AbstractColumn<String, String> {

    public StringColumn(String dbName) {
        super(dbName);
    }

    @Override
    public String get(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    @Override
    public String dbElement(String input) {
        return input;
    }

}
