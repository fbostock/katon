package fjdb.databases.columns;

import fjdb.databases.columns.AbstractColumn;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleColumn extends AbstractColumn<Double, Double> {

    public DoubleColumn(String dbName) {
        super(dbName, "DOUBLE", Double.class);
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
