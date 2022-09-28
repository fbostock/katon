package fjdb.databases;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import fjdb.databases.columns.AbstractColumn;
import fjdb.databases.columns.IdColumn;
import fjdb.graphics.Xform;
import fjdb.util.SqlUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A column dao based around a primary id field.
 *
 * @param <T>
 */
public abstract class IdColumnDao<T extends DataItemIF, I extends DataId> extends AbstractSqlDao implements DaoIF<T> {

    private final IdColumnGroup<T, I> columnGroup;
    //TODO access to this needs to be protected (read/write lock?)
    private final Object idBeanMapLock = new Object();
    protected final BiMap<I, T> idBeanMap = HashBiMap.create();


    public IdColumnDao(DatabaseAccess access, IdColumnGroup<T, I> columnGroup) {
        super(access);
        this.columnGroup = columnGroup;
    }

    public List<T> load() {
        List<T> dataItems = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + getTableName() + " ORDER BY " + columnGroup.idColumn.getName() + " ASC";
            dataItems.addAll(doSelect(selectQuery, new ArrayList<>(), handler()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataItems;
    }

    private ResultHandler<T> handler() {
        return rs -> {
            I id = columnGroup.handleId(rs);
            T handle = columnGroup.handle(rs);
            synchronized (idBeanMapLock) {
                idBeanMap.put(id, handle);
            }
            return handle;
        };
    }

    //TODO 10 July 21: when inserting at a caching layer, we want to lock the table, do the insert, then do a findId to get the id for the item
    //so we can store both the new item and id.

    public T find(I id) throws SQLException {
        List<T> dataItems = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + getTableName() + " WHERE " + columnGroup.idColumn.getName() + " = ?";
            ArrayList<Object> args = new ArrayList<>();
            args.add(id.getId());
            dataItems.addAll(doSelect(selectQuery, args, handler()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (dataItems.size() == 1) {
            return dataItems.get(0);
        } else {
            throw new SQLException("Found multiple matches for " + id);
        }
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
            I id = findId(dataItem);
            synchronized (idBeanMapLock) {
                idBeanMap.put(id, dataItem);
            }
        } catch (SQLException e) {
            //TODO should propagate the exception
            e.printStackTrace();
        }
    }

    public I findId(T dataItem) {
        synchronized (idBeanMapLock) {
            I dataId = idBeanMap.inverse().get(dataItem);
            if (dataId == null) {
                IdColumn<I> idColumn = columnGroup.idColumn;
                String idColumnName = idColumn.getName();
                List<Object> tradeObjects = columnGroup.getDataItemObjects(dataItem);
                String select = "SELECT " + idColumnName + " FROM " + getTableName() + " WHERE ";
                List<String> columnNames = columnGroup.getColumnNames();
                List<Object> args = Lists.newArrayList();
                select += Joiner.on(" AND ").join(columnNames.stream().map(name -> name + "=?").collect(Collectors.toList()));
                for (int i = 0; i < columnNames.size(); i++) {
                    Object value = tradeObjects.get(i);
                    args.add(value);
                }
                try {
                    List<I> ts = doSelect(select, args, new ResultHandler<I>() {
                        @Override
                        public I handle(ResultSet rs) throws SQLException {
                            I resolve = columnGroup.resolve(idColumn, rs);
                            return resolve;
                        }
                    });
                    if (ts.size() != 1) {
                        //TODO replace with warn log output
                        System.out.println(String.format("WARNING Found %s entries for %s (%s)", ts.size(), dataItem, ts));
                    } else {
                        return ts.get(0);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return null;
            }
            return dataId;
        }
    }

    @Override
    protected String createTable() {
        String drop = "DROP TABLE " + getTableName() + " IF EXISTS\n ";
        return drop + "CREATE TABLE " + getTableName() + " (ID INT GENERATED BY DEFAULT AS IDENTITY, " + columnGroup.getColumnDeclarations() + ")";
    }

    @Override
    public void update(T oldItem, T newItem) {
        try {
            List<Object> tradeObjects = columnGroup.getDataItemObjects(newItem);
            I dataId;
            synchronized (idBeanMapLock) {
                dataId = idBeanMap.inverse().get(oldItem);
            }
            tradeObjects.add(dataId.getId());
            List<AbstractColumn> columnList = this.columnGroup.columns;
            String sql = "";
            sql += Joiner.on(",").join(columnList.stream().map(col -> col.getName() + " = ?").collect(Collectors.toList()));
            String insert = "UPDATE " + getTableName() + " SET " + sql + " WHERE " + columnGroup.idColumn.getName() + " = ? ";
            doUpdate(insert, tradeObjects);

            synchronized (idBeanMapLock) {
                idBeanMap.put(dataId, newItem);
            }
        } catch (SQLException e) {
            //TODO should propagate the exception
            e.printStackTrace();
        }
    }

    public T updateField(T item, AbstractColumn column, Object newValue) throws SQLException {
        I dataId;
        synchronized (idBeanMapLock) {
            dataId = idBeanMap.inverse().get(item);
        }
        String sql = column.getName() + " = ? ";
        String update = "UPDATE " + getTableName() + " SET " + sql + " WHERE " + columnGroup.idColumn.getName() + " = ? ";
        List<Object> objects = Lists.newArrayList();
        objects.add(column.dbElement(newValue));
        objects.add(dataId.getId());
        doUpdate(update, objects);

        T newItem = find(dataId);
        synchronized (idBeanMapLock) {
            idBeanMap.put(dataId, newItem);
        }
        System.out.printf("Updated %s for type %s with new value %s\n", item, column.getDataType(), newValue);
        return newItem;
    }

    @Override
    public void delete(T dataItem) {
//            TODO
    }


    public IdColumnGroup<T, I> getColumnGroup() {
        return columnGroup;
    }
}