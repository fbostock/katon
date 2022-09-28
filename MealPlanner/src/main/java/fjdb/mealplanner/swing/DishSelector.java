package fjdb.mealplanner.swing;

import com.google.common.collect.Lists;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.loaders.CompositeDishLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

//TODO rename, as its generic (not dependent on Dishes). Rename variables as well.
public class DishSelector<T> extends JPanel {

    //TODO replace with a JList. If the selector is used for a single selection, it provides one list. If multiple,
    //it provides two JLists, allowing elements to be switched between the two.
    /*
    A class that will be used to pick out Dishes. In first instance, a simple list of all dishes in alphabetical
    order, with a search panel to reduce the list (or create a second list with matches)
     */

    public static void main(String[] args) {
        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new DishSelector<>(new CompositeDishLoader(DaoManager.PRODUCTION).getDishes(), Comparator.comparing(Dish::getName), Dish::getName));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    final List<T> dishes = Lists.newArrayList();
    final SelectedPanel<T> dishSelectedPanel;
    private Function<T, String> labeller;

    public DishSelector(List<T> inputDishes, Function<T, String> labeller) {
        dishSelectedPanel = new SelectedPanel<>(labeller);
        dishes.addAll(inputDishes);
        setup();
    }

    public DishSelector(List<T> inputDishes, Comparator<T> comparator, Function<T, String> labeller) {
        dishSelectedPanel = new SelectedPanel<>(labeller);
        this.labeller = labeller;
        dishes.addAll(inputDishes);
        dishes.sort(comparator);
        setup();
    }

    public void setup() {

        DishListener<T> listener = dishSelectedPanel::add;
        /*
        Add selection list to one side

         */

        JComponent selectionList = selectionList(listener);

        setLayout(new BorderLayout());
        add(selectionList, BorderLayout.CENTER);
        add(dishSelectedPanel, BorderLayout.EAST);
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            List<T> selected = dishSelectedPanel.getSelected();
            for (T dish : selected) {
                System.out.println(labeller.apply(dish));
            }
        });
        add(ok, BorderLayout.SOUTH);
    }

    private JComponent selectionList(DishListener<T> listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (T dish : dishes) {
            JLabel comp = new JLabel(labeller.apply(dish));
//            comp.setToolTipText(dish.getDescription());
            panel.add(comp);
            comp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.update(dish);
                }
            });
        }

        return new JScrollPane(panel);
    }

    public T get() {
        return dishSelectedPanel.get();
    }

    public List<T> getSelected() {
        return Lists.newArrayList(dishSelectedPanel.getSelected());
    }

    private interface DishListener<T> {
        void update(T dish);
    }

    private static class SelectedPanel<T> extends JPanel {
        private final List<T> selected = Lists.newArrayList();
        private final Function<T, String> labeller;
        private final JPanel panel = new JPanel();

        public SelectedPanel() {
            this(Objects::toString);
        }

        public SelectedPanel(Function<T, String> labeller) {
            this.labeller = labeller;
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            add(panel);
        }

        public void add(T item) {
            selected.add(item);
            JButton comp = new JButton(labeller.apply(item));
            panel.add(comp);
            comp.addActionListener(e -> {
                selected.remove(item);
                panel.remove(comp);
                panel.revalidate();
                panel.repaint();
            });
            revalidate();
            repaint();
        }

        public T get() {
            //TODO what if none selected?
            return selected.get(0);
        }

        public List<T> getSelected() {
            return Lists.newArrayList(selected);
        }
    }
}
