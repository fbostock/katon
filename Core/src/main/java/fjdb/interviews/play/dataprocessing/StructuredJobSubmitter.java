package fjdb.interviews.play.dataprocessing;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class StructuredJobSubmitter {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private final PriorityBlockingQueue<StructureJob> jobQueue = new PriorityBlockingQueue<>();
    private final BlackBox blackBox;
    private final Set<StructuredDataStoreListener> structureDataStores = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public StructuredJobSubmitter(BlackBox blackBox) {
        this.blackBox = blackBox;
    }

    public void addListener(StructuredDataStoreListener listener) {
        structureDataStores.add(listener);
    }

    public void removeListener(StructuredDataStoreListener listener) {
        structureDataStores.remove(listener);
    }

    private final AtomicBoolean updateQueue = new AtomicBoolean(false);

    public void runJob() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (!updateQueue.getAndSet(true)) {
                        StructureJob take = null;
                        try {
                            take = jobQueue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateQueue.set(false);
                        take.store.clear();
                        blackBox.run(take);
                    }
                }
            }
        });
    }

    public void startScheduling() {
        Runnable runnable = ()-> {
            if (!updateQueue.getAndSet(true)) {
                jobQueue.clear();
                for (StructuredDataStoreListener structureDataStore : structureDataStores) {
                    LocalDateTime lastUpdate = structureDataStore.getLastUpdate();
                    int updateCount = structureDataStore.getUpdateCount();
                    long time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - lastUpdate.toEpochSecond(ZoneOffset.UTC);
                    jobQueue.add(new StructureJob(structureDataStore.getKeys(), structureDataStore, time*updateCount));
                }

            }
        };
        scheduledExecutor.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);
    }

    public record StructureJob(List<DataKey> dataKeys,
                               StructuredDataStoreListener store,
                               long updateValue) implements Comparable<StructureJob> {

        @Override
        public int compareTo(StructureJob o) {
            return Long.compare(updateValue, o.updateValue);
        }

        public List<DataKey> getDataKeys() {
            return dataKeys;
        }
    }
}
