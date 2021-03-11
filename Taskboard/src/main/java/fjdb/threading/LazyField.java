package fjdb.threading;

/**
 * Created by francisbostock on 28/11/2016.
 */
public abstract class LazyField<T> {

    private volatile T field;

    public T get() {
        if (field == null) {
            synchronized (this) {
                if (field == null) {
                    field = fetch();
                }
            }
        }
        return field;
    }

    public abstract T fetch();
}
