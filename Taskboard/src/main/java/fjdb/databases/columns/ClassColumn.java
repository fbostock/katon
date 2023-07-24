package fjdb.databases.columns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class ClassColumn<T> extends AbstractColumn<T, String>{

    private final Function<T, String> makeDbElement;
    private final Function<String, T> convertDbElement;

    public ClassColumn(String dbName, String dbType, Class<T> clazz, Function<T, String> makeDbElement, Function<String, T> convertDbElement) {
        super(dbName, dbType, clazz);
        this.makeDbElement = makeDbElement;
        this.convertDbElement = convertDbElement;
    }

    @Override
    public T get(ResultSet rs, int index) throws SQLException {
        String string = rs.getString(index);
        return convertDbElement.apply(string);
    }

    @Override
    public String dbElement(T input) {
        return makeDbElement.apply(input);
    }
}
