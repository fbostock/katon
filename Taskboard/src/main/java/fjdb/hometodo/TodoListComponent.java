package fjdb.hometodo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fjdb.databases.IdColumnGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TodoListComponent extends JPanel {

    private TodoDaoPlay dao;
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JPanel itemPanel = new JPanel();
    private Filter filter = new Filter();

    public TodoListComponent(TodoDaoPlay dao) {
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
        TodoDaoPlay.ColumnsSet<TodoDataItem> columnGroup = dao.getColumnGroup();
        List<TodoDaoPlay.ColumnDecorator<TodoDataItem, ?>> columnList = columnGroup.getColumnList();
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
                TodoDaoPlay.ColumnDecorator<TodoDataItem, ?> decorator = columnList.get(i);
//                Class<?> dataType = decorator.getColumn().getDataType();
//                String dbType = decorator.getColumn().dbType();

                if (i==0) {
                    JTextArea comp = new JTextArea(decorator.extract(item).toString());
                    comp.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
                    comp.setColumns(20);
                    comp.setLineWrap(true);
                    panel.add(comp);
                } else {
                    panel.add(new JTextField(decorator.extract(item).toString()));
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
        box.add(categoryJComboBox);
        box.add(termBox);
        box.add(ownerBox);
        box.add(sizeBox);
        getAddItemListener(categoryJComboBox, category -> filter.addCategory(category));
        getAddItemListener(termBox, term -> filter.addTerm(term));
        getAddItemListener(ownerBox, owner -> filter.addOwner(owner));
        getAddItemListener(sizeBox, size -> filter.addSize(size));
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

    private static class Filter {

        Set<Category> categories = Sets.newHashSet();
        Set<Term> terms = Sets.newHashSet();
        Set<Owner> owners = Sets.newHashSet();
        Set<Size> sizes = Sets.newHashSet();

        public Filter() {
        }

        public Filter addCategory(Category category) {
            categories.add(category);
            return this;
        }

        public Filter addTerm(Term term) {
            terms.add(term);
            return this;
        }

        public Filter addOwner(Owner owner) {
            owners.add(owner);
            return this;
        }

        public Filter addSize(Size size) {
            sizes.add(size);
            return this;
        }

        public void clear() {
            categories.clear();
            owners.clear();
            sizes.clear();
            terms.clear();
        }


        List<TodoDataItem> filter(List<TodoDataItem> input) {
            Stream<TodoDataItem> stream = input.stream();
            if (!owners.isEmpty()) {
                stream = stream.filter(item -> owners.contains(item.getOwner()));
            }
            if (!categories.isEmpty()) {
                stream = stream.filter(item -> categories.contains(item.getCategory()));
            }
            if (!terms.isEmpty()) {
                stream = stream.filter(item -> terms.contains(item.getTerm()));
            }
            if (!sizes.isEmpty()) {
                stream = stream.filter(item -> sizes.contains(item.getSize()));
            }
            return stream.collect(Collectors.toList());
        }
    }
}
