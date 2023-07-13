package fjdb.notesapp;

import fjdb.databases.DataItemIF;

import java.time.LocalDate;

public class NoteDataItem implements DataItemIF {

    public NoteDataItem(String title, LocalDate dateCreated, LocalDate dateModified, String content) {
        this.title = title;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public String getContent() {
        return content;
    }

    private final String title;
    private final LocalDate dateCreated;
    private final LocalDate dateModified;
    private final String content;

}
