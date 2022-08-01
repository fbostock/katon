package fjdb.hometodo;

import fjdb.databases.DatabaseAccess;

public class TodoRepository {

    DatabaseAccess access = new DatabaseAccess("Todos2.sql");

    private TodoDaoPlay dao = TodoDaoPlay.getDao(access);

    public TodoDaoPlay getDao() {
        return dao;
    }
}
