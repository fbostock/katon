package fjdb.investments.backtests.models;

import fjdb.investments.tickers.Ticker;

public interface ModelMaker {
    Model makeModel(Ticker ticker);
}
