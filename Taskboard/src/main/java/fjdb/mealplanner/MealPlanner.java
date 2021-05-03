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
        meals.add(new Dish("Chilli con carne", ""));
        meals.add(new Dish("Lasagne", ""));
        meals.add(new Dish("Paella", ""));
        meals.add(new Dish("Pizza", ""));
        meals.add(new Dish("Takeaway", ""));
        meals.add(new Dish("Leftovers", ""));

        List<String> columnNames = Lists.newArrayList("Meal","Description");
        JTable table = new JTable(new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return columnNames.get(column);
            }

            @Override
            public int getRowCount() {
                return meals.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return meals.get(rowIndex).getName();
                } else if (columnIndex == 1) {
                    return "";
                }
                return null;
            }
        });

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }


    private static class Leftovers extends Dish {
        private Dish parent;

        public Leftovers(Dish parent) {
            super(parent.getName() + " leftovers", parent.getDescription());
            this.parent = parent;
        }
    }
}
