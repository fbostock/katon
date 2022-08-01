package fjdb.investments;

import com.google.common.collect.Lists;
import fjdb.util.DateTimeUtil;
import fjdb.util.Pool;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.tools.ant.taskdefs.Local;

import java.io.FileReader;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

public class YahooDataLoader {

    public static DateCache dateCache = new DateCache();

    public static void main(String[] args) {
        ///Users/francisbostock/Downloads/VMID.L.csv
        String filepath = "/Users/francisbostock/Downloads/VMID.L.csv";

        List<LocalDate> dates = Lists.newArrayList();
        List<Double> prices = Lists.newArrayList();

        try {
            CSVParser parser = CSVParser.parse(new FileReader(filepath), CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord next : parser) {
                String date = next.get("Date");
                String closePrice = next.get("Close");
                dates.add(dateCache.date(DateTimeUtil.date(date, DateTimeUtil.DASHED_YYYYMMDD)));
                prices.add(Double.parseDouble(closePrice));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        TimeSeries<Double> priceSeries = SeriesBuilder.makeTimeSeries(dates, prices);
        System.out.println("Done");
    }


    public static class DateCache {
        private final Pool<LocalDate, LocalDate> pool = new Pool<>() {
            @Override
            public LocalDate create(LocalDate key) {
                return LocalDate.of(key.getYear(), key.getMonth(), key.getDayOfMonth());
            }
        };

        public LocalDate date(LocalDate date) {
            return pool.get(date);
        }
    }
}

