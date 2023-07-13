package fjdb.hometodo;

import com.google.common.collect.Lists;
import fjdb.databases.DataId;
import fjdb.databases.DataItemIF;
import fjdb.databases.IdColumnDao;
import fjdb.databases.IdColumnGroup;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.List;

public class DataItemModel<T extends DataItemIF, I extends DataId> extends AbstractTableModel {

    private final IdColumnDao<T, I> dao;

    public DataItemModel(IdColumnDao<T, I> dao, IFilter<T> filter) {
        this.dao = dao;
        columnGroup = dao.getColumnGroup();
        items.addAll(filter.filter(dao.load()));
    }

    public DataItemModel(IdColumnDao<T, I> dao) {
        this.dao = dao;
        columnGroup = dao.getColumnGroup();
        items.addAll(dao.load());
    }

    IdColumnGroup<T, I> columnGroup;
    List<T> items = Lists.newArrayList();

    public List<T> getItems() {
        return items;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //TODO currently only defining for enums.
        Class<?> dataType = dao.getColumnGroup().getColumn(columnIndex).getDataType();
        return dataType.isEnum() || dataType == Integer.class;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnGroup.columnCount();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return dao.getColumnGroup().getColumn(columnIndex).getDataType();
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

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            System.out.printf("Updating old value %s with new value %s\n", getValueAt(rowIndex, columnIndex), aValue);
            T updateItem = dao.updateField(items.get(rowIndex), columnGroup.getColumn(columnIndex), aValue);
            items.set(rowIndex, updateItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    public void refresh() {
        items.clear();
        items.addAll(dao.load());
        fireTableDataChanged();
    }

}
