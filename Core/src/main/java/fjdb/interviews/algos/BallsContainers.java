package fjdb.interviews.algos;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BallsContainers {

    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
//        System.out.println(organizingContainers3(l(l(1, 1), l(1, 1))));
//        System.out.println(organizingContainers3(l(l(0, 2), l(1, 1))));
//        System.out.println(organizingContainers3(l(l(999336263, 998799923), l(998799923, 999763019))));
//        System.out.println(organizingContainers3(l(l(997427147 ,999234285 ,998319806), l(993127006 ,999257405 ,999972351), l(999251470, 996489548 ,994064605))));
        System.out.println(organizingContainers3(l(l(997612619, 934920795, 998879231, 999926463), l(960369681, 997828120, 999792735, 979622676), l(999013654, 998634077, 997988323, 958769423), l(997409523, 999301350, 940952923, 993020546))));

/*
        we can sum up all 0s, 1s, 2s, 3s




 */

    }

    public static <T> List<T> l(T... values) {
        return Lists.newArrayList(values);
    }

    public static String organizingContainers(List<List<Integer>> container) {
        // Write your code here
        int types = container.size();
        for (int i = 0; i < types; i++) {
            List<Integer> contents = container.get(i);
            for (int j = 0; j < types; j++) {
                if (i == j) continue;
                int ballsRemoved = 0;
                for (int balls = 1; balls <= contents.get(j); balls++) {
                    int iBalls = container.get(j).get(i);
                    if (iBalls > 0) {//if there are i type balls in container j
                        int jBalls = container.get(j).get(j);
                        container.get(j).set(i, iBalls - 1);
                        container.get(j).set(j, jBalls + 1);
                        ballsRemoved++;
                    } else {
                        //search other containers for i balls
                        for (int z = 0; z < types; z++) {
                            if (z == i) continue;
                            if (z == j) continue;
                            int iBallsInZ = container.get(z).get(i);
                            if (iBallsInZ > 0) {
                                container.get(z).set(i, iBallsInZ - 1);
                                container.get(z).set(j, container.get(z).get(j) + 1);
                                ballsRemoved++;
                            }
                        }
                    }
                }
                if (ballsRemoved == contents.get(j)) {
                    contents.set(j, 0);
                } else {
                    return "Impossible";
                }
            }
        }
        return "Possible";
    }


    public static String organizingContainers2(List<List<Integer>> container) {
        // Write your code here
        int types = container.size();
        //map of type to balls of that type in the wrong containers.
        Map<Integer, AtomicLong> counters = new HashMap<>();
        Map<Integer, AtomicLong> nonContainerBalls = new HashMap<>();
        for (int i = 0; i < types; i++) {
            List<Integer> contentsOfI = container.get(i);
            for (int j = 0; j < contentsOfI.size(); j++) {
                if (i == j) continue;
                Integer delta = contentsOfI.get(j);
                if (counters.containsKey(j)) {
                    counters.get(j).addAndGet(delta);
                } else {
                    counters.put(j, new AtomicLong(delta));
                }
                if (nonContainerBalls.containsKey(i)) {
                    nonContainerBalls.get(i).addAndGet(delta);
                } else {
                    nonContainerBalls.put(i, new AtomicLong(delta));
                }
            }
        }

        //for container i, if non-i balls is different to counters.get(i).get(), then impossible.
        for (int i = 0; i < types; i++) {
            if (counters.get(i).get() != nonContainerBalls.get(i).get()) {
                return "Impossible";
            }
        }

        return "Possible";
    }

    public static String organizingContainers3(List<List<Integer>> container) {
        int types = container.size();
        Map<Integer, AtomicLong> totalInContainer = new HashMap<>();
        Map<Integer, AtomicLong> totalOfType = new HashMap<>();
        for (int i = 0; i < types; i++) {
            List<Integer> contentsOfI = container.get(i);
            for (int j = 0; j < contentsOfI.size(); j++) {
                Integer value = contentsOfI.get(j);
                if (totalInContainer.containsKey(i)) {
                    totalInContainer.get(i).addAndGet(value);
                } else {
                    totalInContainer.put(i, new AtomicLong(value));
                }
                if (totalOfType.containsKey(j)) {
                    totalOfType.get(j).addAndGet(value);
                } else {
                    totalOfType.put(j, new AtomicLong(value));
                }
            }
        }
        Set<Integer> sortedTypes = new HashSet<>();
        Set<Integer> usedContainers = new HashSet<>();
        while(true) {
            boolean progressMade = false;
            for (int i = 0; i < types; i++) {
                if (usedContainers.contains(i)) continue;
                for (int j = 0; j < types; j++) {
                    if (sortedTypes.contains(j)) continue;
                    Integer ballsOfTypeJInContainerI = container.get(i).get(j);
                    long totalInContainerExceptTypei = totalInContainer.get(i).get() - ballsOfTypeJInContainerI;
                    long totalOfTypeIInOtherContainers = totalOfType.get(j).get() - ballsOfTypeJInContainerI;
                    if (totalInContainerExceptTypei == totalOfTypeIInOtherContainers) {
                        sortedTypes.add(j);
                        usedContainers.add(i);
                        progressMade = true;
                        break;
                    }
                }
            }
            if (!progressMade) return "Impossible";
            if (sortedTypes.size() == types) break;
        }
        /*
        997427147 999234285 998319806
993127006 999257405 999972351
999251470 996489548 994064605
         */
        return "Possible";

    }

}
