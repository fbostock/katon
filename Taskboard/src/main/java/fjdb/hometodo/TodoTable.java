package fjdb.hometodo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class TodoTable extends JTable {

    private TodoTable(DataItemModel <TodoDataItem, TodoId> model) {
        super(model);
    }

    public DataItemModel<TodoDataItem, TodoId> getModel() {
        TableModel model = super.getModel();
        return (DataItemModel<TodoDataItem, TodoId>) model;
    }

    public static TodoTable makeTable(TodoDao dao) {


        //TODO want a read-only column group - adopt an interface which has getters only.
//        columnGroup.

        TodoTable table = new TodoTable(new DataItemModel<>(dao));
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, makeRenderer());
        table.getColumnModel().getColumn(0).setMinWidth(200);

        return table;
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
