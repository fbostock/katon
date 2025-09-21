package fjdb.investments.utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FormattersTest {

    @Test
    public void formatCurrency() {
        assertEquals("£100.00", Formatters.currency£(100.0));
        assertEquals("£123,456,789.00", Formatters.currency£(123456789.0));
        assertEquals("£0.56", Formatters.currency£(0.56));
        assertEquals("-£4.56", Formatters.currency£(-4.56));
        assertEquals("£0.00", Formatters.currency£(0.001));
        assertEquals("NaN", Formatters.currency£(Double.NaN));
    }
}