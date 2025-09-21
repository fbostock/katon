package fjdb;

import javax.swing.*;
import java.awt.*;

public class FloatingTextWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JWindow window = new JWindow();
            window.setBackground(new Color(0, 0, 0, 0)); // Fully transparent

            JLabel label = new JLabel("Floating Text");
            label.setFont(new Font("Arial", Font.BOLD, 24));
            label.setForeground(Color.GREEN);

            window.add(label);
            window.pack();
            window.setLocation(400, 400);
            window.setVisible(true);
        });
    }
}
