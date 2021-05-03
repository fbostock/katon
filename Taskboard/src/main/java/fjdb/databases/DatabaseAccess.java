package fjdb.databases;

public class DatabaseAccess {

    //TODO this should contain or have access to the path (e.g. release directory) of the application
    //as the database will be stored in a particular place on an application by application basis.
    //So we should probably define a standard place to expect to find the db, as well as a standard mechanism
    //for generating the file and appropriate folders if they do not already exist.
    private final String sqlSource;

    public static final DatabaseAccess TRADE_ACCESS = new DatabaseAccess("BOST.sql");

    public DatabaseAccess(String sqlSource) {
        this.sqlSource = sqlSource;
    }

    public String getSqlSource() {
        return sqlSource;
    }
}
