package fjdb.threading;

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

}
