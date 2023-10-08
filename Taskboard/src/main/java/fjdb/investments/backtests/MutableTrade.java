package fjdb.investments.backtests;

import fjdb.series.TimeSeries;
import fjdb.series.TimeSeriesMapBuilder;

import java.time.LocalDate;

public class MutableTrade {

    private final Double startPrice;
    private Double initialTradeAmount;
    String ticker;
    TimeSeriesMapBuilder<Double> pnlBuilder = new TimeSeriesMapBuilder<>(Double.class);
    private Double lastPrice;
    private LocalDate startDate;

    public MutableTrade(String ticker, Double startPrice, Double initialTradeAmount, LocalDate startDate) {
        this.ticker = ticker;
        this.startPrice = startPrice;
        this.initialTradeAmount = initialTradeAmount;
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void put(LocalDate date, Double value) {
        pnlBuilder.put(date, value-initialTradeAmount);
    }

    public Trade make() {
        TimeSeries<Double> pnlSeries = pnlBuilder.make();
        if (pnlSeries.isNotEmpty()) {
            return new Trade(ticker, pnlSeries.firstKey(), pnlSeries.lastKey(), pnlSeries.last(), pnlSeries);
        } else {
            return new Trade(ticker, null, null, 0.0, pnlSeries);
        }
    }

    public void lastPrice(Double price) {
        lastPrice = price;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public Double getLastPrice() {
        return lastPrice;
    }
}
