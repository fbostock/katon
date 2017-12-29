package fjdb.databases;

import java.sql.*;
import java.util.List;

/**
 * Created by francisbostock on 01/10/2017.
 */
public class DatabaseConnection {

    //TODO pass in this from a main settings class where the user can configure the database storage file.
    private static final String sqlSource = "BOST.sql";
    private static DatabaseConnection instance = new DatabaseConnection();

    public static DatabaseConnection getInstance() {
        return instance;
    }

    private Connection connection;


    private DatabaseConnection() {
        makeConnection();
    }

    private Connection makeConnection() {
        connection = null;
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            String url = "jdbc:hsqldb:file:" + sqlSource;
            connection = DriverManager.getConnection(url);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            // TODO what to do if the connection can not be established?
            e.printStackTrace();
        }  //Or any other driver

        return connection;
    }

    /**
     * Creates the tables for all the daos
     * TODO add an option to check for the presence of the table, and dropping if necessary or skipping if present.
     */
    public void setupDatabase(List<Dao> daos) throws SQLException {
        for (Dao dao : daos) {
            Statement stmt = connection.createStatement();
            stmt.execute(dao.createDB());
        }
//        connection.close();

    }

    public void shutdown() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SHUTDOWN");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public void setAutoCommit(boolean set) throws SQLException {
        connection.setAutoCommit(set);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

//    public void

}
