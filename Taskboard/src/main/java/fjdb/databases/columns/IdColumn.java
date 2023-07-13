package fjdb.databases.columns;

import fjdb.databases.DataId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class IdColumn<I extends DataId> extends AbstractColumn<I, Integer> {

    private final Function<Integer, I> idMaker;

    public IdColumn(String dbName, Function<Integer, I> idMaker, Class<I> type) {
        super(dbName, "INT", type);
        this.idMaker = idMaker;
    }

    public IdColumn(String dbName, IdMaker<I> idMaker) {
        super(dbName, "INT", idMaker.getIdType());
        this.idMaker = idMaker;
    }

    @Override
    public I get(ResultSet rs, int index) throws SQLException {
        return idMaker.apply(rs.getInt(index));
    }

    @Override
    public Integer dbElement(DataId input) {
        return input.getId();
    }
}
