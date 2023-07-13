package fjdb.hometodo;

import fjdb.databases.*;

import java.util.List;

public abstract class DecoratedColumnDao<T extends DataItemIF, I extends DataId> extends IdColumnDao<T, I> {

    private final ColumnsSet<T, I> columnGroup;

    public DecoratedColumnDao(DatabaseAccess access, ColumnsSet<T, I> columnGroup) {
        super(access, columnGroup);
        this.columnGroup = columnGroup;
    }

    public List<ColumnDecorator<T, ?>> getColumnList() {
        return columnGroup.getColumnList();
    }


}
