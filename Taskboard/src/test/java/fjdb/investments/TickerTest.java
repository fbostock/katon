package fjdb.investments;

import fjdb.investments.tickers.Ticker;
import fjdb.investments.tickers.Tickers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TickerTest {

    @Test
    public void toString_returns_name() {
        Ticker ticker = Tickers.NASDAQ;
        Assertions.assertEquals(ticker.getName(), ticker.toString());
    }
}