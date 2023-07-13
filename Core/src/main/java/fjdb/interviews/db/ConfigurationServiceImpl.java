package fjdb.interviews.db;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class ConfigurationServiceImpl<K, T> implements ConfigurationService<K, T> {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() - 1);

    private final ConcurrentHashMap<K, Node<T>> dataStore = new ConcurrentHashMap<>();
    private final DatabaseInterface<K, T> database;

    public ConfigurationServiceImpl(DatabaseInterface<K, T> database) {
        this.database = database;
    }

    /**
     * Uses the compute method of concurrentHashMap to generate new Node items, but only if there is either a) no current node,
     * b) the node's configuration has been set to null (as it has existed for 5 minutes).
     * The second case, b), can occur as a node when created submits a cleanup routine to clear the database configuration
     * after 5 minutes.
     * If neither a) nor b) are satisfied the existing node is returned.
     * It can happen that the existing node is returned and prior to extracting the configuration, it is set to null by the clean up
     * routine. In such a circumstance the method merely tries again until success.
     */
    @Override
    public T getConfiguration(K key) {
        Node<T> node = dataStore.compute(key, (k, existingNode) -> {
            if (existingNode == null || existingNode.getConfiguration() == null) {
                return createNode(k);
            } else {
                return existingNode;
            }
        });

        T configuration = node.getConfiguration();
        if (configuration == null) {
            return getConfiguration(key);
        }
        return configuration;
    }

    private Node<T> createNode(K key) {
        Node<T> node = new Node<>(LocalDateTime.now(), database.loadConfiguration(key));
        submitCleanUp(node);
        return node;
    }

    private void submitCleanUp(Node<T> node) {
        executorService.schedule(() -> node.configuration.set(null), 5, TimeUnit.MINUTES);
    }


    private static class Node<T> {
        private LocalDateTime time;
        private AtomicReference<T> configuration;

        public Node(LocalDateTime time, T configuration) {
            this.time = time;
            this.configuration = new AtomicReference<>(configuration);
        }

        public LocalDateTime getTime() {
            return time;
        }

        public boolean isOutOfDate() {
            return LocalDateTime.now().minus(5, ChronoUnit.MINUTES).isAfter(time);
        }

        public T getConfiguration() {
            return configuration.get();
        }
    }


    /**
     * Alternative solution. We store K to FutureNode entries in the datastore. On creation of the FutureNode, it makes
     * a request to fetch the database configuration.
     * When threads call getConfiguration, they use the future to call get on the configuration, all blocking until the
     * single request has succeeded.
     * The database request also sets a 5 minute timer on the job, at which point the reference to the future (and database
     * configuration) is cleared for GC.
     * Subsequent calls to getConfiguration will attempt to create a new future. Multiple threads will block when creating the
     * future using double checked locking, so only one succeeds.
     */
    private class FutureNode {
        private LocalDateTime time;
        private K key;
        private volatile Future<T> future;

        public FutureNode(LocalDateTime time, K key) {
            this.time = time;
            this.key = key;
            future = makeConfiguration();
        }

        public LocalDateTime getTime() {
            return time;
        }

        public T getConfiguration() throws ExecutionException, InterruptedException {
            Future<T> fut = null;
            while (fut == null) {
                fut = getFuture();
            }
            return fut.get();
        }

        private Future<T> getFuture() {
            if (future == null) {
                synchronized (this) {
                    if (future == null) {
                        future = makeConfiguration();
                    }
                }
            }
            return future;
        }

        public Future<T> makeConfiguration() {
            Future<T> submit = executorService.submit(() -> {
                T database = ConfigurationServiceImpl.this.database.loadConfiguration(key);
                executorService.schedule(() -> {
                    future = null;
                }, 5, TimeUnit.MINUTES);
                return database;
            });
            return (future = submit);
        }
    }

}
