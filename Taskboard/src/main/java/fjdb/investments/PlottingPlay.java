package fjdb.investments;

import fjdb.investments.tickers.Tickers;
import fjdb.series.Series;
import fjdb.util.DateTimeUtil;
import javafx.application.Application;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PlottingPlay extends Application {

    private static Series<LocalDate, Double> load;

    public static void main(String[] args) {
//        load = YahooDataLoader.load(Tickers.iSharesGBPIndexLinked.getName());
        load = YahooDataLoader.load(Tickers.Microsalt.getName());
//        load = YahooDataLoader.load(Tickers.TekCapital.getName());
        load = load.end(DateTimeUtil.date(20220104));
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

//        Series<LocalDate, Double> pnlSeries = BackTester.longShortEquityTest(Tickers.NASDAQ, Tickers.FTSE_250,  FinancialDataSource.FILLED_DATASOURCE);


//        LineChartTool.createPlot(stage, pnlSeries);
        LineChartTool.createPlot(stage, load);
    }
}
