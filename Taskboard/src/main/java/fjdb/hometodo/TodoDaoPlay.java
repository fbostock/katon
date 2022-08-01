package fjdb.hometodo;

import com.google.common.collect.Lists;
import fjdb.databases.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class TodoDaoPlay extends IdColumnDao<TodoDataItem, TodoDaoPlay.DefaultId> {

    private final ColumnsSet<TodoDataItem> columnSet;

    public static TodoDaoPlay getDao(DatabaseAccess access) {
        ColumnsSet<TodoDataItem> columnSet = getColumnSet();
        return new TodoDaoPlay(access, columnSet);
    }

    private TodoDaoPlay(DatabaseAccess access, ColumnsSet<TodoDataItem> columnSet) {
        super(access, columnSet);
        this.columnSet = columnSet;
        try {
            if (!checkTableExists()) {
                writeTable();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }



    }

    @Override
    public String getTableName() {
        return "TodoItems";
    }

    @Override
    public ColumnsSet<TodoDataItem> getColumnGroup() {
        return columnSet;
    }

    //TODO generate a class to simplify how to create a columns class based on some DataItem
    //TODO generate a class that knows how to generate a table to view a DataItem, including filters, field updaters...
    /*private static class Columns extends IdColumnGroup<TodoDataItem, TodoId> {

        //String name, Owner owner, Category category, Term term, Size size
        public StringColumn nameColumn = new StringColumn("NAME", "VARCHAR(256)");
        public TypeColumn<Owner> owner = new TypeColumn<>(Owner.class, "OWNER", "VARCHAR(256)");
        public TypeColumn<Category> category = new TypeColumn<>(Category.class, "CATEGORY", "VARCHAR(256)");
        public TypeColumn<Term> term = new TypeColumn<>(Term.class, "TERM", "VARCHAR(256)");
        public TypeColumn<Size> size = new TypeColumn<>(Size.class, "SIZE", "VARCHAR(256)");

//        public ColumnDecorator<TodoDataItem> nameCol = new ColumnDecorator<>(nameColumn, TodoDataItem::getName);

        //TODO perhaps we could remove the TodoId object, and instead have an Id object (so one class) which has a type
        //of the DataItem, or in fact anything. This might simplify things rather than having to create an Id object.
        private final IdColumn<TodoId> idColumn;

        public static Columns of() {
            IdColumn<TodoId> idColumn = new IdColumn<>("ID", TodoId::new);
            return new Columns(idColumn);
        }

        public Columns(IdColumn<TodoId> idColumn) {
            super(idColumn);
            this.idColumn = idColumn;
            addColumn(nameColumn).addColumn(owner).addColumn(category).addColumn(term).addColumn(size);
        }

        @Override
        public TodoDataItem handle(ResultSet rs) throws SQLException {
            return new TodoDataItem(resolve(nameColumn, rs), resolve(owner, rs), resolve(category, rs), resolve(term, rs),
                    resolve(size, rs));
        }

        @Override
        public TodoId handleId(ResultSet rs) throws SQLException {
            return resolve(idColumn, rs);
        }

        @Override
        public List<Object> getDataItemObjects(TodoDataItem dataItem) {
            return Lists.newArrayList(nameColumn.dbElement(dataItem.getName()),
                    owner.dbElement(dataItem.getOwner()),
                    category.dbElement(dataItem.getCategory()),
                    term.dbElement(dataItem.getTerm()),
                    size.dbElement(dataItem.getSize()));
        }
    }*/


    /*
    TODO
    Create ColumnDecorator objects which wrap a column and a lambda for a way to extract the value from a DataItem. This
     will allow the getDataItemObjects method ti simply iterate through the column decorators. The aim is to get the specification
     of the columns done in one place and everything else automatically sorts itself out. i.e. When specifcying the columns above, instead
     we would specify column decorators, defining the extractors as methods to call on TodoDataItem.
     Next, can we simplify the handle method as well? One option might be to define a DataItemMaker interface in which you specify
     how to create the item using arguments which actually generate the column decorators.
     */

    public static class ColumnDecorator<T extends DataItemIF, V> {

        private final AbstractColumn<V, ?> column;
        private final Function<T, V> extractor;

        public ColumnDecorator(AbstractColumn<V, ?> column, Function<T, V> extractor) {

            this.column = column;
            this.extractor = extractor;
        }

        public Object extract(T item) {
            return column.dbElement(extractor.apply(item));
        }

        public AbstractColumn<V, ?> getColumn() {
            return column;
        }
    }

    public static class DefaultId extends DataId {

        private final Class<?> type;

        public DefaultId(int id, Class<?> type) {
            super(id);
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            DefaultId defaultId = (DefaultId) o;
            return Objects.equals(type, defaultId.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), type);
        }

    }

    public static ColumnsSet<TodoDataItem> getColumnSet() {
//        IdColumn<DefaultId> idColumn = new IdColumn<>("ID", integer -> new DefaultId(integer, TodoDataItem.class), DefaultId.class);

        ColumnDecorator<TodoDataItem, String> nameColumn = new ColumnDecorator<>(new StringColumn("NAME", "VARCHAR(256)"), TodoDataItem::getName);
        ColumnDecorator<TodoDataItem, Owner> ownerColumn = new ColumnDecorator<>(new TypeColumn<>(Owner.class, "OWNER", "VARCHAR(256)"), TodoDataItem::getOwner);
        ColumnDecorator<TodoDataItem, Category> categoryColumn = new ColumnDecorator<>(new TypeColumn<>(Category.class, "CATEGORY", "VARCHAR(256)"), TodoDataItem::getCategory);
        ColumnDecorator<TodoDataItem, Term> termColumn = new ColumnDecorator<>(new TypeColumn<>(Term.class, "TERM", "VARCHAR(256)"), TodoDataItem::getTerm);
        ColumnDecorator<TodoDataItem, Size> sizeColumn = new ColumnDecorator<>(new TypeColumn<>(Size.class, "SIZE", "VARCHAR(256)"), TodoDataItem::getSize);
        ColumnDecorator<TodoDataItem, Progress> progressColumn = new ColumnDecorator<>(new TypeColumn<>(Progress.class, "PROGRESS", "VARCHAR(256)"), todoDataItem -> Progress.TODO);
        ColumnDecorator<TodoDataItem, LocalDate> dueDateColumn = new ColumnDecorator<>(new DateColumn("DUEDATE"), todoDataItem -> LocalDate.now());
        ColumnDecorator<TodoDataItem, Integer> priorityColumn = new ColumnDecorator<>(new IntColumn("PRIORITYLEVEL"), todoDataItem -> 1);
        return new ColumnsSet<>(TodoDataItem.class) {
            @Override
            public TodoDataItem makeItem(ResultSet rs) throws SQLException {
                return new TodoDataItem(
                        //TODO create convenience method to simplify each line.
                        //TODO to add new column, how do we update existing table? Need to have it read in old format, and output to new format somehow.
                        resolve(nameColumn.column, rs),
                        resolve(ownerColumn.column, rs),
                        resolve(categoryColumn.column, rs),
                        resolve(termColumn.column, rs),
                        resolve(sizeColumn.column, rs)
                );
            }
        }.addColumnDecorator(nameColumn)
                .addColumnDecorator(ownerColumn)
                .addColumnDecorator(categoryColumn)
                .addColumnDecorator(termColumn)
                .addColumnDecorator(sizeColumn)
                .addColumnDecorator(progressColumn)
                .addColumnDecorator(dueDateColumn)
                .addColumnDecorator(priorityColumn);

    }

    /*
    A column set ties a database definition to a DataItemIF object - the DataItemIF represents the database bean. Applications
    can use the bean, but for better decoupling, it may be better to have an interface that converts the beans into usable
    objects,
     */
    public static abstract class ColumnsSet<T extends DataItemIF> extends IdColumnGroup<T, DefaultId> {

        private final Class<T> type;
        private final List<ColumnDecorator<T, ?>> columnList = Lists.newArrayList();
        private final Map<Integer, Thing<?>> things = new HashMap<>();

        public ColumnsSet(Class<T> type, IdColumn<DefaultId> idColumn) {
            super(idColumn);
            this.type = type;
        }

        public ColumnsSet(Class<T> type) {
            this(type, new IdColumn<>("ID", integer -> new DefaultId(integer, type), DefaultId.class));
        }

        public List<ColumnDecorator<T, ?>> getColumnList() {
            return columnList;
        }

        public <V> ColumnsSet<T> addColumnDecorator(ColumnDecorator<T, V> columnDecorator) {
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


        protected <V> V resolveValue(Thing<V> column, ResultSet rs) throws SQLException {
            return resolve(column.get().column, rs);
        }

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
        public DefaultId handleId(ResultSet rs) throws SQLException {
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

}
