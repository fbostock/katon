package fjdb.investments.backtests;

import fjdb.investments.tickers.Ticker;

import java.time.LocalDate;
import java.util.List;

public class Portfolio {
    private final Ticker ticker;
    private final List<Trade> trades;
    private final List<MutableTrade> liveTrades;
    private double totalPnl = 0.0;

    public Portfolio(Ticker ticker, List<Trade> trades, List<MutableTrade> liveTrades) {
        this.ticker = ticker;
        this.trades = trades;
        this.liveTrades = liveTrades;

        for (Trade trade : trades) {
            totalPnl += trade.pnl;
        }

    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<MutableTrade> getLiveTrades() {
        return liveTrades;
    }

    public double getTotalPnl() {
        return totalPnl;
    }

    public String printAnalysis(double initialAmount, LocalDate start, LocalDate end, boolean includeTradeDetail) {
        double totalPnl = getTotalPnl();
        System.out.printf("######%s######\n", ticker);
        System.out.printf("Trades: %s Pnl %.2f\n", trades.size(), totalPnl);
        if (includeTradeDetail) {
            for (int i = 0; i < trades.size(); i++) {
                Trade trade = trades.get(i);
                System.out.printf("Trade %s Start %s End %s Days %s, Pnl %.2f\n", i, trade.tradeDate, trade.endDate, BackTester.weekendHoliday.daysBetween(trade.tradeDate, trade.endDate), trade.pnl);
            }
        }

        double returnAmount = 100 * totalPnl / initialAmount;
        double years = start.datesUntil(end).count() / 365.0;
        System.out.printf("%.2f%% Gain (per year %.2f%%) on %.2f\n", returnAmount, returnAmount / years, initialAmount);

        return String.format("%s:\nPnL: £%.2f = %.2f%% Gain (per year %.2f%%) on £%.2f\n", ticker, totalPnl, returnAmount, returnAmount / years, initialAmount);
    }
}
