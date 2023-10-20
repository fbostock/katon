package fjdb.investments.backtests.models;

import com.google.common.collect.Lists;
import fjdb.investments.SeriesMaths;
import fjdb.investments.backtests.MutableTrade;
import fjdb.investments.backtests.Portfolio;
import fjdb.investments.backtests.Trade;
import fjdb.series.Series;
import fjdb.series.TimeSeries;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Models {

    /*
    TODO
    can we define a value measure, to help define a value for a trade to know when to buy?
    e.g. taking the difference between the peak and current price (as potential for market to reach), but somehow
    weighting against time since that peak?
     Perhaps defining such a measure is pointless dealing with the index ETFs, since it won't help with a market fall?
    Is there anyway we can create a model which protects us/mitigates the effect of market falls?
      - maintain reserve cash: only invest half, and then somehow decide when the market slump is at the bottom
        (ok, so we have data on market slumps: is there any pattern we can take to know when to "safely" start trading again?)





     */


    public static Model makeRegionalMaxModel(String ticker) {
        return RegionalMaxModel.makeRegionalMaxModel(ticker, 0.03, 0.025, 30, 1);
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
