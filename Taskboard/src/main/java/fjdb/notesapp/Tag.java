package fjdb.notesapp;

import java.util.HashMap;
import java.util.Map;

public class Tag {

    private static final Map<String, Tag> tags = new HashMap<>();

    public static final Tag TODO = of("Todo", "General todo note");
    public static final Tag NONE = of("None", "Default");
    public static final Tag HOLIDAY = of("Holiday", "Related to holidays, like packing lists");
    public static final Tag PRESENTS = of("Presents", "Birthdays, Christmas etc.");

    private final String name;
    private final String description;

    private static Tag of(String name, String description) {
        Tag tag = new Tag(name, description);
        tags.put(name, tag);
        return tag;
    }

    public static Tag from(String name) {
        return tags.get(name);
    }

    private Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
