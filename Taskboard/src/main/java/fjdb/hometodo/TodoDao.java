package fjdb.hometodo;

import com.google.common.collect.Lists;
import fjdb.databases.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class TodoDao extends IdColumnDao<TodoDataItem, TodoId> {

    public TodoDao(DatabaseAccess access) {
        super(access, Columns.of());
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

    //TODO generate a class to simplify how to create a columns class based on some DataItem
    //TODO generate a class that knows how to generate a table to view a DataItem, including filters, field updaters...
    private static class Columns extends IdColumnGroup<TodoDataItem, TodoId> {

        //String name, Owner owner, Category category, Term term, Size size
        public StringColumn nameColumn = new StringColumn("NAME", "VARCHAR(256)");
        public TypeColumn<Owner> owner = new TypeColumn<>(Owner.class, "OWNER", "VARCHAR(256)");
        public TypeColumn<Category> category = new TypeColumn<>(Category.class, "CATEGORY", "VARCHAR(256)");
        public TypeColumn<Term> term = new TypeColumn<>(Term.class, "TERM", "VARCHAR(256)");
        public TypeColumn<Size> size = new TypeColumn<>(Size.class, "SIZE", "VARCHAR(256)");
        //TODO perhaps we could remove the TodoId object, and instead have an Id object (so one class) which has a type
        //of the DataItem, or in fact anything. This might simplify things rather than having to create an Id object.
        private final IdColumn<TodoId> idColumn;

        public static Columns of() {
            IdColumn<TodoId> idColumn = new IdColumn<>("ID", TodoId::new, TodoId.class);
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
    }


    /*
    TODO
    Create ColumnDecorator objects which wrap a column and a lambda for a way to extract the value from a DataItem. This
     will allow the getDataItemObjects method ti simply iterate through the column decorators. The aim is to get the specification
     of the columns done in one place and everything else automatically sorts itself out. i.e. When specifcying the columns above, instead
     we would specify column decorators, defining the extractors as methods to call on TodoDataItem.
     Next, can we simplify the handle method as well? One option might be to define a DataItemMaker interface in which you specify
     how to create the item using arguments which actually generate the column decorators.
     */

    public static class ColumnDecorator {

        private final AbstractColumn<String, String> column;
        private final Function<TodoDataItem, String> extractor;

        public ColumnDecorator(AbstractColumn<String, String> column, Function<TodoDataItem, String> extractor) {

            this.column = column;
            this.extractor = extractor;
        }

        public Object extract(TodoDataItem item) {
            return column.dbElement(extractor.apply(item));
        }
    }
}
