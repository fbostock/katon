package fjdb.hometodo;

import fjdb.databases.*;
import fjdb.databases.columns.DateColumn;
import fjdb.databases.columns.IntColumn;
import fjdb.databases.columns.StringColumn;
import fjdb.databases.columns.TypeColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TodoDaoPlay extends IdColumnDao<TodoDataItem, DefaultId> {

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

    public static ColumnsSet<TodoDataItem> getColumnSet() {
        ColumnDecorator<TodoDataItem, String> nameColumn = new ColumnDecorator<>(new StringColumn("NAME", "VARCHAR(256)"), TodoDataItem::getName);
        ColumnDecorator<TodoDataItem, Owner> ownerColumn = new ColumnDecorator<>(new TypeColumn<>(Owner.class, "OWNER", "VARCHAR(256)"), TodoDataItem::getOwner);
        ColumnDecorator<TodoDataItem, Category> categoryColumn = new ColumnDecorator<>(new TypeColumn<>(Category.class, "CATEGORY", "VARCHAR(256)"), TodoDataItem::getCategory);
        ColumnDecorator<TodoDataItem, Term> termColumn = new ColumnDecorator<>(new TypeColumn<>(Term.class, "TERM", "VARCHAR(256)"), TodoDataItem::getTerm);
        ColumnDecorator<TodoDataItem, Size> sizeColumn = new ColumnDecorator<>(new TypeColumn<>(Size.class, "SIZE", "VARCHAR(256)"), TodoDataItem::getSize);
        ColumnDecorator<TodoDataItem, Progress> progressColumn = new ColumnDecorator<>(new TypeColumn<>(Progress.class, "PROGRESS", "VARCHAR(256)"), TodoDataItem::getProgress);
        ColumnDecorator<TodoDataItem, LocalDate> dueDateColumn = new ColumnDecorator<>(new DateColumn("DUEDATE"), TodoDataItem::getDueDate);
        ColumnDecorator<TodoDataItem, Integer> priorityColumn = new ColumnDecorator<>(new IntColumn("PRIORITYLEVEL"), TodoDataItem::getPriority);
        return new ColumnsSet<>(TodoDataItem.class) {
            @Override
            public TodoDataItem makeItem(ResultSet rs) throws SQLException {
                return new TodoDataItem(
                        //TODO to add new column, how do we update existing table? Need to have it read in old format, and output to new format somehow.
                        resolve(nameColumn.getColumn(), rs),
                        resolve(ownerColumn.getColumn(), rs),
                        resolve(categoryColumn.getColumn(), rs),
                        resolve(termColumn.getColumn(), rs),
                        resolve(sizeColumn.getColumn(), rs),
                        resolve(progressColumn.getColumn(), rs),
                        resolve(dueDateColumn.getColumn(), rs),
                        resolve(priorityColumn.getColumn(), rs)
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

}
