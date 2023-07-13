package fjdb.threading;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by francisbostock on 28/11/2016.
 */
public abstract class LazyConcurrentField<T> {

    private AtomicReference<T> _reference = new AtomicReference<>();

    public T get() {
        T value = _reference.get();
        if (value == null) {
            value = fetch();
            if (_reference.compareAndSet(null, value)) {
                return value;
            } else {
                return _reference.get();
            }
        }
        return value;
    }

    public abstract T fetch();
}
