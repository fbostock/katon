package fjdb.interviews.db;

public interface DatabaseInterface<K, T> {

    T loadConfiguration(K key);

    void saveConfiguration(K key, T data);

}
