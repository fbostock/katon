package fjdb.databases;

public class DataId extends Id {
    private static final long serialVersionUID = 20210720L;

    private final int id;

    public DataId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataId id = (DataId) o;
        return this.id == id.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + id;
    }
}
