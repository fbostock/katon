package fjdb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Frankie Bostock on 05/08/2017.
 */
public class FileUtils {

    /**
     * Attempts to move the file to the target directory. This method assumes the target directory exists.
     * @param file
     * @param targetDirectory
     * @return
     */
    public static boolean moveFile(File file, File targetDirectory) {
        File targetFile = new File(targetDirectory, file.getName());
        try {
            Files.move(file.toPath(), targetFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
