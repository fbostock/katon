package fjdb.maths.collatz;

import fjdb.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiFunction;

public class CollatzBinary {

    /*
    take a number, express as binary.
    Apply algo each step, and see how binary representations

    Binary series: is there a combination which could NOT converge to 100..0?
    What are the constraints that a combination could NOT converge to 1?

TODO consider printing in powers of 4 given you can only 3x+1 up to 2^2m, where m is an integer.

if we did 3x+1/3, then the "even" operation requires m to be an even multiple of 1/3. For 3x+3, what if we required a number
to be a multiple of both 2 and 3 in order to divide by three? However, if it isn't divisible by three, then the multiplication operation
takes us to an odd number. That would merely entail applying another 3x+3 operation. What happens then?
     */
    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            double pow = Math.pow(3, i);
//            Long l = Long.valueOf(Double.toString(pow).replace(".0", ""));
//            System.out.println(String.format("%s %s", l, tertiary(l, 10)));
//        }

        int q = 1;
        Collatz collatz = new Collatz(3, q, new FindingRootFactory());
        Result result = collatz.research(27L);
        result = collatz.getResultsCache().get(27L);

        List<Result> results = result.results();
        for (Result result1 : results) {
            print(q, result1, true, CollatzBinary::binary);
        }


    }

    private static void print(int q, Result result) {
        print(q, result, false);
    }

    private static void print(int q, Result result, boolean expand) {
        print(q, result, expand, CollatzBinary::binary);
    }

    private static void print(int q, Result result, boolean expand, BiFunction<Long, Integer, String> function) {
        int scale = 16;
        System.out.println(String.format("%s:\t %s", StringUtil.pad(result.toString(), 10), function.apply(result.getValue(), scale)));
        if (expand && result.getValue() % 2 != 0) {
            //2*value
            //value
            //3*value
            //+1
            System.out.println(String.format("\t\t\t\t %s", function.apply(result.getValue() * 2, scale)));
            System.out.println(String.format("\t\t\t\t %s", function.apply(result.getValue(), scale)));
            System.out.println(String.format("\t\t\t\t %s", function.apply(result.getValue() * 3, scale)));
            System.out.println(String.format("\t\t\t\t %s", function.apply(result.getValue() * 3 + q, scale)));
        }
    }

    public static String binary(Long value, int scale) {
        return binary(value, scale, true);
    }

    /**
     * Convert the value to binary, expressed with prepended zeroes to a maximum length scale. Returns the corresponding string.
     * If countOnes is true, adds the number of 1s after the binary expression.
     */
    public static String binary(Long value, int scale, boolean countOnes) {
        //long is 2^64. so convert value to binary, turn to string, then prepend to the right number of digits.
//        System.out.println(b);
        StringBuilder bin = new StringBuilder(rebase(Long.toBinaryString(value), scale));
        if (countOnes) {
            return String.format("%s (%s)", bin.toString(), count(bin.toString()));
        } else {
            return bin.toString();
        }
    }

    private static String rebase(String value, int scale) {
        StringBuilder builder = new StringBuilder(value);
        int length = builder.length();
        int zeroes = scale - length;
        for (int i = 0; i < zeroes; i++) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }


    public static String tertiary(Long value, int scale) {
        String s = Long.toString(value, 3);
        return rebase(s, scale);
    }

    private static int count(String string) {
        return StringUtil.count(string, "1");
    }

    protected static void print(List<Result> toPrint, boolean limit) {
        JPanel grid = new JPanel(new GridLayout(0, 2));
        for (int i = (limit && toPrint.size() > 100) ? toPrint.size() - 100 : 0; i < toPrint.size(); i++) {
            Result result = toPrint.get(i);
            int dependents = result.numDependents();
            dependents = dependents == 0 ? result.stepsForRoot() : dependents;
//            JLabel label = new JLabel(String.format("%s (%s steps, %s 1s in binary)", result, dependents, StringUtil.count(Long.toBinaryString(result.getValue()), "1")));
            JLabel label = new JLabel(String.format("<html>%s (%s steps, root : <b>%s</b></html>)", result, dependents, result.getRoot()));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        JMenu menu = getMenu(result);
                        JPopupMenu popup = new JPopupMenu();
                        popup.add(menu);
                        popup.show(label, e.getX(), e.getY());
                    }
                }
            });
            grid.add(label);
            JButton button = new JButton("Expand");
            grid.add(button);
            button.addActionListener(e -> {
                String message = result.printout();
                JDialog dialog = new JDialog((Window) null, String.format("%s (%s)", result, result.numDependents()));
                dialog.add(new JScrollPane(new JTextArea(message)));
                dialog.pack();
                dialog.setVisible(true);
            });
        }
        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(new JScrollPane(panel));
        panel.add(new JScrollPane(grid));
        frame.pack();
        frame.setVisible(true);
    }

    private static JMenu getMenu(Result result) {
        JMenu menu = new JMenu();
        menu.add(new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String binary = binary(result.getValue(), 16, true);
                show(result, binary);
            }
        }));
        return menu;
    }

    private static void show(Result result, String message) {
        JDialog dialog = new JDialog((Window) null, String.format("%s", result));
        dialog.add(new JScrollPane(new JTextArea(message)));
        dialog.pack();
        dialog.setVisible(true);
    }
}
