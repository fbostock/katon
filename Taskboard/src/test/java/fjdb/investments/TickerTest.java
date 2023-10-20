package fjdb.investments;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TickerTest {

    @Test
    public void toString_returns_name() {
        Ticker ticker = Tickers.NASDAQ;
        Assertions.assertEquals(ticker.getName(), ticker.toString());
    }
}