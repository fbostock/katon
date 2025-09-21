package fjdb.investments;

import fjdb.calendar.WeekendHoliday;
import fjdb.investments.tickers.Ticker;
import fjdb.series.Series;
import fjdb.series.TimeSeries;
import fjdb.series.TimeSeriesMapBuilder;

import java.time.LocalDate;

public class FinancialDataSource {

    public static FinancialDataSource MAIN_DATASOURCE = new FinancialDataSource();

    public static FinancialDataSource FILLED_DATASOURCE = new FinancialDataSource() {
        @Override
        public TimeSeries<Double> getPriceSeries(String ticker) {
            TimeSeries<Double> priceSeries = super.getPriceSeries(ticker);

            return FinancialDataSource.forwardFillSeries(priceSeries);
        }
    };

    public FinancialDataSource() {

    }
    //TODO cache the data.
    public TimeSeries<Double> getPriceSeries(Ticker ticker) {
        return getPriceSeries(ticker.getName());
    }

    public TimeSeries<Double> getPriceSeries(String ticker) {
        return YahooDataLoader.load(ticker);
    }

    private static TimeSeries<Double> forwardFillSeries(TimeSeries<Double> input) {
        TimeSeriesMapBuilder<Double> builder = new TimeSeriesMapBuilder<>(Double.class);
        WeekendHoliday weekend = WeekendHoliday.WEEKEND;

        Series.SeriesIterator<LocalDate, Double> iterator = input.iterator();
        iterator.moveNext();
        LocalDate lastDate = iterator.currentKey();
        Double prevailingValue = iterator.curentValue();
        builder.put(lastDate, prevailingValue);


        while (iterator.moveNext()) {
            LocalDate date = iterator.currentKey();
            LocalDate next = weekend.next(lastDate);
            Double value = iterator.curentValue();
            while (next.isBefore(date)) {
                builder.put(next, prevailingValue);
                next = weekend.next(next);
            }
            prevailingValue = value;
            lastDate = date;
            builder.put(date, value);
        }


        return builder.make();
    }
}
