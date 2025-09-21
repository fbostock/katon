package fjdb.investments.backtests;

import com.google.common.collect.Lists;
import fjdb.calendar.WeekendHoliday;
import fjdb.investments.*;
import fjdb.investments.backtests.models.*;
import fjdb.investments.tickers.Ticker;
import fjdb.investments.tickers.Tickers;
import fjdb.series.*;
import fjdb.util.DateTimeUtil;
import fjdb.util.ListUtil;

import java.time.LocalDate;
import java.util.*;

/**
 * General results, based on Nasdaq.
 *
 * Regional max model: clips1 better than clips2.
 * Fraction on/off values not affect much.
 *
 * Do we wait to put on (trough) and wait to take off (peak)? Best result with 0.03 on, 0.02 off is
 *      don't wait to put on. Otherwise takes pnl from around 25/28 % to 20%.
 *      Wait to take off to get to the peak. This increases pnl to 30%.
 */
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
//birthdays(45);
// if (true) return;

        LocalDate start = DateTimeUtil.date(20180102);
//        LocalDate end = DateTimeUtil.date(20230926);
//        LocalDate end = DateTimeUtil.date(20231219);
        LocalDate end = DateTimeUtil.date(20240115);

//        for (Ticker ticker : tickers) {
//            try {
//                Portfolio portfolio = practiseStrategy(ON_3_OFF_2_15_DAYS_SINGLE, ticker, start, end);
//            } catch (Exception ex) {
//                System.out.printf("Failed to run backtest on %s\n", ticker);
//                ex.printStackTrace();
//            }
//        }

//        testStrategies(Tickers.NASDAQ, start, end);
        testStrategies2(Tickers.NASDAQ, start, end);
//        testStrategies2(Tickers.SNP_500, start, end);
    }

    public static void birthdays(int n) {

        double prob = 1/365.0;
        //2/365 * 364/365 + 1/365
        //
        for (int i = 2; i <=n; i++) {
//            double newProb = (1 - prob) * i / 365.0 + prob;
//            System.out.printf("People %d Prob %.2f\n", i, newProb);
//            prob = newProb;

            double product = 1.0;
            for (int j = 1; j <i; j++) {
                product *= (365-j)/365.0;
            }
            double newProb = 1-product;
            System.out.printf("People %d Prob %.3f\n", i, newProb);
        }
    }

    /*
    Model testing Based on Nasdaq - for regional max model:
    Single clip performs better than double clip.
    on/offs 0.02/0.02 generally better for all day windows.

    Next: try to modify putOn/takeOff conditions, based on trends
      a) to put on, price has to be more than previous day e.g. starting to go back up.
      b) to take off, price has to be less than previous day - we've go tto the peak and we're going back down.


     */

    /*
    Runs a separate backtest of each models defined in RegionalMaxModel.models.
     */
    public static void testStrategies(Ticker ticker, LocalDate start, LocalDate end) {
        double initialAmount = 1000.0; //£1000.0
        List<ModelMaker> models = RegionalMaxModel.models;
//        FinancialDataSource financialDataSource = FinancialDataSource.MAIN_DATASOURCE;
        FinancialDataSource financialDataSource = FinancialDataSource.FILLED_DATASOURCE;
        for (ModelMaker modelMaker : models) {

            Model model = modelMaker.makeModel(ticker);
//            Portfolio portfolio = Models.runModel(ticker, model, financialDataSource.getPriceSeries(ticker), model.getStart(start), end, initialAmount);
            Portfolio portfolio = Models.runModel(ticker, Lists.newArrayList(model), Lists.newArrayList(financialDataSource.getPriceSeries(ticker)), model.getStart(start), end, initialAmount);

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

    /**
     * Runs a backtest on a composition of models, where each model shares an equal fraction of the capital
     * Models run independently.
     */
    public static void testStrategies2(Ticker ticker, LocalDate start, LocalDate end) {
        double initialAmount = 1000.0; //£1000.0
        List<ModelMaker> models = RegionalMaxModel.models;
//        FinancialDataSource financialDataSource = FinancialDataSource.MAIN_DATASOURCE;
        FinancialDataSource financialDataSource = FinancialDataSource.FILLED_DATASOURCE;

        Model model1 = RegionalMaxModel.ON_1_OFF_1_30_DAYS_DOUBLE.makeModel(ticker);
        Model model2 = RegionalMaxModel.ON_3_OFF_2_30_DAYS_SINGLE.makeModel(ticker);

//        Model model = modelMaker.makeModel(ticker);
//            Portfolio portfolio = Models.runModel(ticker, model, financialDataSource.getPriceSeries(ticker), model.getStart(start), end, initialAmount);
        ArrayList<Model> modelList = Lists.newArrayList(model1, model2);
//        ArrayList<Model> modelList = Lists.newArrayList(model1);
        Portfolio portfolio = Models.runModel(ticker, modelList, makeSingletonList(financialDataSource.getPriceSeries(ticker), modelList.size()), model1.getStart(start), end, initialAmount);

//            System.out.println(model);
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

    public static Series<LocalDate, Double> longShortEquityTest(Ticker longStock, Ticker shortStock, FinancialDataSource financialDataSource) {
        //get price series for each
        TimeSeries<Double> longSeries = financialDataSource.getPriceSeries(longStock);
        TimeSeries<Double> shortSeries = financialDataSource.getPriceSeries(shortStock);

        //buy £1000 of longStock, and short £1000 of shortStock:
        // get first price. 1000/firstPrice = amountLong.
        // get first price. 1000/firstPrice = amountShort.
        LocalDate start = longSeries.firstKey();
        if (shortSeries.firstKey().isAfter(start)) start = shortSeries.firstKey();

        Double amountLong = 1000.0 / longSeries.get(start);
        Double amountShort = 1000.0 / shortSeries.get(start);

        MapBuilder<LocalDate, Double> pnlBuilder = new MapBuilder<>(LocalDate.class, Double.class);

        Series.SeriesIterator<LocalDate, Double> longIterator = longSeries.iterator();
        Series.SeriesIterator<LocalDate, Double> shortIterator = shortSeries.iterator();
        while (longIterator.moveNext() && shortIterator.moveNext()) {
            LocalDate date1 = longIterator.currentKey();
            LocalDate date2 = shortIterator.currentKey();
            while(date1.isBefore(date2)) {
                System.out.printf("Skipping %s from long as before %s in short\n" , date1, date2);
                if (!longIterator.moveNext()) System.exit(1);
                date1 = longIterator.currentKey();

            }
            while(date2.isBefore(date1)) {
                if (!shortIterator.moveNext()) System.exit(1);
                date2 = shortIterator.currentKey();
                System.out.printf("Skipping %s from short as before %s in long\n", date2, date1);
            }

            pnlBuilder.put(longIterator.currentKey(), amountLong*longIterator.curentValue() - amountShort*shortIterator.curentValue());
        }

        return pnlBuilder.make();



        //iterate over timeseries for each
        //pnl = amountLong*price1 - amountShort*price2

        //print timeseries.

        //furtherWork:
        //assume market is a corresponding ETF for the stocks, eg FTSE, S&P
        //do linear regression between pnl series and S%P. Get gradient, which is our beta.

        //now do a beta hedge - shorting the market by the amount beta. (if beta is 0.3, when the market goes up £10, my portfolio goes up £3,
        //But if I buy and sell the same amount of stock, my cost is zero - how much do I hedge?
        //say my portfolio makes £7 a month constantly, then if we plot returns
        //y = alpha + bX, alpha is £3 per day. X is the market, b is the beta. X is returns per month then of the market.
        //so if we calc the returns of the market, which is per day, against the returns of the portfolio, we will have a straight line
        //whose gradient is the beta, and intercept is the alpha, the £3 a day. So in the plot, when the portfolio is £10, and say
        //the market is £20, we'll have a gradient which is £7/£20, so 0.35 is the beta. This would be based on Trading a
        //certain amount of market stock, say £100. If I short £100 stock, when market goes up 20, my portfolio goes up 7, but I lose 20 on my short.
        //So instead I short 0.35*£100 = £35. When market goes up 20 my portfolio 7, but my short goes down 0.35*20 = 7, so
        //my position is unaffected by market.


    }

    public static Portfolio practiseStrategy(ModelMaker modelMaker, Ticker ticker, LocalDate start, LocalDate end) {
        double initialAmount = 1000.0; //£1000.0
        FinancialDataSource financialDataSource = new FinancialDataSource();
        TimeSeries<Double> priceSeries = financialDataSource.getPriceSeries(ticker);

        Model model = modelMaker.makeModel(ticker);
//        ModelBase model = new RegionalMaxModel(ticker, new FinancialDataSource(), 15);
        model.setParameter(RegionalMaxModel.FRACTION_ON, 0.03);
        model.setParameter(RegionalMaxModel.FRACTION_OFF, 0.02);
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


    private static <T> List<T> makeSingletonList(T input, int size) {
        return new ArrayList<>() {
            @Override
            public int size() {
                return size;
            }

            @Override
            public T get(int index) {
                return input;
            }
        };


    }
}
