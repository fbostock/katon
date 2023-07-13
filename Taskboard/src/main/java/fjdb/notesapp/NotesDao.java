package fjdb.notesapp;

import fjdb.databases.*;
import fjdb.databases.columns.DateColumn;
import fjdb.databases.columns.DefaultIdMaker;
import fjdb.databases.columns.StringColumn;
import fjdb.hometodo.DecoratedColumnDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class NotesDao extends DecoratedColumnDao<NoteDataItem, DefaultId> {

        private final ColumnsSet<NoteDataItem , DefaultId> columnSet;

        public static NotesDao getDao(DatabaseAccess access) {
            ColumnsSet<NoteDataItem, DefaultId> columnSet = getColumnSet();
            return new NotesDao(access, columnSet);
        }

        private NotesDao(DatabaseAccess access, ColumnsSet<NoteDataItem, DefaultId> columnSet) {
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
        public ColumnsSet<NoteDataItem, DefaultId> getColumnGroup() {
            return columnSet;
        }

        public static ColumnsSet<NoteDataItem, DefaultId> getColumnSet() {
            ColumnDecorator<NoteDataItem, String> titleColumn = new ColumnDecorator<>(new StringColumn("NAME", "VARCHAR(256)"), NoteDataItem::getTitle);
            ColumnDecorator<NoteDataItem, LocalDate> createdDateColumn = new ColumnDecorator<>(new DateColumn("CREATEDDATE"), NoteDataItem::getDateCreated);
            ColumnDecorator<NoteDataItem, LocalDate> modifiedDateColumn = new ColumnDecorator<>(new DateColumn("MODIFIEDDDATE"), NoteDataItem::getDateModified);
            ColumnDecorator<NoteDataItem, String> contentColumn = new ColumnDecorator<>(new StringColumn("CONTENT", "VARCHAR(32768)"), NoteDataItem::getContent);
            return new ColumnsSet<NoteDataItem, DefaultId>(new DefaultIdMaker()) {
                @Override
                public NoteDataItem makeItem(ResultSet rs) throws SQLException {
                    return new NoteDataItem(
                            //TODO to add new column, how do we update existing table? Need to have it read in old format, and output to new format somehow.
                            resolve(titleColumn.getColumn(), rs),
                            resolve(createdDateColumn.getColumn(), rs),
                            resolve(modifiedDateColumn.getColumn(), rs),
                            resolve(contentColumn.getColumn(), rs)
                    );
                }
            }.addColumnDecorator(titleColumn)
                    .addColumnDecorator(createdDateColumn)
                    .addColumnDecorator(modifiedDateColumn)
                    .addColumnDecorator(contentColumn);
        }

    }