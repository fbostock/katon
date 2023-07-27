package fjdb.notesapp;

import java.util.HashMap;
import java.util.Map;

public class NoteCategory {

    private static final Map<String, NoteCategory> categories = new HashMap<>();
    public static final NoteCategory ARCHIVE = of("Archive");
    public static final NoteCategory NORMAL = of("Normal");

    private final String name;

    private static NoteCategory of(String name) {
        NoteCategory noteCategory = new NoteCategory(name);
        categories.put(name, noteCategory);
        return noteCategory;
    }

    public static NoteCategory from(String name) {
        return categories.get(name);
    }

    private NoteCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
