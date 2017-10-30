package fjdb.pnl;

/**
 * Created by francisbostock on 29/10/2017.
 */
public class TradeId {

    public static final TradeId NULL  = new TradeId(-1);

    private final int id;

    public TradeId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
