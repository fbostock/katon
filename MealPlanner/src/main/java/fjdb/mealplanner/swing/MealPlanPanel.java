package fjdb.mealplanner.swing;

import com.google.common.collect.Lists;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.loaders.CompositeDishLoader;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.time.LocalDate;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MealPlanPanel extends JPanel {

    /*
    TODO
    Make a MealPlanBuilder class. This should be initialised with a MealPlan object, and makes a MealPlan.
    This builder can be used in the table model, and the setTableValue method can modify the builder.


     */

    public static void main(String[] args) {
        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new MealPlanPanel());
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public MealPlanPanel() {
        setup();
    }

    private void setup() {
        /*
        start with one week - a table with 7 rows, columns Date, Breakfast, Lunch, Dinner
         */

        //TODO make the editor the search selector, and when the item is selected, we may need to trigger the
        //editor to no longer be editing.

        JDialog dialog = new JDialog();
        dialog.setPreferredSize(new Dimension(500, 500));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final AtomicReference<TableCellEditor> editor = new AtomicReference<>();
//        TableCellEditor editor = null;

        List<Dish> dishes = new CompositeDishLoader(DaoManager.PRODUCTION).getDishes();
        SearchSelector<Dish> searchSelector = new SearchSelector<>(Dish::getName);
        SearchSelector.SelectionListener<Dish> listSelectionListener = item -> {
            Dish selectedDish = searchSelector.getSelectedItem();
            System.out.println("The selected dish is " + selectedDish);
            dialog.dispose();
            if (editor.get() != null) {
                editor.get().stopCellEditing();
            }
        };
        JPanel dishSelector = searchSelector.makePanel(dishes, listSelectionListener);
        dialog.add(dishSelector);


        JTable table = new JTable(new TableModel(LocalDate.now(), 7));
        add(new JScrollPane(table));

//use AbstractCellEditor?
        editor.set(new TableCellEditor() {
            int editingRow = -1;
            int editingCol = -1;
            Dish item = null;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                System.out.println("Here");
                editingRow = row;
                editingCol = column;
                dialog.pack();
                dialog.setVisible(true);
                System.out.printf("Selected item: %s%n", searchSelector.getSelectedItem());
                System.out.println("Then here");
                return null;
//                return new JLabel(String.valueOf(value));
            }

            @Override
            public Object getCellEditorValue() {
                System.out.println("getting cell value");
                return item;
            }

            @Override
            public boolean isCellEditable(EventObject anEvent) {
                return true;
            }

            @Override
            public boolean shouldSelectCell(EventObject anEvent) {
                return false;
            }

            @Override
            public boolean stopCellEditing() {
                item = searchSelector.getSelectedItem();
                table.setValueAt(item, editingRow, editingCol);
                System.out.println("Stop here");
                return true;
            }

            @Override
            public void cancelCellEditing() {
                System.out.println("Cancel here");

            }

            @Override
            public void addCellEditorListener(CellEditorListener l) {

            }

            @Override
            public void removeCellEditorListener(CellEditorListener l) {

            }
        });
        table.setDefaultEditor(Dish.class, editor.get());

    }

//    private static void display(JPanel panel) {
//        JDialog dialog = new JDialog();
//        dialog.setPreferredSize(new Dimension(500, 500));
//        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//        dialog.add(panel);
//        dialog.pack();
//        dialog.setVisible(true);
//        System.out.println("Finished");
//    }

    private static final int DATE = 0;
    private static final int UNFREEZE = 1;
    private static final int COOK = 2;
    private static final int BREAKFAST = 3;
    private static final int LUNCH = 4;
    private static final int DINNER = 5;

    private static class TableModel extends AbstractTableModel {

        private final LocalDate startDate;
        private final int days;
        private final List<String> columns;
        private final Map<Integer, LocalDate> dateMap = new HashMap<>();
        private final Map<LocalDate, MealsForDay> mealMap = new HashMap<>();

        public TableModel(LocalDate startDate, int days) {
            this.startDate = startDate;
            this.days = days;
            this.columns = Lists.newArrayList("Date", "Unfreeze", "Cook", "Breakfast", "Lunch", "Dinner");

            LocalDate date = startDate;
            for (int i = 0; i < days; i++) {
                date = date.plusDays(1);
                dateMap.put(i, date);
                mealMap.put(date, new MealsForDay());
            }

            MealsForDay mealsForDay = mealMap.get(dateMap.get(3));
//            mealsForDay.lunch = POACHED_EGGS;
//            mealsForDay.dinner = CHILLI;
        }

        @Override
        public int getRowCount() {
            return days;
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public String getColumnName(int column) {
            return columns.get(column);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex >= BREAKFAST ? Dish.class : String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > DATE;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LocalDate date = dateMap.get(rowIndex);
            if (columnIndex == DATE) {
                return date;
            }
            MealsForDay mealsForDay = mealMap.get(date);
            if (columnIndex == UNFREEZE) {
                return mealsForDay.unfreeze;
            } else if (columnIndex == COOK) {
                return mealsForDay.cook;
            } else if (columnIndex == BREAKFAST) {
                return mealsForDay.breakfast;
            } else if (columnIndex == LUNCH) {
                return mealsForDay.lunch;
            } else if (columnIndex == DINNER) {
                return mealsForDay.dinner;
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            System.out.println(String.format("Trying to set value for %s %s %s", aValue, rowIndex, columnIndex));
//            super.setValueAt(aValue, rowIndex, columnIndex);
            if (columnIndex >= BREAKFAST) {
                LocalDate date = dateMap.get(rowIndex);
                MealsForDay mealsForDay = mealMap.get(date);
                mealsForDay.set(columnIndex, (Dish) aValue);
            }
        }
    }

    private static class MealsForDay {
        private String cook;
        private String unfreeze;
        private Dish breakfast;
        private Dish lunch;
        private Dish dinner;

        public void set(int meal, Dish dish) {
            if (meal==BREAKFAST) {
                breakfast = dish;
            } else if (meal==LUNCH) {
                lunch = dish;
            } else if (meal==DINNER) {
                dinner = dish;
            }
        }

    }

    public static List<MealsForDay> initialise(int size) {
        List<MealsForDay> list = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            list.add(new MealsForDay());
        }
        return list;
    }

}
