package fjdb.interviews.algos;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Subsets {

    public static void main(String[] args) {

        System.out.println(nonDivisibleSubset4(3, Lists.newArrayList(1, 7, 2, 4)));//1 1 2 1
        System.out.println(nonDivisibleSubset4(4, Lists.newArrayList(19, 10, 12, 10, 24, 25, 22)));//3 2 0 2 0 1 2
        System.out.println(nonDivisibleSubset4(500, Lists.newArrayList(10, 10, 10, 10, 10)));
        System.out.println(nonDivisibleSubset4(7, Lists.newArrayList(278, 576, 496, 727, 410, 124, 338, 149, 209, 702, 282, 718, 771, 575, 436)));
//        System.out.println(nonDivisibleSubset(3, Lists.newArrayList(1, 7, 2, 4)));
//        System.out.println(nonDivisibleSubset(4, Lists.newArrayList(19, 10, 12, 10, 24, 25, 22)));
//        System.out.println(nonDivisibleSubset(500, Lists.newArrayList(10, 10, 10, 10, 10)));


    }

    public static int nonDivisibleSubset(int k, List<Integer> s) {

        int maxValue = 0;
        int prevMaxValue = 0;
        for (int m = 2; m <= s.size(); m++) {
            Set<List<Integer>> lists = recurse(s, m);
            if (testLists(lists, k)) {
                maxValue = m;
            } else {
                break;
            }
        }
        return maxValue;
    }

    public static Set<List<Integer>> getPairs(List<Integer> values) {
        Set<List<Integer>> lists = new HashSet<>();
        for (int i = 0; i < values.size(); i++) {
            for (int j = i + 1; j < values.size(); j++) {
                lists.add(Lists.newArrayList(values.get(i), values.get(j)));
            }
        }
        return lists;
    }

    public static Set<List<Integer>> recurse(List<Integer> values, int size) {
        Set<List<Integer>> lists = new HashSet<>();
        if (values.size() == size) {
            lists.add(values);
            return lists;
        }

        for (int i = 0; i < values.size(); i++) {
            List<Integer> stuff = new ArrayList<>();
            stuff.addAll(values.subList(0, i));
            stuff.addAll(values.subList(i + 1, values.size()));
            lists.addAll(recurse(stuff, size));
        }
        return lists;
    }

    public static boolean testLists(Set<List<Integer>> lists, int k) {
        for (List<Integer> list : lists) {
            if (testSet(list, k)) {
                return true;
            }
        }
        return false;
    }

    public static boolean testSet(List<Integer> list, int k) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if ((list.get(i) + list.get(j)) % k == 0) {
                    return false;
                }
            }
        }
        return true;
    }


    /*

     */

    public static int nonDivisibleSubset2(int k, List<Integer> s) {

        /**
         * generate all pairs, keeping all those that satisfy the test.
         * Then from those pairs, generate 3s.
         */
        Map<Integer, AtomicInteger> counts = new HashMap<>();
        for (Integer integer : s) {
            if (counts.containsKey(integer)) {
                counts.get(integer).incrementAndGet();
            } else {
                counts.put(integer, new AtomicInteger(1));
            }
        }

        Set<List<Integer>> allPairs = getPairs(s);
        Set<List<Integer>> goodPairs = new HashSet<>();

        for (List<Integer> entry : allPairs) {
            if (testSet(entry, k)) {
                goodPairs.add(entry);
            }
        }

        int maxValue = 2;
        int prevMaxValue = 0;

        while (true) {
            Set<List<Integer>> newEntries = new HashSet<>();
            for (Integer integer : s) {
                for (List<Integer> goodPair : goodPairs) {
                    ArrayList<Integer> integers = new ArrayList<>(goodPair);
                    integers.add(integer);
                    if (testSet(integers, k) && checkValidSet(integers, counts)) {
                        newEntries.add(integers);
                    }
                }
            }
            if (newEntries.isEmpty()) {
                break;
            } else {
                maxValue++;
            }
            goodPairs = newEntries;
        }

        return maxValue;
    }

    public static boolean checkValidSet(List<Integer> values, Map<Integer, AtomicInteger> map) {
        HashSet<Integer> integers = new HashSet<>(values);
        if (integers.size() < values.size()) {
            for (Integer integer : integers) {
                if (values.stream().filter(v -> Objects.equals(v, integer)).count() > map.get(integer).get()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int nonDivisibleSubset3(int k, List<Integer> s) {

        if (k == 1) {
            return 1;
        } else if (k == 2) {
            boolean allEven = true;
            for (Integer value : s) {
                if (value % 2 != 0) {
                    allEven = false;
                    break;
                }
            }
            if (allEven) return 0;
            if (s.size() > 1) {
                return 2;
            } else {
                return 1;
            }
        }

        int maxValue = 0;
        //TODO how to optimise, if s.size() == 100,000?
        for (int i = 0; i < s.size(); i++) {
            List<Integer> currentSet = new ArrayList<>();
            Integer initial = s.get(i);
            currentSet.add(initial);
            for (int j = 0; j < s.size(); j++) {
                if (i == j) continue;
                Integer candidate = s.get(j);
                //a, b, c, d, e,f   c, d, e
                if (test(currentSet, candidate, k)) {
                    currentSet.add(candidate);
                }
            }
            maxValue = Math.max(maxValue, currentSet.size());
        }
        return maxValue;
    }

    /*
    If I take each number, calculate residual value%k = r;

     */

    private static boolean test(List<Integer> current, Integer candidate, int factor) {
        for (Integer integer : current) {
            if ((integer + candidate) % factor == 0) return false;
        }
        return true;
    }

    public static int nonDivisibleSubset4(int k, List<Integer> s) {
        Map<Integer, AtomicInteger> residualCounters = new HashMap<>();
        for (int i = 0; i < k; i++) {
            residualCounters.put(i, new AtomicInteger(0));
        }
        for (int i = 0; i < s.size(); i++) {
            Integer integer = s.get(i);
            int residual = integer % k;
            residualCounters.get(residual).incrementAndGet();
        }

        int subSetSize = 0;
        //add one from the numbers exactly divisible by k
        subSetSize += Math.min(1, residualCounters.get(0).get());


        for (int i = 1; i < k/2.0; i++) {
            subSetSize += Math.max(residualCounters.get(i).get(), residualCounters.get(k-i).get());
        }
        if (k%2==0) {
            subSetSize += Math.min(1, residualCounters.get(k/2).get());
        }

        return subSetSize;
    }

}
