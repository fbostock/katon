package fjdb.databases;

import fjdb.pnl.TradeId;

import java.sql.ResultSet;
import java.sql.SQLException;

class TradeIdColumn extends AbstractColumn<TradeId, Integer> {

    protected TradeIdColumn(String dbName) {
        super(dbName);
    }

    @Override
    public TradeId get(ResultSet rs, int index) throws SQLException {
        return new TradeId(rs.getInt(index));
    }

    @Override
    public Integer dbElement(TradeId input) {
        return input.getId();
    }
}
