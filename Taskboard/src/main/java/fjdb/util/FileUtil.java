package fjdb.util;

import javax.swing.*;
import java.io.File;

public class FileUtil {

    public static File openFile()
    {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            return chooser.getSelectedFile();
        }
        else
        {
            return null;
        }
    }
}
