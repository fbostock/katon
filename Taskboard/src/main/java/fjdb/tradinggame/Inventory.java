package fjdb.tradinggame;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Inventory {

    private final LockStore<Commodity> lockStore;
    //TODO create a pool class, and use that here.
    Map<Commodity, Integer> quantities = new ConcurrentHashMap<>();


    public Inventory() {
        lockStore = new LockStore<>();
    }

    public void add(Commodity commodity, int amount) {
        synchronized (lockStore.getLock(commodity)) {
            Integer currentAmount = quantities.get(commodity);
            currentAmount = currentAmount == null ? 0 : currentAmount;
            currentAmount += amount;
            quantities.put(commodity, currentAmount);
        }
    }

    public int getQuantity(Commodity commodity) {
        return quantities.get(commodity);
    }

    public List<Commodity> getCommodities() {
        return Lists.newArrayList(Sets.newTreeSet(quantities.keySet()));
    }


    private static class LockStore<V> {

        ConcurrentHashMap<V, Object> locks = new ConcurrentHashMap<>();

        Object getLock(V commodity) {
            return locks.computeIfAbsent(commodity, commodity1 -> new Object());
        }
    }


}
