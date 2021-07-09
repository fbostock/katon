package fjdb.mealplanner.swing;

import com.google.common.collect.Lists;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.loaders.CompositeDishLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

public class SearchSelector<T> {
    private final Function<T, String> searcher;
    private T selectedItem;

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

        List<Dish> dishes = new CompositeDishLoader(DaoManager.PRODUCTION).getDishes();
        dialog.setPreferredSize(new Dimension(500, 500));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        SearchSelector<Dish> searchSelector = new SearchSelector<>(Dish::getName);
        SelectionListener<Dish> listSelectionListener = item -> {
            dialog.dispose();
            Dish selectedDish = searchSelector.getSelectedItem();
            System.out.println("The selected dish is " + selectedDish);
        };
        panel.add(searchSelector.makePanel(dishes, listSelectionListener));
        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);
    }

    public T getSelectedItem() {
        return selectedItem;
    }

    public SearchSelector(Function<T, String> searcher) {
        this.searcher = searcher;
    }

    public JPanel makePanel(List<T> inputItems, SelectionListener<T> selectionListener) {
        JPanel panel = new JPanel();
        JTextField field = new JTextField(20);
        Vector<T> items = new Vector<>(inputItems);
        JList<T> list = new JList<>(items);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point mousePosition = list.getMousePosition();
                int i = list.locationToIndex(mousePosition);
                list.setSelectedIndex(i);
                selectedItem = list.getSelectedValue();
                selectionListener.update(selectedItem);

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
                    List<T> results = performSearch(searchText, inputItems, searcher);
                    items.clear();
                    items.addAll(results);
                    list.setSelectedIndex(0);
                    list.repaint();
            }
        });
        field.addActionListener(e -> {
            selectedItem = list.getSelectedValue();
            selectionListener.update(selectedItem);
        });

        panel.add(field);
        panel.add(list);
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            for (T dish : items) {
                System.out.println(dish.toString());
            }
        });
        panel.add(ok);
        return panel;
    }

    private static <T> List<T> performSearch(String search, List<T> dishes, Function<T, String> searcher) {
        String lowerCaseSearch = search.toLowerCase();
        List<T> list = Lists.newArrayList();
        for (T item : dishes) {
            if (searcher.apply(item).toLowerCase().contains(lowerCaseSearch)) {
                list.add(item);
            }
        }
        return list;
    }

    public interface SelectionListener<T> {
        void update(T selected);
    }
}
