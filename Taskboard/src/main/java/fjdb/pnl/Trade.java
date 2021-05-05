package fjdb.pnl;

import fjdb.databases.DataItemIF;

import java.time.LocalDate;
import java.util.Currency;

/**
 * Created by francisbostock on 30/09/2017.
 */
public class Trade implements DataItemIF {

    private final TradeId id;
    private final TradeType type;
    private final String instrument;
    private final LocalDate tradeDate;
    private final double quantity;
    private final double price;
    private final Currency currency;
    private final double fixing;

    public Trade(TradeType type, String instrument, LocalDate tradeDate, double quantity, double price, Currency currency, double fixing) {
        this(TradeId.NULL, type, instrument, tradeDate, quantity, price, currency, fixing);
    }

    public Trade(TradeId id, TradeType type, String instrument, LocalDate tradeDate, double quantity, double price, Currency currency, double fixing) {
        this.id = id;
        this.type = type;
        this.instrument = instrument;
        this.tradeDate = tradeDate;
        this.quantity = quantity;
        this.price = price;
        this.currency = currency;
        this.fixing = fixing;
    }

    public TradeId getId() {
        return id;
    }

    public TradeType getType() {
        return type;
    }

    public String getInstrument() {
        return instrument;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getFixing() {
        return fixing;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s", instrument, tradeDate, quantity, price, currency, fixing);
    }

    public LocalDate minimumExitDate() {
        return type.equals(TradeType.EQUITY) ? tradeDate.plusMonths(1) : tradeDate;
    }
}
