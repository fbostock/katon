package fjdb.databases;

import fjdb.CleanUtils;
import fjdb.investments.TradeDao;

import java.sql.*;
import java.util.List;

/**
 * Created by francisbostock on 01/10/2017.
 */
public class DatabaseConnection {

    //TODO pass in this from a main settings class where the user can configure the database storage file.
    private static final String sqlSource = "BOST.sql";
    private static final DatabaseConnection instance = new DatabaseConnection(sqlSource);

    //TODO deprecate
    @Deprecated //users should use the public constructor to pass in the DatabaseAccess
    public static DatabaseConnection getInstance() {
        return instance;
    }

    private Connection connection;

    public DatabaseConnection(String sqlSource) {
        makeConnection(sqlSource);
        CleanUtils.getCleaner().register(this, () -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public DatabaseConnection(DatabaseAccess access) {
        this(access.getSqlSource());
    }

    private Connection makeConnection(String sqlSource) {
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
    public void setupDatabase(List<TradeDao> tradeDaos) throws SQLException {
//TODO this needs to be AbstractSqlDaos, and need to decide what mechanism to use when setting up db tables.
        for (TradeDao tradeDao : tradeDaos) {
            Statement stmt = connection.createStatement();
            stmt.execute(tradeDao.createDB());
        }
//        connection.close();

    }

    public void shutdown() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("SHUTDOWN");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

}
