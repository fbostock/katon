package fjdb.databases.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeColumn<T extends Enum<T>> extends AbstractColumn<T, String> {

    private final Class<T> clazz;

    public TypeColumn(Class<T> clazz, String dbName, String dbType) {
        super(dbName, dbType, clazz);
        this.clazz = clazz;
    }

    @Override
    public T get(ResultSet rs, int index) throws SQLException {
        String string = rs.getString(index);
        return Enum.valueOf(clazz, string);
    }

    @Override
    public String dbElement(T input) {
        return input.name();
    }
}
