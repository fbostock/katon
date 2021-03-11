package fjdb.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by francisbostock on 17/09/2016.
 */
public class Threading {

    public static void run(List<Runnable> runnables) {
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());
        for (Runnable runnable : runnables) {
            executorService.submit(runnable);
        }
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Submits runnables and returns immediately.
     */
    public static void runAndReturn(List<Runnable> runnables) {
        throw new UnsupportedOperationException("To be implemented when required");
    }

    public static <V> List<Future<V>> submit(List<Callable<V>> callables) {
        List<Future<V>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(callables.size());
        for (Callable<V> callable : callables) {
            results.add(executorService.submit(callable));
        }
        return results;
    }
}
