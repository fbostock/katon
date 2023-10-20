package fjdb.investments.backtests.models;

import com.google.common.collect.Lists;
import fjdb.calendar.WeekendHoliday;
import fjdb.investments.FinancialDataSource;
import fjdb.investments.SeriesMaths;
import fjdb.investments.backtests.MutableTrade;
import fjdb.series.Series;
import fjdb.util.ListUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionalMaxModel extends ModelBase {

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

    public static List<ModelMaker> models = Lists.newArrayList();
    public static ModelMaker ON_3_OFF_2_30_DAYS_SINGLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.03, 0.02, 30, 1));
    public static ModelMaker ON_2_OFF_2_30_DAYS_SINGLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.02, 0.02, 30, 1));
    public static ModelMaker ON_3_OFF_2_30_DAYS_DOUBLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.03, 0.02, 30, 2));
    public static ModelMaker ON_2_OFF_2_30_DAYS_DOUBLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.02, 0.02, 30, 2));
    public static ModelMaker ON_3_OFF_2_15_DAYS_SINGLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.03, 0.02, 15, 1));
    public static ModelMaker ON_2_OFF_2_15_DAYS_SINGLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.02, 0.02, 15, 1));
    public static ModelMaker ON_3_OFF_2_15_DAYS_DOUBLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.03, 0.02, 15, 2));
    public static ModelMaker ON_2_OFF_2_15_DAYS_DOUBLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.02, 0.02, 15, 2));
    public static ModelMaker ON_3_OFF_2_60_DAYS_SINGLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.03, 0.02, 60, 1));
    public static ModelMaker ON_2_OFF_2_60_DAYS_SINGLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.02, 0.02, 60, 1));
    public static ModelMaker ON_3_OFF_2_60_DAYS_DOUBLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.03, 0.02, 60, 2));
    public static ModelMaker ON_2_OFF_2_60_DAYS_DOUBLE = createModelMaker(ticker -> makeRegionalMaxModel(ticker.getName(), 0.02, 0.02, 60, 2));

    private static ModelMaker createModelMaker(ModelMaker modelMaker) {
        models.add(modelMaker);
        return modelMaker;
    }

    public static Model makeRegionalMaxModel(String ticker, double on, double off, int days, int clips) {
        FinancialDataSource financialDataSource = new FinancialDataSource();
        ModelBase model = new RegionalMaxModel(ticker, financialDataSource, days);
        model.setParameter(RegionalMaxModel.FRACTION_ON, on);
        model.setParameter(RegionalMaxModel.FRACTION_OFF, off);
        model.getParams().setClips(clips);
        return model;
    }

    public RegionalMaxModel(String ticker, FinancialDataSource financialDataSource, int days) {
        super(ticker, financialDataSource);
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
            boolean tradePutOn = priceSeries.get(date) < targetPrice && isTradeGood(date);

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
        if (i < 0) {
            i = -(i + 1);
            System.out.printf("Index on %s not found. Using effective index\n", date);
        }
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

    @Override
    public String toString() {
        return String.format("RegionalMax ON %s OFF %s DAYS %d Clips %d", params.getParameter(FRACTION_ON), params.getParameter(FRACTION_OFF), days, params.getClips());
    }
}
