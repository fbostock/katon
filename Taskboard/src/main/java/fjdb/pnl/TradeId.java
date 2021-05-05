package fjdb.pnl;

import fjdb.databases.DataId;

/**
 * Created by francisbostock on 29/10/2017.
 */
public class TradeId extends DataId {

    public static final TradeId NULL = new TradeId(-1);

    private final int id;

    public TradeId(int id) {
        super(id);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeId tradeId = (TradeId) o;
        return id == tradeId.id;
    }

}
