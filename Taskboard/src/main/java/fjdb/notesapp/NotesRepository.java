package fjdb.notesapp;

import fjdb.databases.DatabaseAccess;

public class NotesRepository {

    DatabaseAccess access = new DatabaseAccess("Notes.sql");

    private NotesDao dao = NotesDao.getDao(access);

    public NotesDao getDao() {
        return dao;
    }
}
