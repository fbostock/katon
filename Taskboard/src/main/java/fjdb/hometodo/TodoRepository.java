package fjdb.hometodo;

import fjdb.databases.DatabaseAccess;

public class TodoRepository {

    DatabaseAccess access = new DatabaseAccess("Todos2.sql");

    private TodoDao dao = TodoDao.getDao(access);

    public TodoDao getDao() {
        return dao;
    }
}
