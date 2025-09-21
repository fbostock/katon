package fjdb.investments.backtests;

import fjdb.investments.tickers.Ticker;
import fjdb.series.TimeSeries;

import java.time.LocalDate;

public class Trade {
    Ticker ticker;
    LocalDate tradeDate;
    LocalDate endDate;
    Double pnl;
    TimeSeries<Double> pnlSeries;

    public Trade(Ticker ticker, LocalDate tradeDate, LocalDate endDate, Double pnl, TimeSeries<Double> pnlSeries) {
        this.ticker = ticker;
        this.tradeDate = tradeDate;
        this.endDate = endDate;
        this.pnl = pnl;
        this.pnlSeries = pnlSeries;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Double getPnl() {
        return pnl;
    }

    public TimeSeries<Double> getPnlSeries() {
        return pnlSeries;
    }
}
