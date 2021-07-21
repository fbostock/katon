package fjdb.databases;

import java.io.Serializable;

/**
 * Created by francisbostock on 06/11/2017.
 */
public abstract class Id implements Serializable {

    public abstract boolean equals(Object other);

    public abstract int hashCode();
}
