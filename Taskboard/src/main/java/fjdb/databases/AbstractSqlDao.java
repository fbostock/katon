package fjdb.databases;

import com.google.common.collect.Lists;
import fjdb.util.DateTimeUtil;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Created by francisbostock on 30/10/2017.
 */
public abstract class AbstractSqlDao {
    private final SqlResolver sqlResolver;
    private final DatabaseConnection databaseConnection;

    /**
     * Constructors should not throw Sql exceptions. that should be down to methods only.
     */
    public AbstractSqlDao(DatabaseAccess access) {
        sqlResolver = new SqlResolver();
        databaseConnection = DatabaseConnection.get(access);
    }

    public abstract String getTableName();

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    /*
    public List<Trade> load() {
        List<Trade> trades = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = DatabaseConnection.getInstance().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet resultSet = null;
        try {
            resultSet = stmt.executeQuery("SELECT * FROM " + getTableName());
            while(resultSet.next()) {
                TradeId tradeId = new TradeId(resultSet.getInt(1));
                Date date = resultSet.getDate(3);
                LocalDate tradeDate = DateTimeUtil.date(date);
                Currency currency = Currency.getInstance(resultSet.getString(6));
                Trade trade = new Trade(tradeId, resultSet.getString(2), tradeDate, resultSet.getDouble(4), resultSet.getDouble(5), currency, resultSet.getDouble(7));
                trades.add(trade);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trades;
    }
     */

    //TODO wrap exception or just throw it?
    public void doUpdate(String sql, List<Object> args) throws SQLException {
        try {
//            DatabaseConnection.getInstance().setAutoCommit(false);
            databaseConnection.setAutoCommit(false);
            //TODO create statement here and pass in to resolver. Make resolver an instance passed into the constructor.
            //TODO call con.prepareStatement(sql) passing in the string, then pass the statement to the resolver to add the args.
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);
            sqlResolver.prepare(preparedStatement, args);
            int rowsAffected = preparedStatement.executeUpdate();
            //TODO log rows affected. Check if zero and throw exception.

            databaseConnection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //TODO if there are exceptions, rollback as part of the handling, then throw exceptions as necessary.
    }

    public <V> List<V> doSelect(String sql, List<Object> args, ResultHandler<V> handler) throws SQLException {
        String content = sql;
        int num = 0;
        int i;
        while ((i = content.indexOf("?")) != -1) {
            num++;
            content = content.substring(i + 1, content.length());
        }
        if (num != args.size()) {
            throw new SQLException(String.format("Statement (%s args) and argument list (%s args) not consistent", num, args.size()));
        }


        PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);
        sqlResolver.prepare(preparedStatement, args);
        //TODO do we loop over the rs here? handler would only return a V, rather than a list.
        //YES, loop over here. The handler should just return one V.
        ResultSet resultSet = preparedStatement.executeQuery();
        List<V> values = new ArrayList<>();
        while (resultSet.next()) {
            values.add(handler.handle(resultSet));
        }
        //TODO pass on resultset to a handler. We do this so that we can close up the connection here.
        resultSet.close();
        preparedStatement.close();
        return values;
    }

    //TODO this should not be public (for testing). Need to work out what has responsibility for shutting down.
    public void shutdown() {
        databaseConnection.shutdown();
    }

    public static interface ResultHandler<V> {
        V handle(ResultSet rs) throws SQLException;
    }

    private static class SqlResolver {

        public PreparedStatement prepare(PreparedStatement preparedStatement, List<Object> objects) throws SQLException {

            for (int j = 0; j < objects.size(); j++) {
                resolve(preparedStatement, j + 1, objects.get(j));
            }
            //TODO for a pure insert query, use execute. For a select query use executeQuery. If we use the latter for an insert, get exception.
            return preparedStatement;
//            preparedStatement.execute();
        }


        public void resolve(PreparedStatement statement, int index, Object object) throws SQLException {

            if (object instanceof LocalDate) {
                LocalDate localDate = (LocalDate) object;
                statement.setDate(index, DateTimeUtil.makeDate(localDate));
            } else if (object instanceof BigDecimal) {
                BigDecimal bigDecimal = (BigDecimal) object;
                statement.setBigDecimal(index, bigDecimal);
            } else if (object instanceof Currency) {
                Currency currency = (Currency) object;
                statement.setString(index, currency.getCurrencyCode());
            } else if (object instanceof Double) {
                statement.setDouble(index, (Double) object);
            } else if (object instanceof Integer) {
                statement.setInt(index, (Integer) object);
            } else {
                //if all else fails. Should handle strings, ints, doubles, floats,
                System.out.println("Resolving as " + object.getClass());
                statement.setObject(index, object);
            }
        }

    }

    public List<List<Object>> debugLoad() {
        List<List<Object>> lineItems = Lists.newArrayList();
        try {
            String selectQuery = "SELECT * FROM " + getTableName();
            lineItems.addAll(doSelect(selectQuery, new ArrayList<>(), new ResultHandler<List<Object>>() {

                @Override
                public List<Object> handle(ResultSet rs) throws SQLException {
                    List<Object> stuff = Lists.newArrayList();
                    int columnCount = rs.getMetaData().getColumnCount();
                    //columns are not zero-indexed, but 1-indexed.
                    for (int i = 1; i <= columnCount; i++) {
                        stuff.add(rs.getObject(i));
                    }
                    return stuff;
                }
            }));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lineItems;
    }

    protected abstract String createTable();

    public boolean writeTable() {
        try {
            Statement stmt = getDatabaseConnection().createStatement();
            String table = createTable();
            stmt.execute(table);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean checkTableExists() throws SQLException {
        return checkTableExists(getTableName());
    }

    public Boolean checkTableExists(String table) throws SQLException {
        table = table.toUpperCase();
        String command = String.format("Select * From INFORMATION_SCHEMA.SYSTEM_TABLES Where TABLE_NAME = '%s'",
                table);

        List<Boolean> results = doSelect(command, Lists.newArrayList(), new ResultHandler<Boolean>() {
            @Override
            public Boolean handle(ResultSet rs) throws SQLException {
                return Boolean.TRUE;
            }
        });
        return !results.isEmpty() && results.get(0);
    }
}
