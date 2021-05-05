package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeColumn<T extends Enum<T>> extends AbstractColumn<T, String> {

    private final Class<T> clazz;

    public TypeColumn(Class<T> clazz, String dbName, String dbType) {
        super(dbName, dbType);
        this.clazz = clazz;
    }

    @Override
    public T get(ResultSet rs, int index) throws SQLException {
        return Enum.valueOf(clazz, rs.getString(index));
    }

    @Override
    public String dbElement(T input) {
        return input.name();
    }
}
