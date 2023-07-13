package fjdb.maths.collatz;

import com.google.common.collect.Lists;
import fjdb.util.Pool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Collatz {

    public static void main(String[] args) {
        Collatz algo = new Collatz(3, 1, new FindingRootFactory());
        System.out.println(algo.research(100L).printout());
        if (true) return;

        for (int i = 1; i < 200; i = i + 2) {
            calculateRoots(3, i, false);
        }
    }

    public static void calculateRoots(int p, int q, boolean print) {
        System.out.println(String.format("\nAnalysis for {%s,%s}", p, q));
//        long MAX = 1000000;
//        long MAX = 100;
        long MAX = 10000;
        long initial = 1;
        ResultFactory factory = new FindingRootFactory();
        Collatz algo = new Collatz(p, q, factory);

        Pool<Long, AtomicInteger> roots = Pool.makePool(aLong -> new AtomicInteger(0));

        for (long i = initial; i <= MAX; i++) {
            try {
                algo.research(i);
                roots.get(algo.getResultsCache().get(i).getRoot().getValue()).incrementAndGet();
                if (print && i % 10000 == 0) {
                    System.out.println(String.format("DONE %s", i));
                }
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
            CollatzBinary.print(Lists.newArrayList(collect), false);
        }
    }


    public static void main0(String[] args) {


        long MAX = 1000000;
        long initial = 1;
        boolean print = true;
        ResultFactory factory = new FixedRootResultFactory(3);
        Collatz algo = new Collatz(3, 3, factory);

        for (int i = 1; i < 10; i++) {
            Result val = algo.research(4L);
            System.out.println(val);
        }


        Result finalResult = algo.algo(factory.makeResult(5));
        System.out.println(finalResult);

        for (long i = initial; i < MAX; i++) {
            System.out.print(String.format("%s:  ", i));
            algo.algo(factory.makeResult(i));
            Result result = algo.getResultsCache().get(i);
            System.out.println(result.details());
        }


        long maxDep = 0;
        long maxDepIndex = 0;
        for (long i = 1; i < MAX; i++) {
            if (i == MAX - 1) {
                System.out.println();
            }
            int deps = algo.getResultsCache().get(i).numDependents();
            if (deps > maxDep) {
                maxDep = deps;
                maxDepIndex = i;
            }
        }
        Result maxDepResult = algo.getResultsCache().get(maxDepIndex);
        System.out.println(String.format("Result MOST DEPS: %s", maxDepResult.details()));
        TreeSet<Long> orderedResults = new TreeSet<>(algo.getResultsCache().keySet());
        System.out.println(orderedResults.last());
        List<Result> toPrint = Lists.newArrayList();

        for (long i = MAX - 10; i < MAX; i++) {
            toPrint.add(algo.getResultsCache().get(i));
        }
        Result lastResult = algo.getResultsCache().get(orderedResults.last());
        toPrint.add(lastResult);
        toPrint.add(maxDepResult);

        for (long i = 1; i < MAX; i++) {
            if (algo.getResultsCache().get(i).isDependent(lastResult)) {
                toPrint.add(algo.getResultsCache().get(i));
                break;
            }
        }

        if (print) {
            CollatzBinary.print(toPrint, false);
        }
    }

    private final int p;
    private final int q;
    private final ResultFactory factory;
    private final Map<Long, Result> _resultsCache = new ConcurrentHashMap<>();

    public Collatz(int p, int q, ResultFactory factory) {
        this.p = p;
        this.q = q;
        this.factory = factory;
    }

    public Map<Long, Result> getResultsCache() {
        return _resultsCache;
    }

    public long calc(long num) {
        if ((num & 1) == 0) {
            return num / 2;
        } else {
            return p * num + q;
        }
    }


    private Result algo(Result num) {
        if (_resultsCache.containsKey(num.getValue())) {
            return _resultsCache.get(num.getValue());
        } else {
            _resultsCache.put(num.getValue(), num);
            long value = calc(num.getValue());
            Result result = algo(factory.makeResult(value));
            num.setDependentResult(result);
            return algo(num);
        }
    }


    public Result research(Long initial) {
        /*
         Assume a Result will not resolve to a known root, but possibly another
         For a given initial value:
           Store the initial value
           apply the algo step. Get a new Result(value).
           check we don't already have that value. If we do stop - we have reached a circular point.
           If not, store the value. Apply the algo.
           Once we get to a previous value, stop. Set that value as a root, and store the root.
           Set all Results calculated for the initial value mapped to the root value.
         Continue for the next initial value which does not already appear in a map.

         */
        Set<Long> values = new HashSet<>();
        Result previous;
        if (_resultsCache.containsKey(initial)) {
            previous = _resultsCache.get(initial);
        } else {
            previous = factory.makeResult(initial);
            _resultsCache.put(initial, previous);
        }
        values.add(previous.getValue());
        long calc = calc(previous.getValue());
        while (true) {
            if (_resultsCache.containsKey(calc)) {
                previous.setDependentResult(_resultsCache.get(calc));
                if (!_resultsCache.containsKey(previous.getValue())) {
                    _resultsCache.put(previous.getValue(), previous);
                }
                if (values.contains(calc)) {
                    break;
                }
                break;
//                return null;
            } else {
                _resultsCache.put(previous.getValue(), previous);
                Result newResult = factory.makeResult(calc);
                previous.setDependentResult(newResult);
                previous = newResult;
                if (values.contains(calc)) {
                    break;
                    //come full circle
                } else {
                    calc = newResult.getValue();
                    values.add(calc);
                    calc = calc(calc);
                    if (calc < 0) {
                        Result nullResult = factory.makeResult(-1);
                        newResult.setDependentResult(nullResult);
                        _resultsCache.put(newResult.getValue(), newResult);
                        _resultsCache.put(nullResult.getValue(), nullResult);

                        break;
                    }
                }
            }

        }
        return _resultsCache.get(initial);
    }


//    protected static void print(List<Result> toPrint, boolean limit) {
//        JPanel grid = new JPanel(new GridLayout(0, 2));
//        for (int i = (limit && toPrint.size() > 100) ? toPrint.size() - 100 : 0; i < toPrint.size(); i++) {
//            Result result = toPrint.get(i);
//            grid.add(new JLabel(String.format("%s (%s steps, %s 1s in bin)", result, result.numDependents(), StringUtil.count(Long.toBinaryString(result.getValue()), "1"))));
//            JButton button = new JButton("Expand");
//            grid.add(button);
//            button.addActionListener(e -> {
//                String message = result.printout();
//                JDialog dialog = new JDialog((Window) null, String.format("%s (%s)", result, result.numDependents()));
//                dialog.add(new JScrollPane(new JTextArea(message)));
//                dialog.pack();
//                dialog.setVisible(true);
//            });
//        }
//        JFrame frame = new JFrame("");
//        frame.setPreferredSize(new Dimension(500, 500));
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        JPanel panel = new JPanel();
//        frame.add(new JScrollPane(panel));
//        panel.add(new JScrollPane(grid));
//        frame.pack();
//        frame.setVisible(true);
//    }
}
