package fjdb.mealplanner.search;

import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.loaders.CompositeDishLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

public class SearchSelectorPopup {

    public static void main(String[] args) {

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton ok = new JButton("OK");
        JTextField field = new JTextField();
        ok.addActionListener(e -> {
            launchDialog(frame, field);
        });
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(field, BorderLayout.CENTER);
        panel.add(ok, BorderLayout.SOUTH);
        frame.add(panel);

        frame.pack();
        frame.setVisible(true);


    }

    public static void launchDialog(JFrame frame, JTextField field) {
        List<Dish> dishes = new CompositeDishLoader(DaoManager.PRODUCTION).getDishes();

        JDialog dialog = new JDialog(frame);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);

        SearchModel<Dish> model = new SearchModel<>(dishes, Dish::toString);
        Vector<Dish> items = new Vector<>(dishes);
        JList<Dish> list = new JList<>(items);
        dialog.add(list);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point mousePosition = list.getMousePosition();
                int i = list.locationToIndex(mousePosition);
                list.setSelectedIndex(i);
                field.setText(list.getSelectedValue().toString());

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

        model.addObserver(new SearchObserver() {
            @Override
            public void update() {
                List<Dish> matches = model.getMatches();
                items.clear();
                items.addAll(matches);
                dialog.revalidate();
                dialog.repaint();
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //TODO move this to different thread if performance is an issue
                String searchText = field.getText();
                model.searchInBackground(searchText);
            }
        });

        System.out.println("Loc1 " + dialog.getLocation());
        dialog.setSize(new Dimension(400, 500));
        Point locationOnScreen = field.getLocationOnScreen();
        Point point = new Point(locationOnScreen.x, locationOnScreen.y + field.getHeight());
        dialog.setLocation(point);
        System.out.println("Loc2 " + dialog.getLocation());
        dialog.setVisible(true);

        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }


}
