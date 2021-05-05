package fjdb;

import java.lang.ref.Cleaner;

public class CleanUtils {

    private static final Cleaner cleaner = Cleaner.create();

    public static final Cleaner getCleaner() {
        return cleaner;
    }
}
