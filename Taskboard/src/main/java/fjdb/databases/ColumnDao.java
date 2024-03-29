package fjdb.databases;

import fjdb.util.SqlUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ColumnDao<T> extends AbstractSqlDao implements DaoIF<T>  {
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
    protected String createTable() {
        String drop = "DROP TABLE " + getTableName() + " IF EXISTS\n ";
        return drop + "CREATE TABLE " + getTableName() + " (" + columnGroup.getColumnDeclarations() + ")";
    }

    @Override
    public void update(T oldData, T newData) {
        //TODO to Support this, we would need to remove the old one and insert a new one, if there is no unique id to
        //identify table entries.
        throw new UnsupportedOperationException("Not implemented for general column daos.");
//        try {
//            List<Object> tradeObjects = columnGroup.getDataItemObjects(dataItem);
//            tradeObjects.add(dataItem.getId().getId());
//            List<AbstractColumn> columnList = this.columnGroup.columns;
//            String sql = "";
//            sql += Joiner.on(",").join(columnList.stream().map(col -> col.getName() + " = ?").collect(Collectors.toList()));
//            String insert = "UPDATE " + getTableName() + " SET " + sql + " WHERE " + columnGroup.idColumn.getName() + " = ? ";
//            doUpdate(insert, tradeObjects);
//        } catch (SQLException e) {
//            //TODO should propagate the exception
//            e.printStackTrace();
//        }
    }

    @Override
    public void delete(T dataItem) {
//            TODO
    }


    public ColumnGroup<T> getColumnGroup() {
        return columnGroup;
    }
}
