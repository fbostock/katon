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
     */
    public static void main(String[] args) {
        //TODO make meal list, add dialog showing list of five meals.
        launch();

    }


    public static void launch() {
        List<Meal> meals = Lists.newArrayList();
        meals.add(new Meal("Chilli con carne"));
        meals.add(new Meal("Lasagne"));
        meals.add(new Meal("Paella"));
        meals.add(new Meal("Pizza"));
        meals.add(new Meal("Takeaway"));
        meals.add(new Meal("Leftovers"));

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
                    return meals.get(rowIndex).name;
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


    private static class Meal {
        private String name;

        public Meal(String name) {
            this.name = name;
        }

    }

    private static class Leftovers extends Meal {
        private Meal parent;

        public Leftovers(Meal parent) {
            super(parent.name + " leftovers");
            this.parent = parent;
        }
    }
}
