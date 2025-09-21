package fjdb.investments.yahoofinance;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import fjdb.series.SeriesBuilder;
import fjdb.series.TimeSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class StockPrice {

    public static void main(String[] args) throws Exception {
        File url = new File("/Users/francisbostock/IdeaProjects/katon/Taskboard/src/main/java/fjdb/investments/yahoofinance/yahoo.json");
        Path path1 = Paths.get(url.toURI());
//        Path path = Paths.get(url.toURI());
        String content = Files.readString(path1);

        TimeSeries<Double> stockData = createStockData(content);
        System.out.println(content);

        String yahooData = getYahooData("VMID.L");
        TimeSeries<Double> ftse250 = createStockData(yahooData);
        System.out.println(ftse250);
    }

    public static TimeSeries<Double> getSeries(String ticker) {
        try {
            return createStockData(getYahooData(ticker));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //range values can be "1d","5d","1mo","3mo","6mo","1y","2y","5y","ytd","max"
    private static String getYahooData(String symbol) throws Exception {
//        String symbol = "AAPL";
        String urlStr = "https://query1.finance.yahoo.com/v8/finance/chart/" +
                symbol + "?interval=1d&range=1mo";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static TimeSeries<Double> createStockData(String json) {
        Root root = new Gson().fromJson(json, Root.class);
        TimeSeries<Double> timeSeries = root.chart.result.get(0).getTimeSeries();
        return timeSeries;
    }

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    public class Adjclose{
        public ArrayList<Double> adjclose;
    }

    public class Chart{
        public ArrayList<Result> result;
        public Object error;
    }

    public class CurrentTradingPeriod{
        public Pre pre;
        public Regular regular;
        public Post post;
    }

    public class Indicators{
        public ArrayList<Quote> quote;
        public ArrayList<Adjclose> adjclose;
    }

    public class Meta{
        public String currency;
        public String symbol;
        public String exchangeName;
        public String fullExchangeName;
        public String instrumentType;
        public int firstTradeDate;
        public int regularMarketTime;
        public boolean hasPrePostMarketData;
        public int gmtoffset;
        public String timezone;
        public String exchangeTimezoneName;
        public double regularMarketPrice;
        public double fiftyTwoWeekHigh;
        public double fiftyTwoWeekLow;
        public double regularMarketDayHigh;
        public double regularMarketDayLow;
        public int regularMarketVolume;
        public String longName;
        public String shortName;
        public double chartPreviousClose;
        public int priceHint;
        public CurrentTradingPeriod currentTradingPeriod;
        public String dataGranularity;
        public String range;
        public ArrayList<String> validRanges;
    }

    public class Post{
        public String timezone;
        public int start;
        public int end;
        public int gmtoffset;
    }

    public class Pre{
        public String timezone;
        public int start;
        public int end;
        public int gmtoffset;
    }

    public class Quote{
        public ArrayList<Double> high;
        public ArrayList<Double> low;
        public ArrayList<Double> close;
        public ArrayList<Double> open;
        public ArrayList<Integer> volume;
    }

    public class Regular{
        public String timezone;
        public int start;
        public int end;
        public int gmtoffset;
    }

    public class Result{
        public Meta meta;
        public ArrayList<Integer> timestamp;
        public Indicators indicators;

        public TimeSeries<Double> getTimeSeries() {
            List<LocalDate> dates = Lists.newArrayList();
            List<Double> prices = Lists.newArrayList();

            for (Integer time : timestamp) {
                LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochSecond(time), ZoneOffset.UTC);
//                LocalDate localDate = LocalDate.ofEpochDay(time);
                dates.add(localDate);
            }

            ArrayList<Quote> quote = indicators.quote;
            for (Quote data : quote) {
                prices.addAll(data.close);
            }

            return SeriesBuilder.makeTimeSeries(dates, prices);

        }
    }

    public class Root{
        public Chart chart;
    }


}
