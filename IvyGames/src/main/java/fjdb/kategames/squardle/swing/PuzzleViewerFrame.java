package fjdb.kategames.squardle.swing;

import fjdb.kategames.squardle.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PuzzleViewerFrame extends JFrame {

    public static void main(String[] args) {
        // Example puzzles - replace with your list when constructing the frame
        Puzzle p1 = new Puzzle("",1, "caterson e".replace(" ", "").toCharArray(), List.of("stone", "rates", "ears", "tone", "cart", "care", "eons", "scar"));
        Puzzle p2 = new Puzzle("", 2, "abcdefghi".toCharArray(), List.of("abc", "def", "ghi"));

        SwingUtilities.invokeLater(() -> new PuzzleViewerFrame(List.of(p1, p2)));
    }

    public PuzzleViewerFrame(List<Puzzle> puzzles) {
        super("Puzzle Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 600));

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        main.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Puzzle p : puzzles) {
            PuzzlePanel panel = new PuzzlePanel(p);
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
            main.add(panel);
            main.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(main,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getContentPane().add(scroll, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}