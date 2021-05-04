package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

class IntColumn extends AbstractColumn<Integer, Integer> {

    protected IntColumn(String dbName) {
        super(dbName);
    }

    @Override
    public Integer get(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);
    }

    @Override
    public Integer dbElement(Integer input) {
        return input;
    }
}
