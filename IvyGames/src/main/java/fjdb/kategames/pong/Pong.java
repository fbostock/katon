package fjdb.kategames.pong;// Pong.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pong extends JPanel implements ActionListener, KeyListener {

    /* ------------ Game constants ------------ */
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 80;
    private static final int BALL_SIZE   = 20;

    private static final double PADDLE_SPEED = 6.0;
    private static final double BALL_SPEED_X = 4.0;
    private static final double BALL_SPEED_Y = 3.0;

    /* ------------ Game state ------------ */
    private int leftPaddleY   = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int rightPaddleY  = HEIGHT / 2 - PADDLE_HEIGHT / 2;

    private double ballX = WIDTH / 2.0;
    private double ballY = HEIGHT / 2.0;

    private double ballVelX = BALL_SPEED_X;
    private double ballVelY = BALL_SPEED_Y;

    private int leftScore  = 0;
    private int rightScore = 0;

    /* ------------ Input flags ------------ */
    private boolean wPressed      = false;
    private boolean sPressed      = false;
    private boolean upArrowPressed   = false;
    private boolean downArrowPressed = false;

    /* ------------ Timer ------------ */
    private final Timer timer = new Timer(16, this);   // ~60 FPS

    /* ------------ Constructor ------------ */
    public Pong() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        timer.start();
    }

    /* ------------ Game loop (called by Timer) ------------ */
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    /* ------------ Update logic ------------ */
    private void updateGame() {
        // Move paddles
        if (wPressed && leftPaddleY > 0)
            leftPaddleY -= PADDLE_SPEED;
        if (sPressed && leftPaddleY + PADDLE_HEIGHT < HEIGHT)
            leftPaddleY += PADDLE_SPEED;

        if (upArrowPressed && rightPaddleY > 0)
            rightPaddleY -= PADDLE_SPEED;
        if (downArrowPressed && rightPaddleY + PADDLE_HEIGHT < HEIGHT)
            rightPaddleY += PADDLE_SPEED;

        // Move ball
        ballX += ballVelX;
        ballY += ballVelY;

        // Top/bottom collision
        if (ballY <= 0 || ballY + BALL_SIZE >= HEIGHT) {
            ballVelY = -ballVelY;
        }

        // Left/right goal
        if (ballX <= 0) {
            rightScore++;
            resetBall();
        } else if (ballX + BALL_SIZE >= WIDTH) {
            leftScore++;
            resetBall();
        }

        // Paddle collision
        Rectangle ballRect = new Rectangle((int)ballX, (int)ballY, BALL_SIZE, BALL_SIZE);
        Rectangle leftPaddleRect  = new Rectangle(0, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
        Rectangle rightPaddleRect = new Rectangle(WIDTH - PADDLE_WIDTH, rightPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT);

        if (ballRect.intersects(leftPaddleRect) || ballRect.intersects(rightPaddleRect)) {
            ballVelX = -ballVelX;
        }
    }

    /* ------------ Reset ball after a goal ------------ */
    private void resetBall() {
        ballX = WIDTH / 2.0;
        ballY = HEIGHT / 2.0;
        // Randomize direction
        ballVelX = Math.random() < 0.5 ? BALL_SPEED_X : -BALL_SPEED_X;
        ballVelY = Math.random() < 0.5 ? BALL_SPEED_Y : -BALL_SPEED_Y;
    }

    /* ------------ Painting ------------ */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw ball
        g.setColor(Color.WHITE);
        g.fillOval((int)ballX, (int)ballY, BALL_SIZE, BALL_SIZE);

        // Draw paddles
        g.fillRect(0, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH, rightPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw scores
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        String scoreText = leftScore + "   |   " + rightScore;
        FontMetrics fm = g.getFontMetrics();
        int textWidth  = fm.stringWidth(scoreText);
        int textHeight = fm.getAscent();
        g.drawString(scoreText, (WIDTH - textWidth) / 2, textHeight + 10);
    }

    /* ------------ KeyListener methods ------------ */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wPressed = true;
            case KeyEvent.VK_S -> sPressed = true;
            case KeyEvent.VK_UP    -> upArrowPressed   = true;
            case KeyEvent.VK_DOWN  -> downArrowPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wPressed = false;
            case KeyEvent.VK_S -> sPressed = false;
            case KeyEvent.VK_UP    -> upArrowPressed   = false;
            case KeyEvent.VK_DOWN  -> downArrowPressed = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) { /* unused */ }

    /* ------------ Main method ------------ */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pong");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Pong());
            frame.pack();
            frame.setLocationRelativeTo(null); // center
            frame.setVisible(true);
        });
    }
}
