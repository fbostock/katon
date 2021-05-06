package fjdb.databases;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class IdColumnGroup<T> {
    protected final List<AbstractColumn> columns = new ArrayList<>();
    private final Map<AbstractColumn, Integer> columnIntegerMap = HashBiMap.create();
    protected AbstractColumn idColumn;

    public IdColumnGroup(AbstractColumn idColumn) {
        this.idColumn = idColumn;
        columnIntegerMap.put(idColumn, 1);
    }

    public IdColumnGroup<T> addColumn(AbstractColumn column) {
        columns.add(column);
        columnIntegerMap.put(column, columnIntegerMap.size() + 1);
        return this;
    }

    public abstract T handle(ResultSet rs) throws SQLException;

    protected <V> V resolve(AbstractColumn<V, ?> column, ResultSet rs) throws SQLException {
        return column.get(rs, columnIntegerMap.get(column));
    }

    public abstract List<Object> getDataItemObjects(T dataItem);

    public String getColumnLabels() {
        return Joiner.on(",").join(columns.stream().map(Functions.toStringFunction()::apply).collect(Collectors.toList()));
    }

    public String getColumnDeclarations() {
        return Joiner.on(" ").join(columns.stream().map(col -> col.getName() + " " + col.dbType()).collect(Collectors.toList()));
    }

    public int columnCount() {
        return columns.size();
    }

}
