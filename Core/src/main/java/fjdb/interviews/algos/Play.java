package fjdb.interviews.algos;

import com.google.common.collect.Lists;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Play {

    public static void main(String[] args) throws InterruptedException {

        twoThreads2();

//        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
//        List<LargeObjectFinalizer> references = new ArrayList<>();
//        List<Object> largeObjects = new ArrayList<>();
//
//        for (int i = 0; i < 10; ++i) {
//            Object largeObject = new Object();
//            largeObjects.add(largeObject);
//            references.add(new LargeObjectFinalizer(largeObject, referenceQueue));
//        }
//
//        largeObjects = null;
//        System.gc();
//
//        Reference<?> referenceFromQueue;
//        for (PhantomReference<Object> reference : references) {
//            System.out.println(reference.isEnqueued());
//        }
//
//        while ((referenceFromQueue = referenceQueue.poll()) != null) {
//            ((LargeObjectFinalizer)referenceFromQueue).finalizeResources();
//            referenceFromQueue.clear();
//        }



//        var stuff = "\nPretius\n rules\n  all!".repeat(10).lines().filter(Predicate.not(String::isBlank))
//                .map(String::strip)
//                .map(s -> s.indent(2))
//                .collect(Collectors.toList());
//        System.out.println(stuff);
//
//        String[] myArray= new ArrayList<String>().toArray(String[]::new);
//         if (true) return;

        System.out.println(Arrays.toString(finaPair(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, 13)));


        List<Integer> numbers = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) {
            numbers.add(i);
        }

        List<Integer> collect = numbers.parallelStream().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                System.out.println(Thread.currentThread().getName());
                return integer+1;
            }
        }).toList();

        System.out.println(collect);
        /*
        Solve maze:
        Vertex, contains list of adjacent vertices.
        Iterate through each, keep track of what we've visited.
        Queue<Vertex> queue = new Queue(root);
        Vertex current = null;
        Vertex exitVertex = null;

        while(!queue.isEmpty()) {
            List<Vertex> adjacents = current.getVertices();
            if (current  is exit) {
                exitVertex = current;
                break;
            }
            for(Vertex v : adjacents) {
                if (!visited.contains(v)) {
                    visited.put(v, current);

                    queue.add(v);
                }
            }
            current = queue.pop();
        }

        if(exitVertex != null) {
            LinkedList<Vertex> list = new LinkedList<Vertex>();
            Vertex parent = visited.get(exitVertex);
            while(parent!= null) {
                 list.addFirst(parent);
                 parent = visited.get(parent);
            }
            return list;
        }
        return null;//didn't find exit.

         */
    }

    private static int[] finaPair(int[] input, int target) {
        HashSet<Integer> values = new HashSet<>();
        for (int i = 0; i < input.length; i++) {
            int val = input[i];
            if (values.contains(target - val)) {
                return new int[]{val, target - val};
            } else {
                values.add(val);
            }

        }


        //Given a list of numbers and a desired sum, produce an algorithm to return a pair of numbers in the array that add
// up to the desired sum. Ideally, something in O(N).
        //1,3,,8,5,2,4,6,9,6,5,,5,,8,6,3,2.....12
        //int target
//        HashSet<Integer> values = new HashSet<>();
        /*
        for (int val : input) {
        if (values.contains(target-val) ) {
            return new int[]{target-val, val}
        }

        }

         */


        return null;
    }

    private static void twoThreads() throws InterruptedException {
        //two threads, one printing even numbers, one printing odd numbers. Design algo to get them to print in natural order.

        Semaphore semaphore = new Semaphore(1);
        Semaphore semaphore2 = new Semaphore(0);
        Thread evenThread = threadPrinter(true, semaphore, semaphore2);
        Thread oddThread = threadPrinter(false, semaphore2, semaphore);
        evenThread.start();
        oddThread.start();

        evenThread.join();
        oddThread.join();
    }

    private static Thread threadPrinter(boolean even, Semaphore semaphore, Semaphore semaphore2) {
        Thread thread = new Thread(() -> {
            int count = even ? 0 : 1;

            while (count < 100) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(count);
                count += 2;
                semaphore2.release();
            }
        });
        return thread;
    }

    private static void twoThreads2() throws InterruptedException {
        //two threads, one printing even numbers, one printing odd numbers. Design algo to get them to print in natural order.

        AtomicBoolean gate = new AtomicBoolean(true);
        Thread evenThread = threadPrinter(true, gate);
        Thread oddThread = threadPrinter(false, gate);
        evenThread.start();
        oddThread.start();

        evenThread.join();
        oddThread.join();

        int i=0;
        for (int j = 0; j < data.length; j++) {
            int datum = data[j];
            if (datum!= i++) {
                throw new RuntimeException();
            }
        }
        System.out.println(Arrays.toString(data));
    }

    private static volatile int index = 0;
    private static volatile int[] data = new int[100];
    private static Thread threadPrinter(boolean even, AtomicBoolean gate) {
        Thread thread = new Thread(() -> {
            int count = even ? 0 : 1;

            while (count < 100) {
                if (even) {
                    while(gate.get()) {
                        gate.set(false);
                        data[index++] = count;
//                    System.out.println(count);
                        count += 2;
                    }
                } else {
                    while(!gate.get()) {
                        gate.set(true);
                        data[index++] = count;
//                    System.out.println(count);
                        count += 2;
                    }

                }
            }
        });
        return thread;
    }

    public static class LargeObjectFinalizer extends PhantomReference<Object> {

        public LargeObjectFinalizer(
                Object referent, ReferenceQueue<? super Object> q) {
            super(referent, q);
        }

        public void finalizeResources() {
            // free resources
            System.out.println("clearing ...");
        }
    }




}
