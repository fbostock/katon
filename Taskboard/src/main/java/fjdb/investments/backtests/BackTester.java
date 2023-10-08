package fjdb.investments.backtests;

import com.google.common.collect.Lists;
import fjdb.calendar.WeekendHoliday;
import fjdb.investments.*;
import fjdb.investments.backtests.models.Models;
import fjdb.series.Series;
import fjdb.series.TimeSeries;
import fjdb.series.TimeSeriesMapBuilder;
import fjdb.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

public class BackTester {

    public static WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
    //    private static final List<Ticker> tickers = Lists.newArrayList(Tickers.DowJonesIndustrialAverage_iShares, Tickers.NASDAQ, Tickers.SNP_500);
    private static final List<Ticker> tickers = Lists.newArrayList(Tickers.NASDAQ);

    public static void main(String[] args) {
        int days = 10;
        System.out.println("Using mean from last " + days + " days");
        getTargetValuesForMeanReversionOfGivenDays(days);
        System.out.println("Using max from last " + days + " days");
        getTargetValuesForMaxReversionOfGivenDays(days);

        for (Ticker ticker : tickers) {
            Portfolio portfolio = practiceStrategy(ticker.getName());

        }
    }

    /*
    Model testing
    Based on Nasdaq - for regional max model, 0.02, 0.02, best strategy is:
    single clip at full amount
    no restriction on previous price being lower than current price.


     */


    public static Portfolio practiceStrategy(String ticker) {
        double initialAmount = 1000.0; //£1000.0
        LocalDate start = DateTimeUtil.date(20180102);
        LocalDate end = DateTimeUtil.date(20230926);
        TimeSeries<Double> priceSeries = YahooDataLoader.load(ticker);
//        MeanReversionModel model = new MeanReversionModel(ticker, 0.02, priceSeries, 10);
        Models.ModelBase model = new Models.RegionalMaxModel(ticker, priceSeries, 30);
        model.setParameter(Models.RegionalMaxModel.FRACTION_ON, 0.02);
        model.setParameter(Models.RegionalMaxModel.FRACTION_OFF, 0.02);
        model.getParams().setClips(1);
        System.out.println(model.description());
        Portfolio portfolio = Models.runModel(ticker, model, priceSeries, model.getStart(start), end, initialAmount);

        System.out.println(portfolio.printAnalysis(initialAmount, start, end, false));
        for (MutableTrade tradeOn : portfolio.getLiveTrades()) {
            Trade trade = tradeOn.make();
            LocalDate tradeDate = trade.getTradeDate();
            Double pnl = trade.getPnl();
            Double startPrice = tradeOn.getStartPrice();
            Double lastPrice = tradeOn.getLastPrice();
            System.out.printf("Trade still on value %.2f traded on %s (FirstPrice %.2f LastPrice %.2f) %n", pnl, tradeDate, startPrice, lastPrice);
        }
        return portfolio;
    }


    public static void getTargetValuesForMeanReversionOfGivenDays(int days) {
        double fraction = 0.02; //2%
        for (Ticker ticker : tickers) {

            TimeSeries<Double> priceSeries = YahooDataLoader.load(ticker.getName());
            int size = priceSeries.getSize();
            Series<LocalDate, Double> subsequence = priceSeries.subsequence(size - days, size);
            Double mean = SeriesMaths.mean(subsequence);
            Double targetPrice = (1 - fraction) * mean;
            Double lastPrice = priceSeries.last();

            boolean isAtTarget = lastPrice < targetPrice;
            System.out.printf("Ticker %s: Last Price %.2f, TARGET price %.2f SHOULD WE TRADE BASED ON EOD %s\n", ticker.getName(), lastPrice, targetPrice, isAtTarget);

        }

    }

    public static void getTargetValuesForMaxReversionOfGivenDays(int days) {
        double fraction = 0.02; //2%
        for (Ticker ticker : tickers) {

            TimeSeries<Double> priceSeries = YahooDataLoader.load(ticker.getName());
            int size = priceSeries.getSize();
            Series<LocalDate, Double> subsequence = priceSeries.subsequence(size - days, size);
            Double max = SeriesMaths.max(subsequence);
            Double targetPrice = (1 - fraction) * max;
            Double lastPrice = priceSeries.last();

            boolean isAtTarget = lastPrice < targetPrice;
            System.out.printf("Ticker %s: Last Price %.2f, TARGET price %.2f MAX Price %.2f SHOULD WE TRADE BASED ON EOD %s\n", ticker.getName(), lastPrice, targetPrice, max, isAtTarget);

        }

    }


    private static TimeSeries<Double> makeStraightLine(LocalDate from, LocalDate to, TimeSeries<Double> timeSeries) {

        long daysBetween = weekendHoliday.daysBetween(from, to);

        Iterator<LocalDate> goodDays = weekendHoliday.getGoodDays(from, true, to, true);
        int num = 0;
        TimeSeriesMapBuilder<Double> builder = new TimeSeriesMapBuilder<>(Double.class);

        double[] time = new double[(int) daysBetween + 1];
        double[] prices = new double[(int) daysBetween + 1];

        Series.SeriesIterator<LocalDate, Double> iterator = timeSeries.subsequence(from, weekendHoliday.next(to)).iterator();
        while (iterator.moveNext()) {
            time[num] = 1.0 * num;
            prices[num] = iterator.curentValue();
            num++;
        }

        Regression regression = new Regression(time, prices);
        double gradient = regression.getGradient();
        double intercept = regression.intercept();

        num = 0;
        while (goodDays.hasNext()) {
            double value = gradient * num + intercept;
            builder.put(goodDays.next(), value);
        }

        return builder.make();
    }

    private static class Regression {

        private final double[] time;
        private final double[] prices;
        LinearRegression regression;

        public Regression(double[] time, double[] prices) {
            this.time = time;
            this.prices = prices;
            regression = new LinearRegression(time, prices);
        }

        public double getGradient() {
            return regression.slope();
        }

        public double intercept() {
            return regression.intercept();
        }

    }


}
