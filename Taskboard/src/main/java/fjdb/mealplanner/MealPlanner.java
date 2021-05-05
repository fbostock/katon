package fjdb.mealplanner;

import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class MealPlanner {

    /*
    Possible features
    A way to modify a meal - e.g. the meals represent templates, which can be modified, like taking the
    template pella recipe (with chorizo and chicken) and swapping the chicken for prawns.

    Dishes should have tags e.g. breakfast, brunch

    A Meal would be a Dish for a particular time/day, with potential modifications to the default Dish.
    e.g. Paella but with prawns instead of chicken.
     */
    public static void main(String[] args) {
        //TODO make meal list, add dialog showing list of five meals.
        launch();

    }


    public static void launch() {
        List<Dish> meals = Lists.newArrayList();
//        meals.add(new Dish("Chilli con carne", ""));
//        meals.add(new Dish("Lasagne", ""));
//        meals.add(new Dish("Paella", ""));
//        meals.add(new Dish("Pizza", ""));
//        meals.add(new Dish("Takeaway", ""));
//        meals.add(new Dish("Leftovers", ""));
        meals.add(new Leftovers(new StubDish()));

        DishLoader dishLoader = new DishLoader();
        List<String> columnNames = Lists.newArrayList("Meal", "Description");
        DishModel model = new DishModel(columnNames, meals, dishLoader);
        JTable table = new JTable(model);

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static class DishModel extends AbstractTableModel {
        private List<String> columnNames;
        private List<Dish> initialDishes;
        private List<Dish> dishes = Lists.newArrayList();
        private DishLoader dishLoader;

        public DishModel(List<String> columnNames, List<Dish> initialDishes, DishLoader dishLoader) {
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
                return dishes.get(rowIndex).getDescription();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Dish oldDish = dishes.get(rowIndex);
            Dish newDish;
            if (columnIndex == 0) {
                newDish = new Dish(oldDish.getId(), aValue.toString(), oldDish.getDescription());
            } else if (columnIndex ==1) {
                newDish = new Dish(oldDish.getId(), oldDish.getName(), aValue.toString());
            } else {
                return;
            }
            dishLoader.updateDish(newDish, oldDish);
            refresh();
        }
    }

    private static class StubDish extends Dish {

        public StubDish() {
            super("", "");
        }
    }

    private static class Leftovers extends Dish {
        private Dish parent;

        public Leftovers(Dish parent) {
            super(parent.getName() + " leftovers", parent.getDescription());
            this.parent = parent;
        }
    }
}
