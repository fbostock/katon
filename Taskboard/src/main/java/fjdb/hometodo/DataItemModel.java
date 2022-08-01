package fjdb.hometodo;

import com.google.common.collect.Lists;
import fjdb.databases.DataId;
import fjdb.databases.DataItemIF;
import fjdb.databases.IdColumnDao;
import fjdb.databases.IdColumnGroup;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class DataItemModel<T extends DataItemIF, I extends DataId> extends AbstractTableModel {

    private final IdColumnDao<T, I> dao;

    public DataItemModel(IdColumnDao<T, I> dao) {
        this.dao = dao;
        columnGroup = dao.getColumnGroup();
        items.addAll(dao.load());
    }

    IdColumnGroup<T, I> columnGroup;
    List<T> items = Lists.newArrayList();

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnGroup.columnCount();
    }

    @Override
    public String getColumnName(int column) {
        return columnGroup.getColumnNames().get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List<Object> dataItemObjects = columnGroup.getDataItemObjects(items.get(rowIndex));
        return dataItemObjects.get(columnIndex);
    }

    public void refresh() {
        items.clear();
        items.addAll(dao.load());
        fireTableDataChanged();
    }

}
