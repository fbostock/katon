package fjdb.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by francisbostock on 17/09/2016.
 */
public class Threading {

    public static int RUNTIME_THREADS = Runtime.getRuntime().availableProcessors();

    public static void run(List<Runnable> runnables, int numberThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);
        for (Runnable runnable : runnables) {
            executorService.submit(runnable);
        }
        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void run(List<Runnable> runnables) {
        run(runnables, RUNTIME_THREADS);
    }

    /**
     * Submits runnables and returns immediately.
     * @return
     */
    public static List<Future<?>> runAndReturn(List<Runnable> runnables) {
        List<Future<?>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());
        for (Runnable runnable : runnables) {
            results.add(executorService.submit(runnable));
        }
        executorService.shutdown();
        return results;
    }

    public static <V> List<Future<V>> submit(List<Callable<V>> callables) {
        List<Future<V>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(callables.size());
        for (Callable<V> callable : callables) {
            results.add(executorService.submit(callable));
        }
        executorService.shutdown();
        return results;
    }

    /*
    an object which takes a list of futures of type V, and an object which will process that result.

    Ideally, as each future result becomes available, it should submit the result to a queue.

    There should be a thread which applies the processing step to the results stored in that queue.

    One approach is for jobs to be provided a (concurrent) queue to store their results. We don't even need Futures for this.
    Each job submits their result to the queue immediately. The worker thread processing the results simply keeps taking things
    from the queue as they are entered.

    How to do it with Futures e.g. a job running which when available will be retrievable by the (blocking) future.get() call?
    We can't simply iterate through the futures and call future.get() since futures which are available later won't get processed
    sooner.


    Create an Interface Job, which wraps a runnable or callable.
    We have a special executor service, which takes Job objects.
    When submitting tasks (runnables or callables), we wrap them in a Job. The wrapping can issue a listener which will be notified when
    the job is complete.
    The Job gets executed. Execution then makes a delegate call to the wrapped object. In the case of a Callable, we submit the callable, then immediately
    call future.get() on the future returned.
    Once that has processed, we notify the listener.
    A simple version on the job interface can simply wrap the runnable/callable and return the result (somehow?).
    The listener in the above example could be the step of issuing the result to the queue which another thread is reading results from.

     */

    public static <V> void processResults(List<Future<V>> futures) {



        for (Future<V> future : futures) {

        }


    }
}
