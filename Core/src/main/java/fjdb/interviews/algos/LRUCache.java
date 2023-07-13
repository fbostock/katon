package fjdb.interviews.algos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCache<K, V> {

    //TODO we could provide a Function with which to generate the values

    Map<K, V> cache = new HashMap<>();
    LinkedList<K> recentList = new LinkedList<>();

    private int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }


    public void put(K key, V value) {
        //TODO add key value to cache.
        if (cache.size() == capacity) {
            removeLRU();
        }
        addEntry(key, value);
    }

    private void addEntry(K key, V value) {
        cache.put(key, value);
        recentList.add(key);
    }

    private void updateList(K key) {
        boolean remove = recentList.remove(key);
        recentList.add(key);
        //To be quicker, we keep a map of keys to Nodes as well. In addEntry, we populate the map with the key and the node just created
        //in the recentList.
        //Then, when removing a key, we remove the k to node mapping.
        //Then, when getting a value and calling updateList, we retrieve the node from the map using the key, remove that node
        //(by doing node.parent.next = node.next, node.next.parent = node.parent.)
        //We can't use the standard LinkedList however, as this does not expose the Node objects, but we could write one.
    }


    public V get(K key) {
        //TODO retrieve value from cache if available.
        V v = cache.get(key);
        if (v != null) {
            updateList(key);
        }
        return v;
    }


    private void removeLRU() {
        K k = recentList.removeFirst();
        cache.remove(k);
    }


    /*
    DataStructures:
    Arrays
    HashTables
    Lists
    LinkedList
    Sets - HashSet, TreeSet
    Maps
    Queues
    Stacks
    (Heap)

    Insertion, Retrieval, Deletion, Memory footprint
    Arrays: insertion by index, constant time. Deletion - requires O(n) to iterate and find element. Retrieval by index constant.
    HashTables - stores key to values, in buckets defined by hashes. Hashes should be highly differentiable, uniformly distributed.
        If overlap, multiple values in a bucket. Store as a linkedList to avoid memory issues (no predefined arrays).
        Insertion/Retrieval/Deletion - constant in best case, but with poor hashing can grow.
     Lists - typically backed by arrays: can grow, but can require reassigning memory.
        Retrieval order n, insertion quick, deletion order n.
     Sets, like HashSet, backed by hashtable with type boolean.

    Thread Safety:
    Synchronized Collections
    ConcurrentCollections
    Atomic variables



     */
}
