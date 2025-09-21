package fjdb.investments.backtests.models;

import fjdb.investments.backtests.MutableTrade;

import java.time.LocalDate;
import java.util.List;

public interface Model {

    String description();

    LocalDate getStart(LocalDate start);

    Double calcTargetPrice(LocalDate date);

    boolean doTrades(LocalDate date, List<MutableTrade> currentTrades);

    boolean takeOff(MutableTrade trade, LocalDate date);

    ModelParams getParams();

    <T> T getParameter(ModelParameter<T> parameter);

    <T> void setParameter(ModelParameter<T> parameter, T value);
}
