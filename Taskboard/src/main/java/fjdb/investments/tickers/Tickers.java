package fjdb.investments.tickers;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class Tickers {

    private static final List<Ticker> s_tickers = Lists.newArrayList();

    public static final Ticker FTSE_100 = make("VUKE.L", "Vanguard FTSE100");
    public static final Ticker FTSE_250 = make("VMID.L", "Vanguard FTSE250");
    public static final Ticker SNP_500 = make("VUSA.L", "Vanguard SNP500");
    public static final Ticker Germany_AllShare = make("VGER.L", "Vanguard Germany AllShare");
    public static final Ticker NASDAQ = make("CNX1.L", "Nasdaq 100");
    public static final Ticker DowJonesIndustrialAverage_iShares = make("CIND.L", "iShares Dow Jones Industrial Average");
    public static final Ticker Lyxor_CAC_40 = make("CACX.L", "Lyxor CAC40");
    public static final Ticker XTrackers_Nikkei_225 = make("XDJP.L", "Xtrackers Nikkei225");

    public static final Ticker Vanguard_USD_Treasury = make("VUTY.L", "Vanguard USD Treasury Bond UCITS ETF");
    public static final Ticker iSharesCoreUKGilt = make("IGLT.L", "iShares Core UK Gilts UCITS ETF");
    public static final Ticker iSharesUKDividend = make("IUKD.L", "iShares UK Dividend UCITS ETF");
    public static final Ticker iSharesGBPIndexLinked = make("INXG.L", "iShares GBP Index linked Gilts UCITS ETF");
    public static final Ticker iSharesUSDTreasuryBond = make("CBUG.L", "iShares USD Treasury Bond 3-7yr UCITS ETF");

    public static final Ticker BAndMRetail = make("BME.L", "B&M");

    /*
    Microsalt  SALT.L  43.12m    ~60p
    Belluscura BELL.L  401m shares 0.9cents
    GenIP      GNIP.L  17.52m   23p
    Inno eye   LUCY    2.45m   $2.6
     */
    public static final Ticker Microsalt = make("SALT.L", "Microsalt");
    public static final Ticker Belluscura = make("BELL.L", "Belluscura");
    public static final Ticker GenIP = make("GNIP.L", "GenIP Plc");
    public static final Ticker InnovativeEyewear = make("LUCY", "Innovative Eyewear");
    public static final Ticker TekCapital = make("TEK.L", "TekCapital");
    public static final List<Ticker> TEKCAPITAL_PORTFOLIO = Lists.newArrayList(Microsalt, Belluscura, GenIP, InnovativeEyewear);

    public static final List<Ticker> Index_ETFs = Lists.newArrayList(FTSE_100, FTSE_250, SNP_500, Germany_AllShare, NASDAQ, DowJonesIndustrialAverage_iShares, Lyxor_CAC_40, XTrackers_Nikkei_225);
    public static final List<Ticker> BOND_ETFS = Lists.newArrayList(Vanguard_USD_Treasury, iSharesCoreUKGilt, iSharesUKDividend, iSharesGBPIndexLinked, iSharesUSDTreasuryBond);
    public static final List<Ticker> EQUITY = Lists.newArrayList(BAndMRetail);

    public static final List<Ticker> My_Portfolio = Lists.newArrayList(TekCapital);

    private static String nasdaq100 = "MSFT,AAPL,NVDA,AMZN,AVGO,META,GOOGL,GOOG,COST,TSLA,NFLX,AMD,PEP,ADBE,QCOM,LIN,CSCO,TMUS,INTU,AMAT,TXN,AMGN,CMCSA,MU,ISRG,HON,INTC,BKNG,LRCX,VRTX,ADI,REGN,KLAC,ADP,PANW,MDLZ,PDD,SNPS,MELI,SBUX,GILD,ASML,CDNS,CRWD,CEG,CTAS,NXPI,PYPL,MAR,CSX,ABNB,MRVL,ORLY,ROP,PCAR,MNST,CPRT,WDAY,MCHP,DXCM,MRNA,AEP,ADSK,KDP,FTNT,IDXX,PAYX,AZN,ROST,DASH,KHC,LULU,TTD,ODFL,CHTR,EXC,FAST,GEHC,DDOG,CSGP,VRSK,FANG,CCEP,CTSH,BIIB,EA,BKR,ON,XEL,CDW,GFS,TEAM,ANSS,MDB,ZS,DLTR,TTWO,WBD,ILMN,WBA,SIRI";

    public static final List<Ticker> NASDAQ_CONSTITUENTS = List.copyOf(EquityTickers.NASDAQ_CONSTITUENTS);

    public static List<Ticker> getAll() {
        return Collections.unmodifiableList(s_tickers);
    }

    protected static Ticker make(String name, String description) {
        Ticker ticker = new Ticker(name);
        s_tickers.add(ticker);
        return ticker;
    }
}
