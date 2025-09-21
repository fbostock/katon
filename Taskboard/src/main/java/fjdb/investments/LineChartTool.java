package fjdb.investments;

import fjdb.investments.tickers.Ticker;
import fjdb.investments.tickers.Tickers;
import fjdb.series.Series;
import fjdb.util.DateTimeUtil;
import fjdb.util.Pool;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
//import jfxtras.scene.control.LocalDateAxis;

public class LineChartTool {//extends Application {

    /*@Override
    public void start(Stage stage) {

//        if(true) {
//            double v = mcTests(100000);
//            System.out.printf("Average rolls %.4f\n", v);
////            histoPlot(stage);
//
//            createPlot(stage);
//            return;
//        }

        Series<LocalDate, Double> load = YahooDataLoader.load(Tickers.iSharesGBPIndexLinked.getName());

        stage.setTitle("Line Chart Sample");
        //defining the axes
//        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis xAxis = getDateAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Number of Month xx");
        //creating the chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        //defining a series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        series.setName("My portfolio");
        //populating the series with data
        Series.SeriesIterator<LocalDate, Double> iterator = load.iterator();

        int i=0;
        while(iterator.moveNext()) {
            series.getData().add(new XYChart.Data(++i, iterator.curentValue()));

        }

//        series.getData().add(new XYChart.Data(3, 15));
//        series.getData().add(new XYChart.Data(4, 24));
//        series.getData().add(new XYChart.Data(5, 34));
//        series.getData().add(new XYChart.Data(6, 36));
//        series.getData().add(new XYChart.Data(7, 22));
//        series.getData().add(new XYChart.Data(8, 45));
//        series.getData().add(new XYChart.Data(9, 43));
//        series.getData().add(new XYChart.Data(10, 17));
//        series.getData().add(new XYChart.Data(11, 29));
//        series.getData().add(new XYChart.Data(12, 25));

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);


        stage.setScene(scene);
        stage.show();

    }*/

    public static void createPlot(Stage stage, Ticker ticker, LocalDate startDate) {
        Series<LocalDate, Double> load = YahooDataLoader.load(ticker.getName());
        if (startDate == null) {
            createPlot(stage, load);
        } else {
            createPlot(stage, load.end(startDate));
        }
    }

    public static void createPlot(Stage stage, Ticker ticker) {
        createPlot(stage, ticker, null);
    }

    public static void createPlot(Stage stage) {
        createPlot(stage, Tickers.NASDAQ, DateTimeUtil.date(20231030));
    }

    public static void createPlot(Stage stage, Series<LocalDate, Double> dataSeries) {
        stage.setTitle("Line Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final ValueAxis<Number> yAxis = new NumberAxis();

        xAxis.setForceZeroInRange(false);

        StringConverter<Number> stringConverter = new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return LocalDate.ofEpochDay(number.longValue()).toString();
            }

            @Override
            public Number fromString(String s) {
                return Double.valueOf(s);
            }
        };
        xAxis.setTickLabelFormatter(stringConverter);
        xAxis.setLabel("Number of Month");
        //creating the chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        //defining a series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        series.setName("My portfolio");
        //populating the series with data
        Series.SeriesIterator<LocalDate, Double> iterator = dataSeries.iterator();

        while(iterator.moveNext()) {
            LocalDate localDate = iterator.currentKey();
            long epochDay = localDate.toEpochDay();
            series.getData().add(new XYChart.Data(epochDay, iterator.curentValue()));
        }
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

//    public static void main(String[] args) {
//        launch(args);
//    }

    static Random random = new Random();

    static Pool<Integer, AtomicInteger> pool = new Pool<>() {
        @Override
        public AtomicInteger create(Integer key) {
            return new AtomicInteger();
        }
    };

    private static double mcTests(int tests) {
        for (int i = 0; i <tests; i++) {
            pool.get(singleTest()).incrementAndGet();
        }

        Map<Integer, AtomicInteger> pool = LineChartTool.pool.getPool();
        double average = 0.0;
        for (Integer integer : pool.keySet()) {
            average += integer * pool.get(integer).get();
        }
        return average/tests;
    }

    private static void histoPlot(Stage stage) {
        TreeSet<Integer> rolls = new TreeSet<>(pool.getPool().keySet());


        stage.setTitle("Line Chart Sample");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Number of Month xx");
        //creating the chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        //defining a series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        series.setName("My portfolio");
        //populating the series with data

        for (Integer roll : rolls) {
            series.getData().add(new XYChart.Data(roll, pool.getPool().get(roll)));
        }
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    private static int singleTest() {

        boolean needFive = true;
        boolean needSix = true;
        int count = 0;
        while(needSix || needFive) {
//        while(needSix ) {
            count++;
            int value = getValue();
            if (value == 5) {
                needFive = false;
            } else if(value == 6) {
                needSix = false;
            }
        }

        return count;
    }

    private static int getValue() {

        int i = random.nextInt(1, 7);
        System.out.println(i);
        return i;
    }

}