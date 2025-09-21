package fjdb.threading;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class LazyInitializer<T> {
    private volatile T instance;

    public abstract T make();

    public T get() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = make();
                }
            }
        }
        return instance;
    }

    public static <T> LazyInitializer<T> makeInitializer(Supplier<T> supplier) {
        return new LazyInitializer<T>() {
            @Override
            public T make() {
                return supplier.get();
            }
        };
    }

    public synchronized void reset() {
        instance = null;
    }
}
