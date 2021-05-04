package fjdb.databases;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

class CurrencyColumn extends AbstractColumn<Currency, String> {

    protected CurrencyColumn(String dbName) {
        super(dbName);
    }

    @Override
    public Currency get(ResultSet rs, int index) throws SQLException {
        return Currency.getInstance(rs.getString(index));
    }

    @Override
    public String dbElement(Currency input) {
        return input.getCurrencyCode();
    }
}
