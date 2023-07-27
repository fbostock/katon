package fjdb.notesapp;

import com.google.common.collect.Lists;
import fjdb.databases.DataItemIF;

import java.time.LocalDateTime;
import java.util.List;

public class NoteDataItem implements DataItemIF {

    public NoteDataItem(String title, LocalDateTime dateCreated, LocalDateTime timeModified, String content, NoteCategory category) {
        this(title, dateCreated, timeModified, content, category, Lists.newArrayList(Tag.NONE));
    }

    public NoteDataItem(String title, LocalDateTime dateCreated, LocalDateTime timeModified, String content, NoteCategory category, List<Tag> tag) {
        this.title = title;
        this.dateCreated = dateCreated;
        this.timeModified = timeModified;
        this.content = content;
        this.category = category;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getTimeModified() {
        return timeModified;
    }

    public String getContent() {
        return content;
    }

    private final String title;
    private final LocalDateTime dateCreated;
    private final LocalDateTime timeModified;
    private final String content;
    private final NoteCategory category;
    private final List<Tag> tag;

    public NoteCategory getCategory() {
        return category;
    }

    public List<Tag> getTags() {
        return tag;
    }
}
