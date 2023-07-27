package fjdb.notesapp;

import com.google.common.base.Joiner;
import fjdb.databases.ColumnDecorator;
import fjdb.databases.ColumnsSet;
import fjdb.databases.DatabaseAccess;
import fjdb.databases.DefaultId;
import fjdb.databases.columns.*;
import fjdb.hometodo.DecoratedColumnDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotesDao extends DecoratedColumnDao<NoteDataItem, DefaultId> {

    private final ColumnsSet<NoteDataItem, DefaultId> columnSet;

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
        ColumnDecorator<NoteDataItem, LocalDateTime> createdDateColumn = new ColumnDecorator<>(new DateTimeColumn("CREATEDDATE"), NoteDataItem::getDateCreated);
        ColumnDecorator<NoteDataItem, LocalDateTime> modifiedDateColumn = new ColumnDecorator<>(new DateTimeColumn("MODIFIEDDDATE"), NoteDataItem::getTimeModified);
        ColumnDecorator<NoteDataItem, String> contentColumn = new ColumnDecorator<>(new StringColumn("CONTENT", "VARCHAR(32768)"), NoteDataItem::getContent);
        ColumnDecorator<NoteDataItem, NoteCategory> categoryColumn = new ColumnDecorator<>(new ClassColumn<>("CATEGORY", "VARCHAR(64)", NoteCategory.class, NoteCategory::getName, NoteCategory::from), NoteDataItem::getCategory);
//        ColumnDecorator<NoteDataItem, Tag> tagColumn = new ColumnDecorator<>(new ClassColumn<>("TAG", "VARCHAR(64)", Tag.class, Tag::getName, Tag::from), NoteDataItem::getTags);
        ColumnDecorator<NoteDataItem, List<Tag>> tagsColumn = getTagColumn();
        return new ColumnsSet<NoteDataItem, DefaultId>(new DefaultIdMaker()) {
            @Override
            public NoteDataItem makeItem(ResultSet rs) throws SQLException {
                return new NoteDataItem(
                        //TODO to add new column, how do we update existing table? Need to have it read in old format, and output to new format somehow.
                        resolve(titleColumn.getColumn(), rs),
                        resolve(createdDateColumn.getColumn(), rs),
                        resolve(modifiedDateColumn.getColumn(), rs),
                        resolve(contentColumn.getColumn(), rs),
                        resolve(categoryColumn.getColumn(), rs),
                        resolve(tagsColumn.getColumn(), rs)
                );
            }
        }.addColumnDecorator(titleColumn)
                .addColumnDecorator(createdDateColumn)
                .addColumnDecorator(modifiedDateColumn)
                .addColumnDecorator(contentColumn)
                .addColumnDecorator(categoryColumn)
                .addColumnDecorator(tagsColumn);


    }

    private static ColumnDecorator<NoteDataItem, List<Tag>> getTagColumn() {
        Class<List<Tag>> clazz = (Class<List<Tag>>) ((List<Tag>) new ArrayList<Tag>()).getClass();
        ColumnDecorator<NoteDataItem, List<Tag>> tagColumn = new ColumnDecorator<>(new ClassColumn<>("TAG", "VARCHAR(64)",
                clazz, tags -> Joiner.on(",").join(tags.stream().map(Tag::getName).toList()), s -> {
            ArrayList<Tag> tags = new ArrayList<>();
            String[] split = s.split(",");
            for (String tag : split) {
                tags.add(Tag.from(tag));
            }
            return tags;
        }), NoteDataItem::getTags);
        return tagColumn;
    }

}