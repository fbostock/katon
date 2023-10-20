package fjdb.investments.backtests;

import com.google.common.collect.Lists;
import fjdb.calendar.WeekendHoliday;
import fjdb.investments.*;
import fjdb.investments.backtests.models.*;
import fjdb.series.Series;
import fjdb.series.TimeSeries;
import fjdb.series.TimeSeriesMapBuilder;
import fjdb.util.DateTimeUtil;
import fjdb.util.ListUtil;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

public class BackTester {

    public static WeekendHoliday weekendHoliday = WeekendHoliday.WEEKEND;
//        private static final List<Ticker> tickers = Lists.newArrayList(Tickers.DowJonesIndustrialAverage_iShares, Tickers.NASDAQ, Tickers.SNP_500);
//    private static final List<Ticker> tickers = Lists.newArrayList(Tickers.NASDAQ);
    private static final List<Ticker> tickers = Lists.newArrayList(Tickers.Index_ETFs);

    public static void main(String[] args) {
//        int days = 10;
//        System.out.println("Using mean from last " + days + " days");
//        getTargetValuesForMeanReversionOfGivenDays(days);
//        System.out.println("Using max from last " + days + " days");
//        getTargetValuesForMaxReversionOfGivenDays(days);

        LocalDate start = DateTimeUtil.date(20180102);
//        LocalDate end = DateTimeUtil.date(20230926);
        LocalDate end = DateTimeUtil.date(20231013);

        for (Ticker ticker : tickers) {
//            Portfolio portfolio = practiceStrategy(ticker.getName(), start, end);

        }

        testStrategies(Tickers.NASDAQ, start, end);
    }

    /*
    Model testing Based on Nasdaq - for regional max model:
    Single clip performs better than double clip.
    on/offs 0.02/0.02 generally better for all day windows.

    Next: try to modify putOn/takeOff conditions, based on trends
      a) to put on, price has to be more than previous day e.g. starting to go back up.
      b) to take off, price has to be less than previous day - we've go tto the peak and we're going back down.


     */

    public static void testStrategies(Ticker ticker, LocalDate start, LocalDate end) {
        double initialAmount = 1000.0; //£1000.0
        List<ModelMaker> models = RegionalMaxModel.models;
        FinancialDataSource financialDataSource = FinancialDataSource.MAIN_DATASOURCE;
        for (ModelMaker modelMaker : models) {

            Model model = modelMaker.makeModel(ticker);
            Portfolio portfolio = Models.runModel(ticker.getName(), model, financialDataSource.getPriceSeries(ticker), model.getStart(start), end, initialAmount);

            System.out.println(model);
            System.out.println(portfolio.printAnalysis(initialAmount, start, end, false));
            for (MutableTrade tradeOn : portfolio.getLiveTrades()) {
                Trade trade = tradeOn.make();
                LocalDate tradeDate = trade.getTradeDate();
                Double pnl = trade.getPnl();
                Double startPrice = tradeOn.getStartPrice();
                Double lastPrice = tradeOn.getLastPrice();
                System.out.printf("Trade still on value %.2f traded on %s (FirstPrice %.2f LastPrice %.2f) \n", pnl, tradeDate, startPrice, lastPrice);
            }
            if (portfolio.getLiveTrades().isEmpty()) {
                Trade lastTrade = ListUtil.last(portfolio.getTrades());
                TimeSeries<Double> pnlSeries = lastTrade.getPnlSeries();
                System.out.printf("No trades still on. Last trade earned %.2f on %s\n", pnlSeries.last(), pnlSeries.lastKey());
            }
            System.out.println();
        }


    }

    public static void longShortEquityTest(Ticker longStock, Ticker shortStock) {
        //get price series for each

        //buy £1000 of longStock, and short £1000 of shortStock:
           // get first price. 1000/firstPrice = amountLong.
           // get first price. 1000/firstPrice = amountShort.

        //iterate over timeseries for each
        //pnl = amountLong*price1 - amountShort*price2

        //print timeseries.

        //furtherWork:
        //assume market is a corresponding ETF for the stocks, eg FTSE, S&P
        //do linear regression between pnl series and S%P. Get gradient, which is our beta.

        //now do a beta hedge - shorting the market by the amount beta. (if beta is 0.3, when the market goes up £10, my portfolio goes up £3,
        //But if I buy and sell the same amount of stock, my cost is zero - how much do I hedge?
        //Try £300. I dunno though.





    }

    public static Portfolio practiceStrategy(String ticker, LocalDate start, LocalDate end) {
        double initialAmount = 1000.0; //£1000.0
        TimeSeries<Double> priceSeries = YahooDataLoader.load(ticker);

        ModelBase model = new RegionalMaxModel(ticker, new FinancialDataSource(), 30);
        model.setParameter(RegionalMaxModel.FRACTION_ON, 0.02);
        model.setParameter(RegionalMaxModel.FRACTION_OFF, 0.02);
        model.getParams().setClips(2);
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
        if (portfolio.getLiveTrades().isEmpty()) {
            Trade lastTrade = ListUtil.last(portfolio.getTrades());
            TimeSeries<Double> pnlSeries = lastTrade.getPnlSeries();
            System.out.printf("No trades still on. Last trade earned %.2f on %s", pnlSeries.last(), pnlSeries.lastKey());
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
