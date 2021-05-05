package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @param <V> The user object
 * @param <X> The data type used to store V in the database
 */
public abstract class AbstractColumn<V, X> {
    private String dbName;

    protected AbstractColumn(String dbName) {
        this.dbName = dbName;
    }

    public abstract V get(ResultSet rs, int index) throws SQLException;

    public abstract X dbElement(V input);

    @Override
    public String toString() {
        return dbName;
    }

    public String getName() {
        return dbName;
    }
}
