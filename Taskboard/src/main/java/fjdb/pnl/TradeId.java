package fjdb.pnl;

import fjdb.databases.Id;

/**
 * Created by francisbostock on 29/10/2017.
 */
public class TradeId extends Id {

    public static final TradeId NULL  = new TradeId(-1);

    private final int id;

    public TradeId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeId tradeId = (TradeId) o;
        return id == tradeId.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
