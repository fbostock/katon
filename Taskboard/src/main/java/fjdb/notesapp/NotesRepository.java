package fjdb.notesapp;

import fjdb.databases.DatabaseAccess;
import fjdb.databases.DefaultId;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesRepository {

    DatabaseAccess access = new DatabaseAccess("Notes.sql");

    private final NotesDao dao = NotesDao.getDao(access);
    private final HashMap<DefaultId, NoteDataItem> dataMap;
    private final HashMap<DefaultId, NoteDataItem> archivedDataMap;

    public NotesRepository() {
        dataMap = new HashMap<>();
        archivedDataMap = new HashMap<>();
        HashMap<DefaultId, NoteDataItem> data = dao.loadIdToDataItems();
        for (Map.Entry<DefaultId, NoteDataItem> entry : data.entrySet()) {
            if (NoteCategory.ARCHIVE.equals(entry.getValue().getCategory())) {
                archivedDataMap.put(entry.getKey(), entry.getValue());
            } else {
                dataMap.put(entry.getKey(), entry.getValue());
            }
        }

    }

    public NotesDao getDao() {
        return dao;
    }

    /**
     * Returns a list of all notes. They are returned ordered by last time modified.
     */
    public List<Note> getNotes() {
        return getNotes(false);
    }

    public List<Note> getNotes(boolean isArchive) {
        if (isArchive) {
            return getNotes(archivedDataMap);
        } else {
            return getNotes(dataMap);
        }
    }

    private List<Note> getNotes(Map<DefaultId, NoteDataItem> map) {
        return map.entrySet().stream()
                .map(entry -> new Note(entry.getValue().getTitle(), entry.getValue().getContent(), entry.getKey()))
                .sorted(Comparator.comparing(o -> dataMap.get(o.id).getTimeModified())).toList();

    }

    public Note generate(String title) {
        return createNew(new NoteDataItem(title, LocalDateTime.now(), LocalDateTime.now(), "", NoteCategory.NORMAL));
    }

    public Note createNew(NoteDataItem noteDataItem) {
        DefaultId defaultId = dao.create(noteDataItem);
        dataMap.put(defaultId, noteDataItem);
        return new Note(noteDataItem.getTitle(), noteDataItem.getContent(), defaultId);
    }

    public void save(Note note) {
        NoteDataItem oldNoteDataItem = dataMap.get(note.id);
        try {
            NoteDataItem update = new NoteDataItem(note.title, oldNoteDataItem.getDateCreated(), LocalDateTime.now(), note.content, oldNoteDataItem.getCategory(), oldNoteDataItem.getTags());
            dao.update(update, note.id);
            dataMap.put(note.id, update);
        } catch (SQLException e) {
            //TODO send error to user.
            e.printStackTrace();
        }
    }

    public void delete(Note note) {
        //TODO do we actually want to delete, or merely archive? Perhaps archive unless content empty.
        //Item should be written out to a deletedFolder.
        dao.delete(dataMap.get(note.id));

    }

    public static class Note {
        private final String title;
        private final String content;
        private final DefaultId id;


        public Note(String title, String content, DefaultId id) {
            this.title = title;
            this.content = content;
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public DefaultId getId() {
            return id;
        }
    }
}
