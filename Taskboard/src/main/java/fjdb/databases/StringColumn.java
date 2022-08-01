package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringColumn extends AbstractColumn<String, String> {

    //TODO create convenience methods to create strings of varchars, e.g. varchar(256), varchar(1024)
    public StringColumn(String dbName, String dbType) {
        super(dbName, dbType, String.class);
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
