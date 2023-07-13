package fjdb.databases.tools;

import fjdb.databases.ColumnDecorator;
import fjdb.databases.DataId;
import fjdb.databases.DataItemIF;
import fjdb.hometodo.DataItemModel;
import fjdb.hometodo.DecoratedColumnDao;
import fjdb.hometodo.IFilter;
import fjdb.hometodo.IntegerEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class DataTable<T extends DataItemIF, I extends DataId> extends JTable {

    private DataTable(DataItemModel<T, I> model) {
        super(model);
    }

    public DataItemModel<T, I> getModel() {
        TableModel model = super.getModel();
        return (DataItemModel<T, I>) model;
    }

//    public static TodoTable makeTable(TodoDao dao) {
//        return makeTable(dao, new Filter());
//    }

    public static <T extends DataItemIF, I extends DataId> DataTable<T, I> makeTable(DecoratedColumnDao<T, I> dao) {
        return makeTable(dao, ArrayList::new);
    }

    public static <T extends DataItemIF, I extends DataId> DataTable<T, I> makeTable(DecoratedColumnDao<T, I> dao, IFilter<T> filter) {

        //TODO want a read-only column group - adopt an interface which has getters only.

        DataTable<T, I> table = new DataTable<>(new DataItemModel<>(dao, filter));
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, makeRenderer());
        table.setDefaultRenderer(Integer.class, makeRenderer());
        table.getColumnModel().getColumn(0).setMinWidth(200);

        for (ColumnDecorator<T, ?> column : dao.getColumnList()) {
            if (column.getColumn().getDataType().isEnum()) {
                table.setDefaultEditor(column.getColumn().getDataType(), makeCellEditor(column.getColumn().getDataType()));
            } else if (column.getColumn().getDataType() == Integer.class) {
                table.setDefaultEditor(Integer.class, makeIntegerEditor());
            }
        }
        return table;
    }

    private static <T> TableCellEditor makeCellEditor(Class<T> clazz) {

        if (clazz.isEnum()) {
            JComboBox<T> enumCombo = new JComboBox<>(clazz.getEnumConstants());
            DefaultCellEditor defaultCellEditor = new DefaultCellEditor(enumCombo);

            return defaultCellEditor;
        } else {
            throw new IllegalArgumentException("Editors only supported for enums at present");
        }

    }

    private static TableCellEditor makeIntegerEditor() {
        return IntegerEditor.of(0, 100);
    }


    private static DefaultTableCellRenderer makeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        comp.setBackground(Color.lightGray);
                    } else {
                        comp.setBackground(Color.white);
                    }
                }
                return comp;
            }
        };
    }


}
