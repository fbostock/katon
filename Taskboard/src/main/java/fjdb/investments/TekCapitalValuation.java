package fjdb.investments;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import fjdb.investments.tickers.Ticker;
import fjdb.investments.tickers.Tickers;
import fjdb.investments.utils.Formatters;
import fjdb.util.DateTimeUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TekCapitalValuation {

    /*
    Portfolio:
    Belluscura - medical devices            - 6.4%
    GenIP - Generative AI analytic services - 63%
    Innovative eyewear - smart glasses      - 10%
    Microsalt - low sodium salt equivalent   - 69.6% ownership
    Guident - remote monitor for self-d cars- 70%  value $18.1m

    For each one, need number of shares, price per share to get valuation
    Belluscura BELL.L  401m shares 0.9cents   MC $4.5m
    GenIP      GNIP.L  17.52m   23p MC £5.7m
    Inno eye   LUCY    2.45m   $2.6 $8.4
    Microsalt  SALT.L  43.12m    ~60p  £31
    Guident    ----



    TekCapital: 216.9m shares ~7p => £15.2m
     */

    static int belluscuraShares = 401000000;
    static int genIPShares = 17520000;
    static int microsaltShares = 41120000;
    static int innovativeEyewearShares = 2450000;
    static int tekCapitalShares = 216900000;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {


        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI("http://localhost:8080/api/meals"))
                .uri(new URI("http://192.168.0.46:8080/api/meals"))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List list = new Gson().fromJson(response.body(), List.class);
        System.out.println(response);

        if(true) return;

        FinancialDataSource financialDataSource = new FinancialDataSource();
        List<Ticker> tekcapitalPortfolio = Lists.newArrayList(Tickers.TEKCAPITAL_PORTFOLIO);
        tekcapitalPortfolio.add(Tickers.TekCapital);
        Map<Ticker, Double> prices = new HashMap<>();
        for (Ticker ticker : tekcapitalPortfolio) {
            try {
                Double aDouble = financialDataSource.getPriceSeries(ticker).get(DateTimeUtil.previousWeekDay());
                prices.put(ticker, aDouble);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (Map.Entry<Ticker, Double> entry : prices.entrySet()) {
            System.out.printf("%s: %s%n", entry.getKey(), Formatters.currency£(entry.getValue()));
        }

        Map<Ticker, Double> shares = Map.of(Tickers.Belluscura, 0.064 * belluscuraShares,
                Tickers.InnovativeEyewear, 0.1 * innovativeEyewearShares,
                Tickers.Microsalt, 0.696* microsaltShares,
                Tickers.GenIP, 0.63*genIPShares,
                Tickers.TekCapital, tekCapitalShares*1.0);

        double totalValue = 0.0;
        totalValue += getValue(Tickers.Belluscura, shares, prices, 0.01 / 1.3);//priced in cents
        totalValue += getValue(Tickers.Microsalt, shares, prices, 0.01);//priced in pence
        totalValue += getValue(Tickers.GenIP, shares, prices, 0.01);//priced in pence
        totalValue += getValue(Tickers.InnovativeEyewear, shares, prices, 1.0 / 1.3);//priced in dollars

        double tekCapitalShareValue = getValue(Tickers.TekCapital, shares, prices, 0.01);
        System.out.printf("Total %s%nTekCapital Market Cap: %s\n", Formatters.currency£(totalValue), Formatters.currency£(tekCapitalShareValue));
    }

    private static double getValue(Ticker ticker, Map<Ticker, Double> shares, Map<Ticker, Double> prices, double conversionToPounds) {
        double value = shares.get(ticker) * prices.get(ticker) * conversionToPounds;
        System.out.printf("%s: %s%n", ticker, Formatters.currency£(value));
        return value;
    }

}
