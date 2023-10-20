package fjdb.investments.backtests.models;

import fjdb.investments.FinancialDataSource;
import fjdb.investments.SeriesMaths;
import fjdb.investments.backtests.MutableTrade;
import fjdb.series.Series;

import java.time.LocalDate;

/**
 * This Model calculates the mean over the last n days specified in the model for the given price series. It then assumes
 * a mean reversion around that mean, and when the price dips the fraction specified below that means, it trades. When a trade
 * on gets to the latest mean, it takes off, whether it has made money or not.
 */
public class MeanReversionModel extends ModelBase {

    private final double fraction;
    private final int days;

    public MeanReversionModel(String ticker, double fraction, FinancialDataSource financialDataSource, int days) {
        super(ticker, financialDataSource);
        this.fraction = fraction;
        this.days = days;
    }

    @Override
    public LocalDate getStart(LocalDate start) {
        LocalDate key = priceSeries.getKey(days);
        return key.isAfter(start) ? key : start;
    }

    @Override
    public Double calcTargetPrice(LocalDate date) {
        int i = priceSeries.indexOf(date);
        Series<LocalDate, Double> subsequence = priceSeries.subsequence(i - days, i - 1);
        Double mean = SeriesMaths.mean(subsequence);
        return (1 - fraction) * mean;
    }


    @Override
    public boolean takeOff(MutableTrade trade, LocalDate date) {
        int i = priceSeries.indexOf(date);
        Series<LocalDate, Double> subsequence = priceSeries.subsequence(i - days, i - 1);
        Double mean = SeriesMaths.mean(subsequence);
        return priceSeries.get(date) > mean;
    }

    @Override
    public String description() {
        return String.format("MeanReversion: Trades when price is %s%% less than mean from last %s days", fraction, days);
    }
}
