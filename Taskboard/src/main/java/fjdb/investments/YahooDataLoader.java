package fjdb.investments;

import com.google.common.collect.Lists;
import fjdb.series.SeriesBuilder;
import fjdb.series.TimeSeries;
import fjdb.util.DateTimeUtil;
import fjdb.util.Pool;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class YahooDataLoader {

    public static DateCache dateCache = new DateCache();

    public static void main(String[] args) {
        ///Users/francisbostock/Downloads/VMID.L.csv

        System.out.println(dateInTime(DateTimeUtil.date(20230509)));
        System.out.println(dateInTime(DateTimeUtil.date(20230505)));
        System.out.println(dateInTime(DateTimeUtil.date(20230504)));
        fetchData("VMID.L", DateTimeUtil.date(20230501), DateTimeUtil.date(202305010));
         if (true) return;

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


    private static void fetchData(String ticker, LocalDate from, LocalDate to) {
//https://query1.finance.yahoo.com/v7/finance/download/VMID.L?period1=1652115189&period2=1683651189&interval=1d&events=history&includeAdjustedClose=true
//https://query1.finance.yahoo.com/v7/finance/download/VMID.L?period1=1683244800&period2=1683590400&interval=1d&events=history&includeAdjustedClose=true
//https://query1.finance.yahoo.com/v7/finance/download/VMID.L?period1=1683158400&period2=1683158400&interval=1d&events=history&includeAdjustedClose=true
//https://query1.finance.yahoo.com/v7/finance/download/VMID.L?period1=1683072000&period2=1683158400&interval=1d&events=history&includeAdjustedClose=true
/*
1683072000 3rd may
1683158400 4th may
 */

        String fromDate = dateInTime(from);
        String toDate = dateInTime(to);
        try {
            String uri = String.format("https://query1.finance.yahoo.com/v7/finance/download/%s?period1=%s&period2=%s&interval=1d&events=history&includeAdjustedClose=true", ticker, fromDate, toDate);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    //                .headers("Content-Type", "text/plain;charset=UTF-8")
                    //                .POST(HttpRequest.BodyPublishers.fromFile(
                    //                        Paths.get("src/test/resources/sample.txt")))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            CSVParser parser = CSVParser.parse(response.body(), CSVFormat.DEFAULT.withFirstRecordAsHeader());
            List<LocalDate> dates = Lists.newArrayList();
            List<Double> prices = Lists.newArrayList();

            for (CSVRecord next : parser) {
                String date = next.get("Date");
                String closePrice = next.get("Close");
                dates.add(dateCache.date(DateTimeUtil.date(date, DateTimeUtil.DASHED_YYYYMMDD)));
                prices.add(Double.parseDouble(closePrice));
            }
            TimeSeries<Double> priceSeries = SeriesBuilder.makeTimeSeries(dates, prices);

            System.out.println("Done");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static String dateInTime(LocalDate date) {
        return String.valueOf(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
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

