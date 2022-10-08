package fjdb.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

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
     * Returns the first element in the list if the list is not empty. Otherwise, returns null.
     */
    public static <V> V tryFirst(List<V> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Return the last element of a list. If the list is empty, this will throw an IndexOutOfBoundsException
     */
    public static <V> V last(List<V> list) {
        return list.get(list.size() - 1);
    }

    /**
     * Returns the last element in the list if the list is not empty. Otherwise, returns null.
     */
    public static <V> V tryLast(List<V> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public static <V> List<V> randomiseOrder(List<V> input) {
        List<V> tilesCopy = Lists.newArrayList(input);
        List<V> tilesRandom = Lists.newArrayList();

        Random random = new Random();
        int max = tilesCopy.size();
        for (int i = 0; i < max; i++) {
            int next = random.nextInt(0, tilesCopy.size());
            tilesRandom.add(tilesCopy.get(next));
            V remove = tilesCopy.remove(next);
        }
        return tilesRandom;
    }
}
