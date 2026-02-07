package fjdb.kategames.squardle.swing;


import fjdb.kategames.squardle.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PuzzlePanel extends JPanel {

    private final JPanel contentPanel;
    private final JButton headerButton;
    private final String title;

    public PuzzlePanel(Puzzle puzzle) {
        this.title = puzzle.word();

        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Header button acts as the clickable title (flat look)
        headerButton = new JButton("▾ " + title);
        headerButton.setFocusPainted(false);
        headerButton.setContentAreaFilled(false);
        headerButton.setBorderPainted(false);
        headerButton.setHorizontalAlignment(SwingConstants.LEFT);
        headerButton.addActionListener(this::toggleContent);
        headerButton.setFont(headerButton.getFont().deriveFont(Font.BOLD));
        add(headerButton, BorderLayout.NORTH);

        // Content panel that will be shown/hidden
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Buttons area for words
        List<String> words = puzzle.wordlist();
        JPanel buttonsPanel = new JPanel();
        int cols = 3;
        int rows = Math.max(1, (words.size() + cols - 1) / cols);
        buttonsPanel.setLayout(new GridLayout(rows, cols, 6, 6));

        for (String w : words) {
            JButton b = new JButton(w);
            b.addActionListener((ActionEvent e) -> {
                System.out.printf("\"%s\",\n", w);
                b.setEnabled(false);
            });
            buttonsPanel.add(b);
        }

        // Fill remaining grid cells so layout stays tidy
        int filled = words.size();
        int total = rows * cols;
        for (int i = filled; i < total; i++) {
            buttonsPanel.add(Box.createRigidArea(new Dimension(1, 1)));
        }

        contentPanel.add(buttonsPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Ensure panel shrinks to header when collapsed
        updateMaximumSize();

        toggleContent(null);
    }

    private void toggleContent(ActionEvent ignored) {
        boolean visible = contentPanel.isVisible();
        contentPanel.setVisible(!visible);
        headerButton.setText((visible ? "▸ " : "▾ ") + title);
        updateMaximumSize();
        revalidate();
        repaint();
        // notify ancestor scrollpane to update scrollbars immediately
        SwingUtilities.invokeLater(() -> {
            Container p = getParent();
            if (p != null) p.revalidate();
        });
    }

    private void updateMaximumSize() {
        // Allow full width but limit height to preferred so BoxLayout in the parent stacks nicely
        Dimension pref = getPreferredSize();
        setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
    }
}
