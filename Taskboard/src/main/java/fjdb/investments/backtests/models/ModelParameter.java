package fjdb.investments.backtests.models;

import fjdb.util.TypedKey;

public class ModelParameter<T> implements TypedKey<T> {

    private final T defaultValue;

    public ModelParameter(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getDefault() {
        return defaultValue;
    }
}
