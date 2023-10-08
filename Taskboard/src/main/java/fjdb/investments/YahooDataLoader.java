package fjdb.investments;

import com.google.common.collect.Lists;
import fjdb.calendar.WeekendHoliday;
import fjdb.series.SeriesBuilder;
import fjdb.series.TimeSeries;
import fjdb.series.TimeSeriesMapBuilder;
import fjdb.util.DateTimeUtil;
import fjdb.util.Pool;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class YahooDataLoader {

    public static DateCache dateCache = new DateCache();
    private static final LocalDate s_startDate = DateTimeUtil.date(20180102);

    static List<String> s_tickers = Lists.newArrayList("VMID.L", "VUKE.L", "VUSA.L", "VGER.L", "CNX1.L", "CIND.L", "CACX.L", "XDJP.L");

    public static void main(String[] args) {
        updateDataScript(Tickers.getAll());
    }

    /**
     * Fetches data between from and to dates, then merges with any existing data. Data fetched will overwrite any existing data.
     */
    private static TimeSeries<Double> fetchAndMergeData(String ticker, LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            System.out.printf("Full data for %s up to %s. Nothing to fetch\n", ticker, from);
            return null;
        }
        TimeSeries<Double> priceSeries = fetchData2(ticker, from, to);
        if (priceSeries != null) {
            writeAndMergeSeries(ticker, priceSeries);
            System.out.printf("Completed fetch for %s from %s to %s%n", ticker, from, to);
        }
        return priceSeries;
    }

    private static TimeSeries<Double> fetchData2(String ticker, LocalDate from, LocalDate to) {
        System.out.printf("Starting fetch for %s from %s to %s%n", ticker, from, to);
        String fromDate = dateInTime(from);
        //add one day to end, to ensure its at end of day when converted to epoch seconds.
        String toDate = dateInTime(to.plusDays(1));
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
                String dateStr = next.get("Date");
                String closePrice = next.get("Close");
                try {
                    double price = Double.parseDouble(closePrice);
                    dates.add(dateCache.date(DateTimeUtil.date(dateStr, DateTimeUtil.DASHED_YYYYMMDD)));
                    prices.add(price);
                } catch (NumberFormatException e) {
                    System.out.printf("Failed to parse price on %s: %s%n", dateStr, closePrice);
                }
            }
            return SeriesBuilder.makeTimeSeries(dates, prices);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void fetchData(Ticker ticker) {
        fetchData(ticker, s_startDate, WeekendHoliday.WEEKEND.previous(DateTimeUtil.today()));
    }

    /**
     * Fetched and save/write data between the given dates. Can be used to prepend, append, or overwrite existing data.
     */
    public static void fetchData(Ticker ticker, LocalDate startDate, LocalDate endDate) {
        fetchAndMergeData(ticker.getName(), startDate, endDate);
    }

    private static void updateDataScript(List<Ticker> tickers) {
        //iterate through all tickers. For each ticker:
        for (Ticker ticker : tickers) {
            TimeSeries<Double> load = load(ticker.getName());
            LocalDate from = WeekendHoliday.WEEKEND.next(load.lastKey());
            fetchAndMergeData(ticker.getName(), from, WeekendHoliday.WEEKEND.previous(DateTimeUtil.today()));
        }
    }

    private static final File data_directory = new File("/Users/francisbostock/TradeData/");

    /**
     * For the given ticker, writes the timeeries to file, merging with any existing data. If there is any overlap, the
     * input series will overwrite.
     */
    private static void writeAndMergeSeries(String ticker, TimeSeries<Double> doubleTimeSeries) {
        TimeSeries<Double> priceSeries;
        TimeSeries<Double> load = load(ticker);
        if (load.isEmpty()) {
            priceSeries = doubleTimeSeries;
        } else {
            TimeSeriesMapBuilder<Double> builder = new TimeSeriesMapBuilder<>(Double.class);
            for (LocalDate key : load.getKeys()) {
                builder.put(key, load.get(key));
            }
            for (LocalDate key : doubleTimeSeries.getKeys()) {
                builder.put(key, doubleTimeSeries.get(key));
            }
            priceSeries = builder.make();
        }
        try (FileWriter writer = new FileWriter(makeFile(ticker))) {
            writer.write("Date,Close" + System.lineSeparator());
            List<LocalDate> keys = priceSeries.getKeys();
            for (LocalDate key : keys) {
                writer.write(String.format("%s,%s%s", key, priceSeries.get(key), System.lineSeparator()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static File makeFile(String ticker) {
        return new File(data_directory, ticker + ".csv");
    }

    public static TimeSeries<Double> load(String ticker) {
        File file = makeFile(ticker);
        if (file.exists()) {
            CSVParser parser = null;
            try {
                parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<LocalDate> dates = Lists.newArrayList();
            List<Double> prices = Lists.newArrayList();

            for (CSVRecord next : parser) {
                String date = next.get("Date");
                String closePrice = next.get("Close");
                dates.add(dateCache.date(DateTimeUtil.date(date, DateTimeUtil.DASHED_YYYYMMDD)));
                prices.add(Double.parseDouble(closePrice));
            }
            return SeriesBuilder.makeTimeSeries(dates, prices);
        } else {
            return SeriesBuilder.makeEmpty();
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

