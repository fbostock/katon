package fjdb.databases;

import fjdb.pnl.TradeId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TradeIdColumn extends IdColumn<TradeId> {

    public TradeIdColumn(String dbName) {
        super(dbName, TradeId::new);
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
