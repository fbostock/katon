package fjdb.interviews.db;

public interface ConfigurationService<K, T> {

    T getConfiguration(K key);

}
