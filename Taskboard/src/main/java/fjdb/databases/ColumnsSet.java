package fjdb.databases;

import com.google.common.collect.Lists;
import fjdb.databases.columns.IdColumn;
import fjdb.databases.columns.IdMaker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/*
A column set ties a database definition to a DataItemIF object - the DataItemIF represents the database bean. Applications
can use the bean, but for better decoupling, it may be better to have an interface that converts the beans into usable
objects,
 */
public abstract class ColumnsSet<T extends DataItemIF, I extends DataId> extends IdColumnGroup<T, I> {

    private final List<ColumnDecorator<T, ?>> columnList = Lists.newArrayList();
    private final Map<Integer, Thing<?>> things = new HashMap<>();

    public ColumnsSet(IdColumn<I> idColumn) {
        super(idColumn);
    }

    public ColumnsSet(IdMaker<I> idMaker) {
        this(new IdColumn<>("ID", idMaker));
    }

    protected <V> V resolve(ColumnDecorator<T, V> column, ResultSet rs) throws SQLException {
        return resolve(column.column, rs);
    }

    public List<ColumnDecorator<T, ?>> getColumnList() {
        return columnList;
    }

    public <V> ColumnsSet<T, I> addColumnDecorator(ColumnDecorator<T, V> columnDecorator) {
        columnList.add(columnDecorator);
        addColumn(columnDecorator.column);
        return this;
    }

    public <V> Thing<V> retrieve(int i, Supplier<ColumnDecorator<T, V>> supplier) {
        if (things.get(i) == null) {
            Thing<V> thing = new Thing<>(supplier);
            things.put(i, thing);
        }
        return (Thing<V>) things.get(i);
    }

//        public static <T extends DataItemIF> ColumnsSet<T> of(Class<T> type) {
//            IdColumn<DefaultId<T>> idColumn = new IdColumn<>("ID", integer -> new DefaultId<>(integer, type));
//            return new ColumnsSet<>(type, idColumn);
//        }

    public abstract T makeItem(ResultSet rs) throws SQLException;


//        protected <V> V resolveValue(Thing<V> column, ResultSet rs) throws SQLException {
//            return resolve(column.get().column, rs);
//        }

    public class Thing<V> {
        private ColumnDecorator<T, V> column;
        private final Supplier<ColumnDecorator<T, V>> supplier;

        public Thing(Supplier<ColumnDecorator<T, V>> supplier) {
            column = supplier.get();
            add(column);
            this.supplier = supplier;
        }

        private void add(ColumnDecorator<T, V> column) {
            addColumn(column.column);
            columnList.add(column);
        }

        public ColumnDecorator<T, V> get() {
            //TODO can probably remove the suppler field and the lazy initialization, since we want to generate the columns straight away.
            if (column == null) {
                column = supplier.get();
                add(column);
            }
            return column;
        }

    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        return makeItem(rs);
    }

    @Override
    public I handleId(ResultSet rs) throws SQLException {
        return resolve(idColumn, rs);
    }

    @Override
    public List<Object> getDataItemObjects(T dataItem) {
        ArrayList<Object> objects = Lists.newArrayList();
        for (ColumnDecorator<T, ?> columnDecorator : columnList) {
            objects.add(columnDecorator.extract(dataItem));
        }
        return objects;
    }
}
