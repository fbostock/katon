package fjdb.kategames.scorchedearth;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ScorchedEarth extends JPanel implements ActionListener, KeyListener {

    static final int WIDTH = 900;
    static final int HEIGHT = 600;
    static final double GRAVITY = 0.4;

    Timer timer = new Timer(16, this);
    Random rand = new Random();

    int[] terrain = new int[WIDTH];
    Tank[] tanks;
    Projectile projectile;

    int currentPlayer = 0;
    double wind = 0;
    boolean shopping = false;

    public ScorchedEarth() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        generateTerrain();

        tanks = new Tank[]{
                new Tank(120, Color.RED, false),
                new Tank(750, Color.CYAN, true)
        };

        addKeyListener(this);
        setFocusable(true);
        timer.start();
    }

    void generateTerrain() {
        int h = HEIGHT / 2;
        for (int i = 0; i < WIDTH; i++) {
            h += rand.nextInt(7) - 3;
            h = Math.max(300, Math.min(550, h));
            terrain[i] = h;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Terrain
        g.setColor(new Color(40, 160, 60));
        for (int x = 0; x < WIDTH; x++)
            g.drawLine(x, terrain[x], x, HEIGHT);

        // Tanks
        for (Tank t : tanks) t.draw(g);

        if (projectile != null) projectile.draw(g);

        drawHUD(g);
    }

    void drawHUD(Graphics g) {
        Tank t = tanks[currentPlayer];
        g.setColor(Color.WHITE);
        g.drawString("Player " + (currentPlayer + 1), 20, 20);
        g.drawString("Angle: " + t.angle, 20, 35);
        g.drawString("Power: " + t.power, 20, 50);
        g.drawString("Weapon: " + t.weapon.name, 20, 65);
        g.drawString("Money: $" + t.money, 20, 80);
        g.drawString("Wind: " + String.format("%.2f", wind), 20, 95);

        if (shopping) {
            g.drawString("SHOP: [1] Small ($0) [2] Big ($50) [3] Nuke ($200)", 250, 20);
            g.drawString("Press ENTER to continue", 250, 35);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Gravity for tanks
        for (Tank t : tanks) t.applyGravity(terrain);

        if (projectile != null) {
            projectile.update(wind);

            int x = (int) projectile.x;
            int y = (int) projectile.y;

            if (x < 0 || x >= WIDTH || y >= HEIGHT) {
                endShot();
                return;
            }

            // Terrain collision
            if (y >= terrain[x]) {
                explode(x, y, projectile.weapon);
                endShot();
            }

            // Tank collision
            for (Tank t : tanks) {
                if (t.hit(x, y)) {
                    explode(x, y, projectile.weapon);
                    t.health -= projectile.weapon.damage;
                    endShot();
                }
            }
        }

        // AI turn
        if (projectile == null && tanks[currentPlayer].ai && !shopping) {
            aiTurn();
        }

        repaint();
    }

    void explode(int cx, int cy, Weapon w) {
        for (int x = Math.max(0, cx - w.radius); x < Math.min(WIDTH, cx + w.radius); x++) {
            int dx = x - cx;
            int dy = (int) Math.sqrt(w.radius * w.radius - dx * dx);
            terrain[x] = Math.min(HEIGHT, terrain[x] + dy);
        }
    }

    void endShot() {
        projectile = null;
        shopping = true;
    }

    void nextTurn() {
        shopping = false;
        currentPlayer = (currentPlayer + 1) % tanks.length;
        wind = rand.nextDouble() * 2 - 1;
    }

    void fire(Tank t) {
        if (projectile != null) return;
        projectile = new Projectile(
                t.x,
                t.y,
                Math.cos(Math.toRadians(t.angle)) * t.power,
                -Math.sin(Math.toRadians(t.angle)) * t.power,
                t.weapon
        );
    }

    void aiTurn() {
        Tank ai = tanks[currentPlayer];
        Tank target = tanks[0];

        ai.angle = 30 + rand.nextInt(40);
        ai.power = 20 + rand.nextInt(15);
        fire(ai);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Tank t = tanks[currentPlayer];

        if (shopping) {
            if (e.getKeyCode() == KeyEvent.VK_1) t.weapon = Weapon.SMALL;
            if (e.getKeyCode() == KeyEvent.VK_2 && t.money >= 50) {
                t.weapon = Weapon.BIG;
                t.money -= 50;
            }
            if (e.getKeyCode() == KeyEvent.VK_3 && t.money >= 200) {
                t.weapon = Weapon.NUKE;
                t.money -= 200;
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER) nextTurn();
            return;
        }

        if (projectile == null) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) t.angle -= 2;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) t.angle += 2;
            if (e.getKeyCode() == KeyEvent.VK_UP) t.power += 1;
            if (e.getKeyCode() == KeyEvent.VK_DOWN) t.power -= 1;
            if (e.getKeyCode() == KeyEvent.VK_SPACE) fire(t);
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("Scorched Earth - Java");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new ScorchedEarth());
        f.pack();
        f.setVisible(true);
    }

    // ===== Classes =====

    static class Tank {
        int x, y;
        int angle = 45;
        int power = 25;
        int health = 100;
        int money = 200;
        boolean ai;
        Color color;
        Weapon weapon = Weapon.SMALL;

        Tank(int x, Color c, boolean ai) {
            this.x = x;
            this.color = c;
            this.ai = ai;
        }

        void applyGravity(int[] terrain) {
            while (y < terrain[x] - 1) y++;
        }

        boolean hit(int px, int py) {
            return new Rectangle(x - 10, y - 6, 20, 12).contains(px, py);
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x - 10, y - 6, 20, 12);
            int bx = (int) (x + Math.cos(Math.toRadians(angle)) * 15);
            int by = (int) (y - Math.sin(Math.toRadians(angle)) * 15);
            g.drawLine(x, y, bx, by);
        }
    }

    static class Projectile {
        double x, y, vx, vy;
        Weapon weapon;

        Projectile(double x, double y, double vx, double vy, Weapon w) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.weapon = w;
        }

        void update(double wind) {
            x += vx;
            y += vy;
            vx += wind * 0.01;
            vy += GRAVITY;
        }

        void draw(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillOval((int) x - 3, (int) y - 3, 6, 6);
        }
    }

    enum Weapon {
        SMALL("Small", 20, 20, 10),
        BIG("Big", 40, 40, 25),
        NUKE("Nuke", 80, 80, 60);

        String name;
        int radius;
        int damage;
        int cost;

        Weapon(String n, int r, int d, int c) {
            name = n;
            radius = r;
            damage = d;
            cost = c;
        }
    }
}
