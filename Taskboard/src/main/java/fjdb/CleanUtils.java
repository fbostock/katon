package fjdb;

import java.lang.ref.Cleaner;

public class CleanUtils {

    private static final Cleaner cleaner = Cleaner.create();

    public static void register(Object obj, Runnable action) {
        cleaner.register(obj, action);
    }

}
