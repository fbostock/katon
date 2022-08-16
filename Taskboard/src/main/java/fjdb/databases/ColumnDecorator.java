package fjdb.databases;

import fjdb.databases.columns.AbstractColumn;

import java.util.function.Function;

public class ColumnDecorator<T extends DataItemIF, V> {

    protected final AbstractColumn<V, ?> column;
    private final Function<T, V> extractor;

    public ColumnDecorator(AbstractColumn<V, ?> column, Function<T, V> extractor) {

        this.column = column;
        this.extractor = extractor;
    }

    public Object extract(T item) {
        return column.dbElement(get(item));
    }

    public V get(T item) {
        return extractor.apply(item);
    }

    public AbstractColumn<V, ?> getColumn() {
        return column;
    }
}
