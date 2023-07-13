package fjdb.hometodo;

import fjdb.databases.DatabaseAccess;
import fjdb.databases.tools.DataTable;
import fjdb.util.DateTimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TodoPanel {

    //TODO Add a new Column class, being a DataItemIFColumn. It would be used to store the id of a data item of one type
    //as a field in another type.

    //TODO remove old TodoDao class, remove commented code from new one, then rename.
    public static void updateTable() {
        DatabaseAccess accessOld = new DatabaseAccess("Todos.sql");
        DatabaseAccess accessNew = new DatabaseAccess("Todos2.sql");
        TodoDao oldDao = TodoDao.getDao(accessOld);
        TodoDao newDao = TodoDao.getDao(accessNew);

        List<TodoDataItem> load = oldDao.load();
        for (TodoDataItem todoDataItem : load) {
            newDao.insert(todoDataItem);
        }

    }

    public static void main(String[] args) {
//updateTable();
// if (true) return;
        /*
        TODO Columns to add
        Done (state) column
        Due date
        Order/priority index - the field should reference the id of the following item. We'll need logic in the repository
        layer to map items so we know what what items point to others, in order to handle reordering.
        priority level
         */
        TodoRepository todoRepository = new TodoRepository();

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());

        DataTable table = DataTable.makeTable(todoRepository.getDao(), new Filter().addProgress(Progress.TODO, Progress.IN_PROGRESS));
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

//        TodoTable doneTable = TodoTable.makeTable(todoRepository.getDao(), new Filter().addProgress(Progress.DONE));

//        TodoListComponent view = new TodoListComponent(todoRepository.getDao());
//        panel.add(new JScrollPane(view), BorderLayout.CENTER);
        panel.add(addInsertPanel(todoRepository.getDao(), e -> {
            DataItemModel<?,?> model = table.getModel();
            model.refresh();
//            view.refresh();
        }), BorderLayout.SOUTH);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }


    private static JPanel addInsertPanel(TodoDao todoDao, ActionListener listener) {
        JPanel panel = new JPanel();

        Box row = Box.createHorizontalBox();
        JTextField nameField = new JFormattedTextField();
        nameField.setColumns(30);
        JComboBox<Owner> owner = makeCombo(Owner.class);
        JComboBox<Category> category = makeCombo(Category.class);
        JComboBox<Term> term = makeCombo(Term.class);
        JComboBox<Size> size = makeCombo(Size.class);

        row.add(nameField);
        row.add(owner);
        row.add(category);
        row.add(term);
        row.add(size);
        JButton ok = new JButton("Insert");
        ok.addActionListener(e -> {
            TodoDataItem todoDataItem = new TodoDataItem(nameField.getText(), owner.getItemAt(owner.getSelectedIndex()), category.getItemAt(category.getSelectedIndex()),
                    term.getItemAt(term.getSelectedIndex()), size.getItemAt(size.getSelectedIndex()), Progress.TODO, DateTimeUtil.date(20230101), 9);
            todoDao.insert(todoDataItem);
            listener.actionPerformed(e);
        });

        panel.add(row);
        panel.add(ok);
        return panel;
    }

    public static <T extends Enum<T>> JComboBox<T> makeCombo(Class<? extends T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        JComboBox<T> combo = new JComboBox<>(enumConstants);
        combo.setSelectedIndex(0);
        return combo;
    }


}
