package fjdb.databases;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;
import fjdb.util.SqlUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ColumnDao<T extends DataItemIF> extends AbstractSqlDao implements DaoIF<T> {

    private final ColumnGroup<T> columnGroup;

    public ColumnDao(DatabaseAccess access, ColumnGroup<T> columnGroup) {
        super(access);
        this.columnGroup = columnGroup;
    }

    public List<T> load() {
        List<T> dataItems = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + getTableName();
            dataItems.addAll(doSelect(selectQuery, new ArrayList<>(), columnGroup::handle));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataItems;
    }


    @Override
    public void insert(T dataItem) {

        try {
            List<Object> tradeObjects = columnGroup.getDataItemObjects(dataItem);
            if (tradeObjects.size() != columnGroup.columnCount()) {
                throw new RuntimeException(String.format("Arguments and columns different size: %s %s", tradeObjects.size(), columnGroup.columnCount()));
            }
            String insert = "INSERT INTO " + getTableName() + " (" + columnGroup.getColumnLabels() + ") values " + SqlUtil.makeQuestionMarks(columnGroup.columnCount());
            doUpdate(insert, tradeObjects);
        } catch (SQLException e) {
            //TODO should propagate the exception
            e.printStackTrace();
        }
    }

    @Override
    public void update(T dataItem) {
        try {
            List<Object> tradeObjects = columnGroup.getDataItemObjects(dataItem);
            tradeObjects.add(dataItem.getId().getId());
            List<AbstractColumn> columnList = this.columnGroup.columns;
            String sql = "";
            sql += Joiner.on(",").join(columnList.stream().map(col -> col.getName() + " = ?").collect(Collectors.toList()));
            String insert = "UPDATE " + getTableName() + " SET " + sql + " WHERE " + columnGroup.idColumn.getName() + " = ? ";
            doUpdate(insert, tradeObjects);
        } catch (SQLException e) {
            //TODO should propagate the exception
            e.printStackTrace();
        }
    }

    protected abstract static class ColumnGroup<T> {
        private final List<AbstractColumn> columns = new ArrayList<>();
        private final Map<AbstractColumn, Integer> columnIntegerMap = HashBiMap.create();
        protected AbstractColumn idColumn;

        public ColumnGroup(AbstractColumn idColumn) {
            this.idColumn = idColumn;
            columnIntegerMap.put(idColumn, 1);
        }

        protected ColumnGroup<T> addColumn(AbstractColumn column) {
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

        public int columnCount() {
            return columns.size();
        }

    }

}