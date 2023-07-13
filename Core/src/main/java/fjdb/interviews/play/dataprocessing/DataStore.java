package fjdb.interviews.play.dataprocessing;

import java.time.LocalDateTime;
import java.util.*;

public class DataStore {

    private DataKey dataKey;
    private SortedMap<LocalDateTime, Double> prices = Collections.synchronizedSortedMap(new TreeMap<>());
    private List<DataStoreListener> listeners = new ArrayList<>();

    public DataStore(DataKey dataKey) {
        this.dataKey = dataKey;
    }

    public void addListener(DataStoreListener listener) {
        listeners.add(listener);
    }
    public void add(Double price, LocalDateTime time) {
        prices.put(time, price);
        for (DataStoreListener listener : listeners) {
            listener.update(dataKey);
        }
    }

    public void getLast() {
        prices.get(prices.lastKey());
    }
}
