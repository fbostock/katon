package fjdb.databases;

/**
 * Created by francisbostock on 06/11/2017.
 */
public abstract class Id {

    public abstract boolean equals(Object other);

    public abstract int hashCode();
}
