package fjdb.hometodo;

import fjdb.databases.ColumnDecorator;
import fjdb.databases.DefaultId;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.EventObject;
import java.util.List;

public class TodoTable extends JTable {

    private TodoTable(DataItemModel<TodoDataItem, DefaultId> model) {
        super(model);
    }

    public DataItemModel<TodoDataItem, DefaultId> getModel() {
        TableModel model = super.getModel();
        return (DataItemModel<TodoDataItem, DefaultId>) model;
    }

    public static TodoTable makeTable(TodoDao dao) {
        return makeTable(dao, new Filter());
    }

    public static TodoTable makeTable(TodoDao dao, IFilter<TodoDataItem> filter) {

        //TODO want a read-only column group - adopt an interface which has getters only.

        TodoTable table = new TodoTable(new DataItemModel<>(dao, filter));
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, makeRenderer());
        table.setDefaultRenderer(Integer.class, makeRenderer());
        table.getColumnModel().getColumn(0).setMinWidth(200);

        for (ColumnDecorator<TodoDataItem, ?> column : dao.getColumnGroup().getColumnList()) {
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
