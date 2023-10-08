package fjdb.investments;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class Tickers {

    private static final List<Ticker> s_tickers = Lists.newArrayList();

    public static final Ticker FTSE_100 = make("VUKE.L");
    public static final Ticker FTSE_250 = make("VMID.L");
    public static final Ticker SNP_500 = make("VUSA.L");
    public static final Ticker Germany_AllShare = make("VGER.L");
    public static final Ticker NASDAQ = make("CNX1.L");
    public static final Ticker DowJonesIndustrialAverage_iShares = make("CIND.L");
    public static final Ticker Lyxor_CAC_40 = make("CACX.L");
    public static final Ticker XTrackers_Nikkei_225 = make("XDJP.L");


    public static final Ticker Vanguard_USD_Treasury = make("VUTY.L", "Vanguard USD Treasury Bond UCITS ETF");
    public static final Ticker iSharesCoreUKGilt = make("IGLT.L", "iShares Core UK Gilts UCITS ETF");
    public static final Ticker iSharesUKDividend = make("IUKD.L", "iShares UK Dividend UCITS ETF");
    public static final Ticker iSharesGBPIndexLinked = make("INXG.L", "iShares GBP Index linked Gilts UCITS ETF");
    public static final Ticker iSharesUSDTreasuryBond = make("CBUG.L", "iShares USD Treasury Bond 3-7yr UCITS ETF");

    public static final List<Ticker> Index_ETFs = Lists.newArrayList(FTSE_100, FTSE_250, SNP_500, Germany_AllShare, NASDAQ, DowJonesIndustrialAverage_iShares, Lyxor_CAC_40, XTrackers_Nikkei_225);
    public static final List<Ticker> BOND_ETFS = Lists.newArrayList(Vanguard_USD_Treasury, iSharesCoreUKGilt, iSharesUKDividend, iSharesGBPIndexLinked, iSharesUSDTreasuryBond);

    public static List<Ticker> getAll() {
        return Collections.unmodifiableList(s_tickers);
    }

    private static Ticker make(String name) {
        Ticker ticker = new Ticker(name);
        s_tickers.add(ticker);
        return ticker;
    }
    private static Ticker make(String name, String description) {
        Ticker ticker = new Ticker(name);
        s_tickers.add(ticker);
        return ticker;
    }
}
