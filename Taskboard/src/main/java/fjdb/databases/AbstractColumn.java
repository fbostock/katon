package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @param <V> The user object
 * @param <X> The data type used to store V in the database
 */
public abstract class AbstractColumn<V, X> {
    private final String dbName;
    private final String dbType;
    private Class<V> dataType;

    protected AbstractColumn(String dbName, String dbType, Class<V> dataType) {
        this.dbName = dbName;
        this.dbType = dbType;
        this.dataType = dataType;
    }

    public abstract V get(ResultSet rs, int index) throws SQLException;

    public abstract X dbElement(V input);

    public Class<V> getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return dbName;
    }

    public String getName() {
        return dbName;
    }

    /**
     * The name of the data type stored in the database, e.g. varchar(256) or double
     */
    public String dbType() {
        return dbType;
    }
}
