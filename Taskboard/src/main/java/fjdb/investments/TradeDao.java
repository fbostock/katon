package fjdb.investments;

import com.google.common.collect.Lists;
import fjdb.databases.*;
import fjdb.databases.columns.*;
import fjdb.pnl.Trade;
import fjdb.pnl.TradeId;
import fjdb.pnl.TradeType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by francisbostock on 01/10/2017.
 */
public class TradeDao extends IdColumnDao<Trade, TradeId> implements DaoIF<Trade> {
//    private final Columns1 columns;

    /*

    Note: opening balance was £51,210,66
    setup create method to actually add the trade:
     - dao needs to convert the trade fields into strings.
     - need column object which define the fields in the database, and how to convert the field in the object to the
     value to store in the database, and back again.
       Ideally, a Currency column would be usable for many tables/objects, trades being just one. But how do the currency
       get extract from trade and passed to the column?

TODO Calculate minimum exit date for trades, e.g. in Trade object calculate it for one month hence if equity

TODO add a memory/performance monitor to show current mem usage in jvm, and other properties, to get a feel for how
programs' performance change over time with changes in engines etc. If a new change results in sudden increase in mem,
 may want to address that.


 TODO check how to draw custom nodes using GraphicsContext

//TODO make a quandl fetcher


     */

    public static void main(String[] args) throws SQLException {
        TradeDao tradeDao = new TradeDao();
//        dao.setup();
//        DatabaseConnection.setupDatabase(Lists.newArrayList(dao));
//        dao.create(new Trade(TradeType.ETF, "VSUD", LocalDate.of(2017, 9, 25), 550, 47.437, Currency.getInstance("USD"), 1.12));
//        dao.create(new Trade(TradeType.ETF, "VMID", LocalDate.of(2017, 10, 2), 330, 31.9152, Currency.getInstance("GBP"), 1.0));
//        dao.create(new Trade(TradeType.EQUITY, "CNA", LocalDate.of(2017, 10, 11), 3000, 179.4717, Currency.getInstance("GBP"), 1.0));
//        dao.create(new Trade(TradeType.EQUITY, "CLLN", LocalDate.of(2017, 10, 26), 6500, 0.4560, Currency.getInstance("GBP"), 1.0));
//        dao.create(new Trade(TradeType.EQUITY, "MERL", LocalDate.of(2017, 10, 30), 550, 3.7342, Currency.getInstance("GBP"), 1.0));
//        dao.create(new Trade(TradeType.EQUITY, "TEST", LocalDate.of(2017, 11, 12), 550, 3.7342, Currency.getInstance("GBP"), 1.0));
//        dao.create(new Trade(TradeType.EQUITY, "CITY", LocalDate.of(2018, 1, 15), 8400, 59.6, Currency.getInstance("GBP"), 1.0));
//        dao.create(new Trade(TradeType.EQUITY, "BMK", LocalDate.of(2018, 1, 16), 8000, 75.15, Currency.getInstance("GBP"), 1.0));

//        DateTimeFormatter.ofPattern("yyyyMMdd").
//dao.setup();

        List<Trade> trades = tradeDao.load();
        for (Trade trade : trades) {
            System.out.println(trade);
        }
        //tradeDao.getDatabaseConnection().shutdown();
    }

    /*
    The Dao will have an interface including a register/setup method which will be used by the machinery to initially set
    up all the databases.
     */


    public TradeDao() {
        super(DatabaseAccess.TRADE_ACCESS, Columns.of());
        createTable();
//        columns = Columns1.of();
        try {
            if (!checkTableExists()) {
                writeTable();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*
    cash bean, cash id, map of id to beans, beans do not contain id.
    inserting bean, inserts entry in database, automatically inserting id. Loading from database, selecting all from
    db, add to maps before returning.
     */

    public List<Trade> load() {
        return super.load();
    }

    public String getTableName() {
        return "TRADES";
    }

//    public void setup() {
//        try {
//            Statement stmt = DatabaseConnection.getInstance().createStatement();
//            stmt.execute(createDB());
//            stmt.close();
//        } catch (Exception e) {
//            System.out.println("Exception: " + e);
//            e.printStackTrace();
//        }
//    }

//    private String getColumnLabels() {
//        return columns.getColumnLabels();
////        return "INSTRUMENT, TRADE_DATE, QUANTITY, PRICE, CURRENCY, FIXING";
//    }


    /*
    perhaps we can register columns with a class to store the object type so we can construct the trader from a map of columns class references


    similar to row columns, we could supply an extractor which extracts an object of type Clazz from an object associated
    with the row definition. Then, for writing to the database, we have a set of classes which we register for a particular
    class type, that will handle the reading and writing for that class type.
    register(Class<T> clazz, RWclass rwClazz)
    where RWClass had a read method and write method
    T read(ResultSet rs, index) {//construct T from rs.getXXX(index)//}
    void write(T obj) {//extract from T such that the result feeds into the read method to generate T}

    So for a Currency Column, we could register a RWClass as above to create the Currency from a 3 char string, then
    we define a Currency Column object with an extractor from Trade: trade.getCurrency(), with the Column object having
    various other properties such as visible, editable etc. for table displays.

     */

    private static class Columns extends IdColumnGroup<Trade, TradeId> {

        private final IdColumn<TradeId> idColumn;
        private final StringColumn instrumentColumn = new StringColumn("INSTRUMENT", "VARCHAR(256)");
        private final CurrencyColumn currencyColumn = new CurrencyColumn("CURRENCY");
        private final DateColumn tradeDateColumn = new DateColumn("TRADE_DATE");
        private final DoubleColumn quantityColumn = new DoubleColumn("QUANTITY");
        private final DoubleColumn priceColumn = new DoubleColumn("PRICE");
        private final DoubleColumn fixingColumn = new DoubleColumn("FIXING");
        private final TypeColumn<TradeType> tradetype = new TypeColumn<>(TradeType.class, "TRADETYPE", "VARCHAR(32)");

        public static Columns of() {
            IdColumn<TradeId> idColumn = new IdColumn<>("ID", TradeId::new, TradeId.class);
            return new Columns(idColumn);
        }

        public Columns(IdColumn<TradeId> idColumn) {
            super(idColumn);
            this.idColumn = idColumn;
            addColumn(tradetype).addColumn(instrumentColumn).addColumn(tradeDateColumn).addColumn(quantityColumn).addColumn(priceColumn);
            addColumn(currencyColumn).addColumn(fixingColumn);
        }

        @Override
        public Trade handle(ResultSet rs) throws SQLException {
            return new Trade(handleId(rs), resolve(tradetype, rs), resolve(instrumentColumn, rs), resolve(tradeDateColumn, rs),
                    resolve(quantityColumn, rs), resolve(priceColumn, rs), resolve(currencyColumn, rs), resolve(fixingColumn, rs));
        }

        @Override
        public TradeId handleId(ResultSet rs) throws SQLException {
            return resolve(idColumn, rs);
        }

        //TODO if we can specify the dbElement calls as lambdas when constructing the Columns, we can do away with this, or rather
        //delegate to a method on the columns, thus allowing flexibility.
        @Override
        public List<Object> getDataItemObjects(Trade dataItem) {
            return Lists.newArrayList(instrumentColumn.dbElement(dataItem.getInstrument()), currencyColumn.dbElement(dataItem.getCurrency()),
                    tradeDateColumn.dbElement(dataItem.getTradeDate()), quantityColumn.dbElement(dataItem.getQuantity()),
                    priceColumn.dbElement(dataItem.getPrice()), fixingColumn.dbElement(dataItem.getFixing()), tradetype.dbElement(dataItem.getType()));
        }
    }

}
