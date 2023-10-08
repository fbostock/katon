package fjdb.investments.backtests.models;

import com.google.common.collect.Lists;
import fjdb.calendar.WeekendHoliday;
import fjdb.investments.SeriesMaths;
import fjdb.investments.backtests.MutableTrade;
import fjdb.investments.backtests.Portfolio;
import fjdb.investments.backtests.Trade;
import fjdb.series.Series;
import fjdb.series.TimeSeries;
import fjdb.util.ListUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Models {

    public static abstract class ModelBase implements Model {
        protected final String ticker;
        protected final Series<LocalDate, Double> priceSeries;
        protected ModelParams params;

        public ModelBase(String ticker, Series<LocalDate, Double> priceSeries, ModelParams modelParams) {
            this.ticker = ticker;
            this.priceSeries = priceSeries;
            this.params = modelParams;
        }

        public ModelBase(String ticker, Series<LocalDate, Double> priceSeries) {
            this(ticker, priceSeries, new ModelParams());
        }

        @Override
        public abstract String description();

        @Override
        public boolean doTrades(LocalDate date, List<MutableTrade> currentTrades) {
            Double targetPrice = calcTargetPrice(date);
            return priceSeries.get(date) < targetPrice;
        }

        @Override
        public ModelParams getParams() {
            return params;
        }

        public <T> T getParameter(ModelParameter<T> parameter) {
            return params.getParameterOrDefault(parameter);
        }

        public <T> void setParameter(ModelParameter<T> parameter, T value) {
            params.setParameter(parameter, value);
        }
    }

    public static class MeanReversionModel extends ModelBase {

        private final double fraction;
        private final int days;

        public MeanReversionModel(String ticker, double fraction, Series<LocalDate, Double> priceSeries, int days) {
            super(ticker, priceSeries);
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

    public static class RegionalMaxModel extends ModelBase {

        public static final ModelParameter<Double> FRACTION_ON = new ModelParameter<>(0.02);
        public static final ModelParameter<Double> FRACTION_OFF = new ModelParameter<>(0.02);
        private final int days;
        Map<LocalDate, TradeParams> tradesInProgress = new ConcurrentHashMap<>();

        /*
        TODO ways to improve the model:
        - consider putting on 3% under max, but take off at 1% under max (to gain 2%)
        - For second/third etc. lots, we could apply the fraction from max in multiples i.e. the first trace is one frac below,
        the second is 2*frac, the third is 3*frac.
        - For second/third etc. lots, we apply constraint that the prices need to have stopped descending,
        - For the primary lot, we apply the constraint that the prices been to be increasing when it is below 2%.

         */
        public RegionalMaxModel(String ticker, Series<LocalDate, Double> priceSeries, int days) {
            super(ticker, priceSeries);
            this.days = days;
            params.setParameter(FRACTION_ON);
            params.setParameter(FRACTION_OFF);
        }


        @Override
        public LocalDate getStart(LocalDate start) {
            LocalDate key = priceSeries.getKey(days);
            return key.isAfter(start) ? key : start;
        }

        @Override
        public String description() {
            return String.format("RegionalMax: Trades when price is %s%% less than max value with last %s days\n " +
                    "and taken off with increase of %.2f%%. ", getParameter(FRACTION_ON), days, getParameter(FRACTION_OFF));
        }


        @Override
        public boolean doTrades(LocalDate date, List<MutableTrade> currentTrades) {
            if (currentTrades.isEmpty()) {
                Double targetPrice = calcTargetPrice(date);
                boolean tradePutOn =  priceSeries.get(date) < targetPrice && isTradeGood(date);

//                boolean tradePutOn = super.doTrades(date, currentTrades);
                //TODO if we have 1 trade on, the targetPrice should be 2*frac below the max from the previous trade, or one frac
                //below max if that max is less than price the other trade was put on.
                if (tradePutOn) {
                    Double pricePutOn = priceSeries.get(date);
                    tradesInProgress.put(date, new TradeParams(pricePutOn, (1 + getParameter(FRACTION_OFF)) * pricePutOn));
                }
                return tradePutOn;
            } else {
                //if the recent maximum price is more than the start price of a trade on, the target price must be multiples of the fraction
                //below the max, e.g. 4% rather than 2%.
                //Otherwise, if the recent max is below the start price of a trade on, we apply the normal constraint of
                //1*fraction below the max.
                Double recentMaximum = calcTargetPrice(date, 0.0);
                Double targetPrice;
                if (recentMaximum > 100.0 * ListUtil.last(currentTrades).getStartPrice()) {
                    targetPrice = calcTargetPrice(date, getParameter(FRACTION_ON) * currentTrades.size() + 1);
                } else {
                    targetPrice = calcTargetPrice(date);
                }
                boolean tradePutOn = priceSeries.get(date) < targetPrice && isTradeGood(date);
                if (tradePutOn) {
                    Double pricePutOn = priceSeries.get(date);
                    tradesInProgress.put(date, new TradeParams(pricePutOn, (1 + getParameter(FRACTION_OFF)) * pricePutOn));
                }
                return tradePutOn;
            }
        }

        private boolean isTradeGood(LocalDate date) {
            //check price, last price. If last price less than price (going back up) yes, we're good.
             if (true) return true;
            Double currentPrice = priceSeries.get(date);
            Double previousPrice = null;
            LocalDate prevDate = date;
            while (previousPrice == null) {
                prevDate = WeekendHoliday.WEEKEND.previous(prevDate);
                previousPrice = priceSeries.get(prevDate);
            }
            if (previousPrice > currentPrice) {
                System.out.printf("PREVENTING TRADE DUE TO FALLING KNIFE %s Current %.2f Previous %.2f\n", date, currentPrice, previousPrice);
            }
            return currentPrice > previousPrice;
        }

        private Double calcTargetPrice(LocalDate date, Double frac) {
            int i = priceSeries.indexOf(date);
            Series<LocalDate, Double> subsequence = priceSeries.subsequence(i - days, i - 1);
            Double max = SeriesMaths.max(subsequence);
            return (1 - frac) * max;
        }

        public Double calcTargetPrice(LocalDate date) {
            return calcTargetPrice(date, getParameter(FRACTION_ON));
        }


        public boolean takeOff(MutableTrade trade, LocalDate date) {
            TradeParams tradeParams = tradesInProgress.get(trade.getStartDate());
            if (priceSeries.get(date) > tradeParams.priceToTakeOff) {
                tradesInProgress.remove(date);
                return true;
            }
            return false;
        }

        private record TradeParams(double pricePutOn, double priceToTakeOff) {
        }
    }


    public static Portfolio runModel(String ticker, Model model, TimeSeries<Double> priceSeries, LocalDate start, LocalDate end, double initialAmount) {
        List<LocalDate> keys = priceSeries.subsequence(start, end).getKeys();

        int maxTrades = model.getParams().getClips();
        List<MutableTrade> tradesOn = new ArrayList<>();

        double initialAmountPerTrade = initialAmount / maxTrades; //£1000.0
        double contractSize = initialAmountPerTrade / ((priceSeries.first() / 100.0));
//        double contractSizes[] = new double[]{contractSize * 4.0/3.0, contractSize * 2.0/3.0};
        double contractSizes[] = new double[]{contractSize * 3.0 / 3.0, contractSize * 3.0 / 3.0};
        //        double firstContract = contractSize * 4.0/3.0;
//        double secondContract = contractSize * 2.0/3.0;

        List<Trade> completedTrades = Lists.newArrayList();
        Double initialTradeAmount = 0.0;
        for (LocalDate date : keys) {
            if (date.isBefore(end)) {
                Double price = priceSeries.get(date) / 100.0;

                int i = 0;
                Iterator<MutableTrade> iterator = tradesOn.iterator();
                while (iterator.hasNext()) {
                    MutableTrade mutableTrade = iterator.next();
                    mutableTrade.lastPrice(price);
//                    mutableTrade.put(date, contractSize * price);
                    mutableTrade.put(date, contractSizes[i++] * price);


                    if (model.takeOff(mutableTrade, date)) {
                        Trade make = mutableTrade.make();
                        completedTrades.add(make);
                        iterator.remove();
                        if (model.getParams().printTrading()) {
                            System.out.printf("Removing trade on %s (traded on %s), was on for %s days. Trades on %s\n", date, make.getTradeDate(), make.getPnlSeries().getSize(), tradesOn.size());
                        }
                    }
                }
                if (tradesOn.size() < maxTrades) {
                    if (model.doTrades(date, tradesOn)) {
//                        initialTradeAmount = contractSize * price;
                        initialTradeAmount = contractSizes[tradesOn.size()] * price;
//                        MutableTrade newTrade = new MutableTrade(ticker, price, initialTradeAmount, date);
                        MutableTrade newTrade = new MutableTrade(ticker, price, initialTradeAmount, date);
                        tradesOn.add(newTrade);
                        newTrade.put(date, initialTradeAmount);
                        if (model.getParams().printTrading()) {
                            System.out.printf("Placing trade on %s, %s trades on\n", date, tradesOn.size());
                        }
                    }
                }
            }
        }

        return new Portfolio(ticker, completedTrades, tradesOn);
    }
}
