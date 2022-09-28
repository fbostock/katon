package fjdb.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class to handle listeners of type V, allowing storage, removal and retrieval. This class is thread-safe, synchronising
 * on the underlying data.
 * Listeners when added to this collection are stored <I>weakly</I> - that is, they are wrapped in WeakReferences. This prevents
 * memory leaks if the holder class goes out of scope as no strong references to those listeners are stored.
 * Note: you should not store anonymous listeners in here, as they will be immediately available for garbage collection.
 *
 * @param <V>
 */
public class AbstractListenerCollection<V> {

    private final List<WeakReference<V>> listeners = Collections.synchronizedList(Lists.newArrayList());

    public void addListener(V listener) {
        listeners.add(new WeakReference<>(listener));
    }

    public void removeListener(V listener) {
        synchronized (listeners) {
            Iterator<WeakReference<V>> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                WeakReference<V> next = iterator.next();
                if (listener.equals(next.get())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }


    /**
     * Returns an immutable list of listeners which are still in scope.
     */
    public List<V> getListeners() {
        ImmutableList.Builder<V> builder = ImmutableList.builder();
        synchronized (listeners) {
            cleanse(builder::add);
        }
        return builder.build();
    }

    private void cleanse(Consumer<V> consumer) {
        synchronized (listeners) {
            Iterator<WeakReference<V>> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                WeakReference<V> ref = iterator.next();
                V v = ref.get();
                if (v == null) {
                    iterator.remove();
                } else {
                    consumer.accept(v);
                }
            }
        }
    }

}
