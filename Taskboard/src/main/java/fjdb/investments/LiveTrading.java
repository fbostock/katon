package fjdb.investments;

import com.google.common.collect.Lists;
import fjdb.investments.backtests.models.Model;
import fjdb.investments.backtests.models.Models;
import fjdb.series.TimeSeries;
import fjdb.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class LiveTrading {

    public static void main(String[] args) {
        FinancialDataSource financialDataSource = new FinancialDataSource();

        List<String> results = Lists.newArrayList();


        for (Ticker ticker : Tickers.Index_ETFs) {


            Model model = Models.makeRegionalMaxModel(ticker.getName());

            Double targetPrice = model.calcTargetPrice(DateTimeUtil.today());

            TimeSeries<Double> priceSeries = financialDataSource.getPriceSeries(ticker);


            Double lastPrice = priceSeries.last();
            Double max = SeriesMaths.max(priceSeries.end(priceSeries.getSize() - 30));
            double percentageDown = 100 * (max - lastPrice) / max;
            if (model.doTrades(DateTimeUtil.previousWeekDay(), new ArrayList<>())) {
                System.out.println("TRADE " + ticker);
                results.add(String.format("Y Ticker: %s, target price %s last price %s MAX price %s Percent Down %.2f", ticker, targetPrice, lastPrice, max, percentageDown));
            } else {
                System.out.println("DONT TRADE " + ticker);
                results.add(String.format("N Ticker: %s, target price %s last price %s MAX price %s Percent Down %.2f", ticker, targetPrice, lastPrice, max, percentageDown));
            }
        }

        for (String result : results) {
            System.out.println(result);
        }
    }

}
