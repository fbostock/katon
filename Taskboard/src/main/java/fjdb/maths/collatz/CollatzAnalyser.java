package fjdb.maths.collatz;

import com.google.common.collect.Lists;
import fjdb.graphics.Xform;
import fjdb.util.Pool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CollatzAnalyser {

    private Collatz algo;
    private long max = 1000000;
    private boolean trackDeps = false;
    private boolean printUpdates = false;

    public static void main(String[] args) {
//        createCalculator();
//         if (true) return;
        //TODO for roots we could not solve, it would be nice if we could store a Result object to encapsulate this, in particular so
        //that if another number depends on a root which could not be found, the calculation stops gracefully.
        CollatzAnalyser analyser = new CollatzAnalyser(new Collatz(3, 3, new FindingRootFactory()));
//        analyser.setMax(100000).trackMostDeps(true).calculateRoots(true);
        analyser.setMax(100).trackMostDeps(false).printUpdates(true).calculateRoots(true);
    }

    public CollatzAnalyser(Collatz algo) {
        this.algo = algo;
//        System.out.println(String.format("\nAnalysis for {%s,%s}",p, q));
    }

    public CollatzAnalyser setMax(long max) {
        this.max = max;
        return this;
    }

    /**
     * Track which number has the most number of steps before it hits a root.
     */
    public CollatzAnalyser trackMostDeps(boolean trackDeps) {
        this.trackDeps = trackDeps;
        return this;
    }

    public CollatzAnalyser printUpdates(boolean printUpdates) {
        this.printUpdates = printUpdates;
        return this;
    }

    public void calculateRoots(boolean print) {
        long MAX = this.max;
        long initial = 1;

        DepsTracker depsTracker = new DepsTracker();

        Pool<Long, AtomicInteger> roots = Pool.makePool(aLong -> new AtomicInteger(0));
        for (long i = initial; i <= MAX; i++) {
            try {
                Result research = algo.research(i);
                if (trackDeps) {
                    depsTracker.check(research);
                }
                roots.get(algo.getResultsCache().get(i).getRoot().getValue()).incrementAndGet();
                if (printUpdates && i % 10000 == 0) {
                    System.out.printf("DONE %s%n", i);
                }

            } catch (InvalidValue ex) {
                System.out.println(String.format("Could not find root for %s:", i) + ex.getMessage());
            } catch (Exception ex) {
                System.out.println(String.format("Problem for value %s", i));
                throw ex;
            }
        }
        Map<Long, Result> resultsCache = algo.getResultsCache();
        TreeSet<Long> results = new TreeSet<>(resultsCache.keySet());

        System.out.println(String.format("Total Roots: %s", roots.getPool().keySet().size()));
        for (Long root : new TreeSet<>(roots.getPool().keySet())) {
            Result rootResult = resultsCache.get(root);
            System.out.println(String.format("ROOT %s (%s steps): Hits %s", root, rootResult.stepsForRoot(), roots.get(root).get()));
        }
        if (print) {
            List<Result> collect = results.stream().map(resultsCache::get).collect(Collectors.toList());
            for (Long aLong : new TreeSet<>(roots.getPool().keySet())) {
                collect.add(resultsCache.get(aLong));
            }
            if (trackDeps) {
                collect.add(depsTracker.getMaxResult());
            }
            CollatzBinary.print(Lists.newArrayList(collect), false);
        }
    }

    /**
     * A class to keep track of the Result which has the most dependents i.e. the most steps before getting to a root.
     */
    private static class DepsTracker {
        long maxDep = 0;
        long maxDepIndex = 0;
        Result maxResult;

        public void check(Result result) {
            int deps = result.numDependents();
            if (deps > maxDep) {
                maxDep = deps;
                maxDepIndex = result.getValue();
                maxResult = result;
            }
        }

        public long getMax() {
            return maxDepIndex;
        }

        public Result getMaxResult() {
            return maxResult;
        }
    }


    private static void createCalculator() {

//        CollatzAnalyser analyser = new CollatzAnalyser(new Collatz(3, 1, new FindingRootFactory()));
//        analyser.setMax(100000).trackMostDeps(true).calculateRoots(true);
//        analyser.setMax(100).trackMostDeps(false).printUpdates(true).calculateRoots(true);

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        JButton run = new JButton("Run");
        JTextField numField = new JFormattedTextField();
        JPanel results = new JPanel();
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                results.removeAll();
                String text = numField.getText();
                long i = Long.parseLong(text);
                Result research = new Collatz(3, 1, new FindingRootFactory()).research((long) i);
                int dependents = research.numDependents();
                Result root = research.getRoot();
                int stepsForRoot = research.stepsForRoot();
                int evenOperations = research.numEvenOperations();
                int oddOperations = research.numOddOperations();
                String message = research.printout();
                message += String.format("\nSteps %s\n", stepsForRoot);
                message += String.format("\nDependents %s\n", dependents);
                message += String.format("\nOdd ops %s\n", oddOperations);
                message += String.format("\nEven ops %s\n", evenOperations);
                results.add(new JTextArea(message));
                results.revalidate();
                results.repaint();
            }
        });
        Box topBox = Box.createHorizontalBox();
        topBox.add(run);
        topBox.add(numField);

        panel.add(topBox, BorderLayout.NORTH);
        panel.add(results, BorderLayout.CENTER);


        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
