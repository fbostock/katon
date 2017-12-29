package fjdb.databases;

/**
 * Created by francisbostock on 11/11/2017.
 */
public interface DaoIF<T> {
    void create(T data);
    void delete(T data);
    void update(T data);

}
