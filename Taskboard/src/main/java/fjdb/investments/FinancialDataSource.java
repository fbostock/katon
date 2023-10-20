package fjdb.investments;

import fjdb.series.TimeSeries;

public class FinancialDataSource {

    public static FinancialDataSource MAIN_DATASOURCE = new FinancialDataSource();

    //TODO cache the data.
    public TimeSeries<Double> getPriceSeries(Ticker ticker) {
        return getPriceSeries(ticker.getName());
    }

    public TimeSeries<Double> getPriceSeries(String ticker) {
        return YahooDataLoader.load(ticker);
    }

}
