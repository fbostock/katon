package fjdb.mealplanner.swing;

import com.google.common.collect.Lists;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.DbDishLoader;
import fjdb.mealplanner.Dish;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MealPlannerTest {

    /*
    Possible features
    A way to modify a meal - e.g. the meals represent templates, which can be modified, like taking the
    template pella recipe (with chorizo and chicken) and swapping the chicken for prawns.

    Dishes should have tags e.g. breakfast, brunch

    A Meal would be a Dish for a particular time/day, with potential modifications to the default Dish.
    e.g. Paella but with prawns instead of chicken.
     */

    public static void main(String[] args) {
        Map<String, String> getenv = System.getenv();
        for (Map.Entry<String, String> entry : getenv.entrySet()) {
            System.out.println(String.format("KEY %s \t\t VALUE %s", entry.getKey(), entry.getValue()));
        }

        launch();
    }


    public static void launch() {
        List<Dish> meals = Lists.newArrayList();
        meals.add(new Leftovers(new StubDish()));

        DbDishLoader dishLoader = new DbDishLoader(DaoManager.PRODUCTION);
        List<String> columnNames = Lists.newArrayList("Meal", "Description");
        DishModel model = new DishModel(columnNames, meals, dishLoader);
        JTable table = new JTable(model);

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTabbedPane tabs = new JTabbedPane();
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            Box box = Box.createHorizontalBox();
            box.add(new JLabel("Name"));
            JTextField nameField = new JTextField();
            box.add(nameField);
            box.add(new JLabel("Description"));
            JTextField descriptionField = new JTextField();
            box.add(descriptionField);

            int result = JOptionPane.showConfirmDialog(null, box, "Add Dish", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Dish dish = new Dish(nameField.getText(), descriptionField.getText());
                dishLoader.addDish(dish);
                model.refresh();
            }
        });

        panel.add(ok);
        tabs.addTab("DB Dishes", panel);
        tabs.addTab("Configure a plan", new JLabel("TODO"));

        tabs.addTab("Table Planner", new MealPlanPanel());
        tabs.addTab("Plan Editor", new JLabel("TODO"));
        frame.add(tabs);
        frame.pack();
        frame.setVisible(true);
    }

    private static class DishModel extends AbstractTableModel {
        private final List<String> columnNames;
        private final List<Dish> initialDishes;
        private final List<Dish> dishes = Lists.newArrayList();
        private final DbDishLoader dishLoader;

        public DishModel(List<String> columnNames, List<Dish> initialDishes, DbDishLoader dishLoader) {
            this.columnNames = columnNames;
            this.initialDishes = initialDishes;
            this.dishLoader = dishLoader;
            refresh();
        }

        public void refresh() {
            dishes.clear();
            dishes.addAll(initialDishes);
            dishes.addAll(dishLoader.loadDishes());
            fireTableDataChanged();
        }

        @Override
        public String getColumnName(int column) {
            return columnNames.get(column);
        }

        @Override
        public int getRowCount() {
            return dishes.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }


        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
//            return super.isCellEditable(rowIndex, columnIndex);
        }


        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return dishes.get(rowIndex).getName();
            } else if (columnIndex == 1) {
                return dishes.get(rowIndex).getDetails();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Dish oldDish = dishes.get(rowIndex);
            Dish newDish;
            if (columnIndex == 0) {
                newDish = new Dish(aValue.toString(), oldDish.getDetails());
            } else if (columnIndex == 1) {
                newDish = new Dish(oldDish.getName(), aValue.toString());
            } else {
                return;
            }
            dishLoader.updateDish(oldDish, newDish);
            refresh();
        }
    }

    public static StubDish stub() {
        return new StubDish();
    }

    public static class StubDish extends Dish {

        public StubDish() {
            super("", "");
        }
    }

    public static class Leftovers extends Dish {
        private final Dish parent;

        public Leftovers(Dish parent) {
            super(parent.getName() + " leftovers", parent.getDetails());
            this.parent = parent;
        }

        public Dish getParent() {
            return parent;
        }
    }
}
