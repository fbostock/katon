package fjdb.investments.backtests.models;

import fjdb.investments.Ticker;

public interface ModelMaker {
    Model makeModel(Ticker ticker);
}
