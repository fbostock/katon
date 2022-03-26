package fjdb.mealplanner;

import com.google.common.collect.ArrayListMultimap;

public class MyMain {

    public static void main(String[] args) {
        ArrayListMultimap<Object, Object> map = ArrayListMultimap.create();
        System.out.println("Hello docker " + map.size());
    }
}
