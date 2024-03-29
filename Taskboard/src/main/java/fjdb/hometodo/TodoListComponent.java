package fjdb.hometodo;

import fjdb.databases.DefaultId;
import fjdb.databases.columns.AbstractColumn;
import fjdb.databases.ColumnDecorator;
import fjdb.databases.ColumnsSet;
import fjdb.databases.columns.TypeColumn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class TodoListComponent extends JPanel {

    private TodoDao dao;
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JPanel itemPanel = new JPanel();
    private Filter filter = new Filter();

    public TodoListComponent(TodoDao dao) {
        this.dao = dao;
        itemPanel.add(makePanel(filter));
        mainPanel.add(filterPanel(), BorderLayout.NORTH);
        mainPanel.add(itemPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /*
    TODO want a form a column group where you pass it a TodoDataItem and it generates the components.
     */
    private JPanel makePanel(Filter filter) {
        List<TodoDataItem> items = filter.filter(dao.load());
        ColumnsSet<TodoDataItem, DefaultId> columnGroup = dao.getColumnGroup();
        List<ColumnDecorator<TodoDataItem, ?>> columnList = columnGroup.getColumnList();
        List<String> columnNames = columnGroup.getColumnNames();


        JPanel panel = new JPanel(new GridLayout(-1, columnNames.size()));

        for (String columnName : columnNames) {
            panel.add(new JTextField(columnName));
        }

        for (TodoDataItem item : items) {
            for (int i = 0; i < columnList.size(); i++) {
                //TODO add a renderer class that handles different Column types, to provide the component for each type.
                //e.g. For a StringColumn of a certain size (varchar 256), we might default to a textArea comp, but for smaller
                //ones we use testField. Use the dataType and dbType properties to determine this.
                ColumnDecorator<TodoDataItem, ?> decorator = columnList.get(i);
//                Class<?> dataType = decorator.getColumn().getDataType();
//                String dbType = decorator.getColumn().dbType();

                if (i==0) {
                    JTextArea comp = new JTextArea(decorator.extract(item).toString());
                    comp.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
                    comp.setColumns(20);
                    comp.setLineWrap(true);
                    panel.add(comp);
                } else {
                    if (decorator.getColumn().getDataType().isEnum()) {
                        AbstractColumn<?, ?> column = decorator.getColumn();
                        if (column instanceof TypeColumn<?>) {
                            Class<?> dataType = decorator.getColumn().getDataType();
                            TypeColumn<?> col = (TypeColumn<?>) column;
                            Class<? extends Enum> dataType1 = col.getDataType();
                            JComboBox jComboBox = TodoPanel.makeCombo(dataType1);
                            jComboBox.setSelectedItem(decorator.get(item));
                            panel.add(jComboBox);
                            jComboBox.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Object selectedItem = jComboBox.getSelectedItem();
                                    try {
                                        System.out.printf("Updating field %s for %s", item, selectedItem);
                                        //TODO can we make the updateField method generic on the column type?
                                        dao.updateField(item, col, selectedItem);
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        panel.add(new JTextField(decorator.extract(item).toString()));
                    }
                }
            }
        }
        return panel;
    }

    public void refresh() {
        itemPanel.removeAll();
        itemPanel.add(makePanel(filter));
        itemPanel.revalidate();
    }

    private JPanel filterPanel() {
        Box box = Box.createHorizontalBox();
        JComboBox<Category> categoryJComboBox = TodoPanel.makeCombo(Category.class);
        JComboBox<Term> termBox = TodoPanel.makeCombo(Term.class);
        JComboBox<Owner> ownerBox = TodoPanel.makeCombo(Owner.class);
        JComboBox<Size> sizeBox = TodoPanel.makeCombo(Size.class);
        JComboBox<Progress> progressBox = TodoPanel.makeCombo(Progress.class);
        box.add(categoryJComboBox);
        box.add(termBox);
        box.add(ownerBox);
        box.add(sizeBox);
        box.add(progressBox);
        getAddItemListener(categoryJComboBox, category -> filter.addCategory(category));
        getAddItemListener(termBox, term -> filter.addTerm(term));
        getAddItemListener(ownerBox, owner -> filter.addOwner(owner));
        getAddItemListener(sizeBox, size -> filter.addSize(size));
        getAddItemListener(progressBox, progress -> filter.addProgress(progress));
        JButton refresh = new JButton("OK");
        refresh.addActionListener(e -> refresh());
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
                    filter.clear();
                    refresh();
                }
        );
        box.add(refresh);
        box.add(clear);
        JPanel panel = new JPanel();
        panel.add(box);
        return panel;
    }

    private <T> void getAddItemListener(JComboBox<T> categoryJComboBox, Consumer<T> function) {
        categoryJComboBox.addItemListener(e -> {
            T itemAt = categoryJComboBox.getItemAt(categoryJComboBox.getSelectedIndex());
            function.accept(itemAt);
        });
        categoryJComboBox.addActionListener(e -> {
            T itemAt = categoryJComboBox.getItemAt(categoryJComboBox.getSelectedIndex());
            function.accept(itemAt);
        });
    }

}
