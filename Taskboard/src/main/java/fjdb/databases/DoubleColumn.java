package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

class DoubleColumn extends AbstractColumn<Double, Double> {

    protected DoubleColumn(String dbName) {
        super(dbName);
    }

    @Override
    public Double get(ResultSet rs, int index) throws SQLException {
        return rs.getDouble(index);
    }

    @Override
    public Double dbElement(Double input) {
        return input;
    }
}
