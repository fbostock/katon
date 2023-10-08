package fjdb.investments.database;

import com.google.common.collect.Lists;
import fjdb.databases.DefaultId;
import fjdb.hometodo.DecoratedColumnDao;
import fjdb.series.SeriesBuilder;
import fjdb.series.TimeSeries;

import java.time.LocalDate;
import java.util.List;

public class TradeDataDao {

    public TimeSeries<Double> getPrices(String ticker) {

        List<LocalDate> dates = Lists.newArrayList();
        List<Double> prices = Lists.newArrayList();



        return SeriesBuilder.makeTimeSeries(dates, prices);
    }


}
