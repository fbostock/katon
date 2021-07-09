package fjdb.databases;

import fjdb.CleanUtils;
import fjdb.util.Pool;

import java.sql.*;

/**
 * Created by francisbostock on 01/10/2017.
 */
public class DatabaseConnection {

    private static final Pool<DatabaseAccess, DatabaseConnection> connections = new Pool<DatabaseAccess, DatabaseConnection>() {
        @Override
        public DatabaseConnection create(DatabaseAccess key) {
            return new DatabaseConnection(key.getSqlSource());
        }
    };

    private Connection connection;

    private DatabaseConnection(String sqlSource) {
        makeConnection(sqlSource);
        CleanUtils.register(this, () -> {
            try {
                System.out.println("Database shutdown");
                shutdown();
                System.out.println("Database disconnect");
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static DatabaseConnection get(DatabaseAccess access) {
        return connections.get(access);
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
//    public void setupDatabase(List<TradeDao> tradeDaos) throws SQLException {
////TODO this needs to be AbstractSqlDaos, and need to decide what mechanism to use when setting up db tables.
//        for (TradeDao tradeDao : tradeDaos) {
//            Statement stmt = connection.createStatement();
//            stmt.execute(tradeDao.createDB());
//        }
////        connection.close();
//
//    }
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
