package fjdb.util;

import java.util.List;

/**
 * Created by Frankie Bostock on 12/08/2017.
 */
public class ListUtil {

    /**
     * Returns the first element in the list. If the list is empty, this will throw an IndexOutOfBoundsException
     */
    public static <V> V first(List<V> list) {
        return list.get(0);
    }

    /**
     * Return the last element of a list. If the list is empty, this will throw an IndexOutOfBoundsException
     */
    public static <V> V last(List<V> list) {
        return list.get(list.size()-1);
    }

}
