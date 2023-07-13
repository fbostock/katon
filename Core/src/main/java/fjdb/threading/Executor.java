package fjdb.threading;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class used to schedule tasks for execution. Applications are in charge of managing the existence of
 * the thread pool (construction and termination).
 */
public class Executor {

    private ExecutorService service;

    public Executor()
    {
        service = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
    }

    public <T> Future<T> submitTask(Callable<T> task)
    {
        return service.submit(task);
    }

    /**
     * Submit a task to be completed sequentially N times i.e. executed N times. the period denotes the frequency of
     * executions. The period will only be approximate. The tasks are committed in the same thread, and are assumed to
     * have negligible run time compared to the period for task execution.
     * @param task
     * @param n The number of times the task should be executed
     * @param period The time to wait between executions
     * @param <T>
     * @return a future for the task, to allow cancelling the task prior to the completion of N executions
     */
    public <T> Future<T> submitTaskNTimes(Callable<T> task, int n, long period)
    {
        //wrap the task in a repeated task, which is then wrapped in a MyFutureTask object. This then applies
        //the cancellation policy for whatever thread is submitting the sequential tasks to make use of.
        MyFutureTask<T> mainTask = new MyFutureTask<T>(new RepeatedTask<T>(task, n, period));
        //submit the task to the thread pool
        service.submit(mainTask);
        //immediately return the future for the submitted task.
        return mainTask;
    }


    /**
     * An extension of Callable which supports cancellation. If this is packaged up in a FutureTask, then overriding the
     * cancel method in FutureTask can call cancel on the callable to perform whatever cancellation is required.
     * @param <T>
     */
    public interface CancellableTask<T> extends Callable<T> {
        public boolean cancel();
    }

    /**
     * An implementation of FutureTask. In principle, this is something we can submit to an executor which will run,
     * and as a future, we will already have the reference required to a future object which we can cancel. We'll
     * just have to give it a go and see what happens.
     *
     * @param <T>
     */
    public class MyFutureTask<T> extends FutureTask<T>
    {
        private CancellableTask<T> task;
        public MyFutureTask(CancellableTask<T> callable) {
            super(callable);
            task = callable;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            task.cancel();
            return super.cancel(mayInterruptIfRunning);
        }
    }

    public class RepeatedTask <T> implements CancellableTask<T>
    {
        private boolean isCancelled = false;
        private int numberExecutions;
        private long period;
        private Callable<T> task;

        public RepeatedTask(Callable<T> task, int n, long period)
        {
            this.task = task;
            this.numberExecutions = n;
            this.period = period;
        }

        @Override
        public boolean cancel() {
            isCancelled = true;
            return true;
        }

        @Override
        public T call() throws Exception {

            while (numberExecutions > 0)
            {
                if (!isCancelled)
                {
                    task.call();
                    numberExecutions--;
                    wait(period);
                }
                else {
                    break;
                }
            }
            return null;
        }
    }


}
