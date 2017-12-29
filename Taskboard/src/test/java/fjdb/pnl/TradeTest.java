package fjdb.pnl;

import fjdb.util.DateTimeUtil;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

/**
 * Created by francisbostock on 13/11/2017.
 */
public class TradeTest {

    @Test
    public void exit_date_same_day_for_etf_one_month_for_equity() {
        LocalDate date = DateTimeUtil.date(20171002);
        Trade etfTrade = new Trade(TradeType.ETF, "VMID", date, 330, 31.9152, Currency.getInstance("GBP"), 1.0);
        assertEquals(date, etfTrade.minimumExitDate());
        Trade equityTrade = new Trade(TradeType.EQUITY, "CNA", date, 3000, 179.4717, Currency.getInstance("GBP"), 1.0);
        assertEquals(date.plusMonths(1), equityTrade.minimumExitDate());

    }
}