package fjdb.maths.collatz;

import com.google.common.collect.Lists;
import fjdb.util.Pool;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CollaltzAnalyser {

    private Collatz algo;
    private long max = 1000000;
    private boolean trackDeps = false;

    public static void main(String[] args) {

        CollaltzAnalyser analyser = new CollaltzAnalyser(new Collatz(3, 1, new FindingRootFactory()));
        analyser.setMax(1000000).trackMostDeps(true).calculateRoots(true);
    }

    public CollaltzAnalyser(Collatz algo) {
        this.algo = algo;
//        System.out.println(String.format("\nAnalysis for {%s,%s}",p, q));
    }

    public CollaltzAnalyser setMax(long max) {
        this.max = max;
        return this;
    }

    public CollaltzAnalyser trackMostDeps(boolean trackDeps) {
        this.trackDeps = trackDeps;
        return this;
    }


    public void calculateRoots(boolean print) {
//        long MAX = 1000000;
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
            if (trackDeps) {
                collect.add(depsTracker.getMaxResult());
            }
            CollatzBinary.print(Lists.newArrayList(collect), true);
        }
    }


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

}
