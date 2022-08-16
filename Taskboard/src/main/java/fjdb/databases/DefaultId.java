package fjdb.databases;

import java.util.Objects;

public class DefaultId extends DataId {

    private final Class<?> type;

    public DefaultId(int id, Class<?> type) {
        super(id);
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DefaultId defaultId = (DefaultId) o;
        return Objects.equals(type, defaultId.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

}
