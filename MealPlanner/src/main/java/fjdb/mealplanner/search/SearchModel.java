package fjdb.mealplanner.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fjdb.util.AbstractListenerCollection;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchModel<T> {
    private final List<T> inputs;
    private final Set<T> matches = Sets.newConcurrentHashSet();
    private final Function<T, String> labeller;
    private final AbstractListenerCollection<SearchObserver> observers = new AbstractListenerCollection<>();

    private final Job<T> job = new Job<>();

    public SearchModel(List<T> inputs, Function<T, String> labeller) {
        this.inputs = inputs;
        this.labeller = labeller;
    }

    public void addObserver(SearchObserver observer) {
        observers.addListener(observer);
    }

    public List<T> getMatches() {
        return matches.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Performs a search, and blocks until the search has completed and all matches ready to be returned.
     * @param text
     */
    public void searchAndWait(String text) {
        searchParameter(text);
    }

    private void searchParameter(String text) {
        Search<T> simpleSearch = new Search<>(new SimpleSearch<>());
        List<T> matchedInputs = simpleSearch.searchAndWait(text, inputs, labeller);
        matches.clear();
        matches.addAll(matchedInputs);
        updateObservers();
    }

    /**
     * Run search in background. Non-blocking. Calling thread expected to manually call {@link #getMatches()} to retrieve
     * results. Use {@link #addObserver(SearchObserver)} to be informed when further matches have been found.
     */
    public void searchInBackground(String text) {
        matches.clear();
        Search<T> simpleSearch = new Search<>(new SimpleSearch<>());
        ArrayBlockingQueue<T> queue = new ArrayBlockingQueue<>(100);
        Future<List<T>> matched = simpleSearch.search(text, inputs, labeller, queue);
        Thread queueUpdater = new Thread(() -> {
            while (true) {
                try {
                    while (queue.peek() != null) {
                        matches.add(queue.poll());
                    }
                    updateObservers();
                    T poll = queue.take();
                    matches.add(poll);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted - no longer checking queue");
                    break;
                }

            }
        });
        queueUpdater.start();
        job.cancel();
        job.set(matched, queueUpdater);

        Thread jobEndThread = new Thread(() -> {
            try {
                matched.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                //if the search completes, or it is cancelled, we interrupt the thread processing the queue
                queueUpdater.interrupt();
            }
        });
        jobEndThread.start();
    }

    private static class Job<T> {
        public void set(Future<List<T>> matched, Thread thread) {
            this.matched = matched;
            this.thread = thread;
        }

        private Future<List<T>> matched;
        private Thread thread;

        public void cancel() {
            if (matched != null) {
                matched.cancel(true);
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private void updateObservers() {
        for (SearchObserver observer : observers.getListeners()) {
            observer.update();
        }
    }

    private record Search<T>(SearchAlgo<T> algo) {

        public List<T> searchAndWait(String text, List<T> inputs, Function<T, String> labeller) {
            final List<T> results = Lists.newArrayList();
            for (T input : inputs) {
                if (algo.search(labeller.apply(input), text)) {
                    results.add(input);
                }
            }
            return results;
        }

        public Future<List<T>> search(String text, List<T> inputs, Function<T, String> labeller, BlockingQueue<T> queue) {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            try {
                return executorService.submit(() -> {
                    final List<T> results = Lists.newArrayList();
                    for (T input : inputs) {
                        if (algo.search(labeller.apply(input), text)) {
                            results.add(input);
                            queue.add(input);
                        }
                    }
                    return results;
                });
            } finally {
                executorService.shutdown();
            }
        }
    }
}
