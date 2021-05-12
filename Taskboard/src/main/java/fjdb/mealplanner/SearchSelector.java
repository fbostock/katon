package fjdb.mealplanner;

import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

public class SearchSelector {

    /*
    TODO
    - Tidy up class
    - Layout options: 1) Have visible list initially with all options (with scrollpane)
                      2) Start with just Search bar, then when two or more keystrokes have been pressed, add
                      the JList panel showing the options. Remove again if text size < 2
    - (Lower priority) when searching, do Intellij style search where we may match the leading letters of the meal,
    for instance, fp would match Fish Pie. If the searching is on a separate thread, then we could do all sorts.
     */

    public static void main(String[] args) {

        JDialog dialog = new JDialog();

        List<Dish> dishes = new DishLoader().loadDishes();
//        JFrame frame = new JFrame("");
        dialog.setPreferredSize(new Dimension(500, 500));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        SearchSelector searchSelector = new SearchSelector();
        ListSelectionListener listSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                dialog.dispose();
                Dish selectedDish = searchSelector.getSelectedDish();
                System.out.println("The selected dish is " + selectedDish);
            }
        };
        panel.add(searchSelector.makePanel(dishes, listSelectionListener));
        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);


    }

    private Dish selectedDish;

    public Dish getSelectedDish() {
        return selectedDish;
    }

    public JPanel makePanel(List<Dish> inputDishes, ListSelectionListener selectionListener) {
        JPanel panel = new JPanel();
        JTextField field = new JTextField(20);
        Vector<Dish> dishes = new Vector<>(inputDishes);
        JList<Dish> list = new JList<>(dishes);
//        list.addListSelectionListener(e -> {
//
//            selectedDish = list.getSelectedValue();
//            selectionListener.valueChanged(e);
//        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point mousePosition = list.getMousePosition();
                int i = list.locationToIndex(mousePosition);
                list.setSelectedIndex(i);
                selectedDish = list.getSelectedValue();
                selectionListener.valueChanged(null);

            }
        });
        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePosition = list.getMousePosition();
                if (mousePosition != null) {
                    int i = list.locationToIndex(mousePosition);
                    list.setSelectedIndex(i);
                }
            }
        });
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //TODO move this to diferent thread if performance is an issue
                String searchText = field.getText();
                List<Dish> results = performSearch(searchText, inputDishes);
                dishes.clear();
                dishes.addAll(results);
                list.repaint();
            }
        });

        panel.add(field);
        panel.add(list);
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            for (Dish dish : dishes) {
                System.out.println(dish.toString());
            }
        });
        panel.add(ok);
        return panel;
    }

    private static List<Dish> performSearch(String search, List<Dish> dishes) {
        String lowerCaseSearch = search.toLowerCase();
        List<Dish> list = Lists.newArrayList();
        for (Dish dish : dishes) {
            if (dish.getName().toLowerCase().contains(lowerCaseSearch)) {
                list.add(dish);
            }
        }
        return list;
    }
}
