package fjdb;

import java.lang.ref.Cleaner;

/**
 * A utility class for registering objects for cleaning actions once those objects become phantom reachable.
 */
public class CleanUtils {

    private static final Cleaner cleaner = Cleaner.create();

    public static void register(Object obj, Runnable action) {
        cleaner.register(obj, action);
    }

}
