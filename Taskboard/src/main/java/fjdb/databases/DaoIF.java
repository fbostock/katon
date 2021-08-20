package fjdb.databases;

import java.util.List;

/**
 * Created by francisbostock on 11/11/2017.
 */
public interface DaoIF<T> {
    void insert(T data);

    void delete(T data);

    void update(T oldData, T newData);

    List<T> load();
}
