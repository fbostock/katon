package fjdb.investments.backtests.models;

import fjdb.investments.FinancialDataSource;
import fjdb.investments.tickers.Ticker;
import fjdb.investments.backtests.MutableTrade;
import fjdb.series.Series;

import java.time.LocalDate;
import java.util.List;

public abstract class ModelBase implements Model {
    protected final Ticker ticker;
    private FinancialDataSource financialDataSource;
    protected final Series<LocalDate, Double> priceSeries;
    protected ModelParams params;

    public ModelBase(Ticker ticker, FinancialDataSource financialDataSource, ModelParams modelParams) {
        this.ticker = ticker;
        this.financialDataSource = financialDataSource;
        this.priceSeries = financialDataSource.getPriceSeries(ticker);
        this.params = modelParams;
    }

    public ModelBase(Ticker ticker, FinancialDataSource financialDataSource) {
        this(ticker, financialDataSource, new ModelParams());
    }

    @Override
    public abstract String description();

    @Override
    public boolean doTrades(LocalDate date, List<MutableTrade> currentTrades) {
        Double targetPrice = calcTargetPrice(date);
        return priceSeries.get(date) < targetPrice;
    }

    @Override
    public ModelParams getParams() {
        return params;
    }

    public <T> T getParameter(ModelParameter<T> parameter) {
        return params.getParameterOrDefault(parameter);
    }

    public <T> void setParameter(ModelParameter<T> parameter, T value) {
        params.setParameter(parameter, value);
    }
}
