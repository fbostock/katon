package fjdb.investments.backtests;

import fjdb.series.TimeSeries;

import java.time.LocalDate;

public class Trade {
    String ticker;
    LocalDate tradeDate;
    LocalDate endDate;
    Double pnl;
    TimeSeries<Double> pnlSeries;

    public Trade(String ticker, LocalDate tradeDate, LocalDate endDate, Double pnl, TimeSeries<Double> pnlSeries) {
        this.ticker = ticker;
        this.tradeDate = tradeDate;
        this.endDate = endDate;
        this.pnl = pnl;
        this.pnlSeries = pnlSeries;
    }

    public String getTicker() {
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
