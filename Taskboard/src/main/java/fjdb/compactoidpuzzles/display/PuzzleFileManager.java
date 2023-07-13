package fjdb.compactoidpuzzles.display;

import com.google.common.collect.Lists;
import fjdb.compactoidpuzzles.GridFile;
import fjdb.compactoidpuzzles.TileGrid;
import fjdb.util.AbstractListenerCollection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleFileManager {

    /*
    TODO
    1) create a cache of puzzle files that is to get updated with new and deleted puzzles.
    2) implement a system of listeners to respond to file changes e.g. new puzzle added.
    3) Create a WeakListener collection, which wraps listeners in WeakReferences for the purposes of avoiding memory leaks.

     */
    private static final File default_fileDirectory = new File("/Users/francisbostock/Documents/CompactoidPuzzles");

    private final Map<String, GridFile> gridFilesCache = new HashMap<>();
    private final File fileDirectory;

//    private final List<GridFileListener> listeners = Lists.newArrayList();
    private final AbstractListenerCollection<GridFileListener> listeners = new AbstractListenerCollection<>();

    private static final PuzzleFileManager instance = new PuzzleFileManager(default_fileDirectory);

    public static final PuzzleFileManager getInstance() {
        return instance;
    }

    public static final PuzzleFileManager get(File tileGridFileDirectory) {
        return new PuzzleFileManager(tileGridFileDirectory);
    }

    private PuzzleFileManager(File filedir) {
        fileDirectory = filedir;
        loadGridFiles();
    }

    public File getFileDirectory() {
        return fileDirectory;
    }

    public List<GridFile> getGridFiles() {
        return Lists.newArrayList(gridFilesCache.values());
    }

    private void loadGridFiles() {
        File[] files = fileDirectory.listFiles();
        for (File file : files) {
            try {
                if (!file.getName().contains("puzzle")) continue;
                GridFile gridFile = GridFile.makeGrid(file.getPath());
                gridFilesCache.put(gridFile.getName(), gridFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void createFile(String name, TileGrid grid) {
        try {
            GridFile file = GridFile.createFile(PuzzleFileManager.getInstance().getFileDirectory().getPath() + "/" + name + ".txt", grid);
            gridFilesCache.put(file.getName(), file);
            updateListeners(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateListeners(GridFile gridFile) {
        for (GridFileListener listener : listeners.getListeners()) {
            listener.fileAdded(gridFile);
        }
    }

    public void addListener(GridFileListener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(GridFileListener listener) {
        listeners.removeListener(listener);
    }

    public interface GridFileListener {
        void fileAdded(GridFile gridFile);
    }
}
