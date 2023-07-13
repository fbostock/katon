package fjdb.interviews.play.dataprocessing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class StructuredDataStoreListener implements DataStoreListener {
    private final AtomicReference<LocalDateTime> lastUpdate = new AtomicReference<>(LocalDateTime.now());
    private final AtomicInteger updateCount = new AtomicInteger(0);
    private final List<DataKey> dependentKeys;

    public StructuredDataStoreListener(List<DataKey> dependentKeys) {
        this.dependentKeys =  Collections.unmodifiableList(dependentKeys);
    }

    @Override
    public void update(DataKey dataKey) {
        updateCount.incrementAndGet();
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate.get();
    }

    public int getUpdateCount() {
        return updateCount.get();
    }

    public void clear() {
        lastUpdate.set(LocalDateTime.now());
        updateCount.set(0);
    }

    public List<DataKey> getKeys() {
        return dependentKeys;
    }
}
