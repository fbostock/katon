package fjdb.investments.tickers;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fjdb.investments.tickers.Tickers.make;

public class EquityTickers {

    protected static final List<Ticker> NASDAQ_CONSTITUENTS = Lists.newArrayList();

    public static final Ticker MSFT = makeNasDaq("MSFT", "Microsoft Corp");
    public static final Ticker AAPL = makeNasDaq("AAPL", "Apple Inc");
    public static final Ticker NVDA = makeNasDaq("NVDA", "NVIDIA Corp");
    public static final Ticker AMZN = makeNasDaq("AMZN", "Amazon.com Inc");
    public static final Ticker AVGO = makeNasDaq("AVGO", "Broadcom Inc");
    public static final Ticker META = makeNasDaq("META", "Meta Platforms Inc");
    public static final Ticker GOOGL = makeNasDaq("GOOGL", "Alphabet Inc");
    public static final Ticker GOOG = makeNasDaq("GOOG", "Alphabet Inc");
    public static final Ticker COST = makeNasDaq("COST", "Costco Wholesale Corp");
    public static final Ticker TSLA = makeNasDaq("TSLA", "Tesla Inc");
    public static final Ticker NFLX = makeNasDaq("NFLX", "Netflix Inc");
    public static final Ticker AMD = makeNasDaq("AMD", "Advanced Micro Devices Inc");
    public static final Ticker PEP = makeNasDaq("PEP", "PepsiCo Inc");
    public static final Ticker ADBE = makeNasDaq("ADBE", "Adobe Inc");
    public static final Ticker QCOM = makeNasDaq("QCOM", "QUALCOMM Inc");
    public static final Ticker LIN = makeNasDaq("LIN", "Linde PLC");
    public static final Ticker CSCO = makeNasDaq("CSCO", "Cisco Systems Inc");
    public static final Ticker TMUS = makeNasDaq("TMUS", "T-Mobile US Inc");
    public static final Ticker INTU = makeNasDaq("INTU", "Intuit Inc");
    public static final Ticker AMAT = makeNasDaq("AMAT", "Applied Materials Inc");
    public static final Ticker TXN = makeNasDaq("TXN", "Texas Instruments Inc");
    public static final Ticker AMGN = makeNasDaq("AMGN", "Amgen Inc");
    public static final Ticker CMCSA = makeNasDaq("CMCSA", "Comcast Corp");
    public static final Ticker MU = makeNasDaq("MU", "Micron Technology Inc");
    public static final Ticker ISRG = makeNasDaq("ISRG", "Intuitive Surgical Inc");
    public static final Ticker HON = makeNasDaq("HON", "Honeywell International Inc");
    public static final Ticker INTC = makeNasDaq("INTC", "Intel Corp");
    public static final Ticker BKNG = makeNasDaq("BKNG", "Booking Holdings Inc");
    public static final Ticker LRCX = makeNasDaq("LRCX", "Lam Research Corp");
    public static final Ticker VRTX = makeNasDaq("VRTX", "Vertex Pharmaceuticals Inc");
    public static final Ticker ADI = makeNasDaq("ADI", "Analog Devices Inc");
    public static final Ticker REGN = makeNasDaq("REGN", "Regeneron Pharmaceuticals Inc");
    public static final Ticker KLAC = makeNasDaq("KLAC", "KLA Corp");
    public static final Ticker ADP = makeNasDaq("ADP", "Automatic Data Processing Inc");
    public static final Ticker PANW = makeNasDaq("PANW", "Palo Alto Networks Inc");
    public static final Ticker MDLZ = makeNasDaq("MDLZ", "Mondelez International Inc");
    public static final Ticker PDD = makeNasDaq("PDD", "PDD Holdings Inc ADR");
    public static final Ticker SNPS = makeNasDaq("SNPS", "Synopsys Inc");
    public static final Ticker MELI = makeNasDaq("MELI", "MercadoLibre Inc");
    public static final Ticker SBUX = makeNasDaq("SBUX", "Starbucks Corp");
    public static final Ticker GILD = makeNasDaq("GILD", "Gilead Sciences Inc");
    public static final Ticker ASML = makeNasDaq("ASML", "ASML Holding NV");
    public static final Ticker CDNS = makeNasDaq("CDNS", "Cadence Design Systems Inc");
    public static final Ticker CRWD = makeNasDaq("CRWD", "Crowdstrike Holdings Inc");
    public static final Ticker CEG = makeNasDaq("CEG", "Constellation Energy Corp");
    public static final Ticker CTAS = makeNasDaq("CTAS", "Cintas Corp");
    public static final Ticker NXPI = makeNasDaq("NXPI", "NXP Semiconductors NV");
    public static final Ticker PYPL = makeNasDaq("PYPL", "PayPal Holdings Inc");
    public static final Ticker MAR = makeNasDaq("MAR", "Marriott International Inc/MD");
    public static final Ticker CSX = makeNasDaq("CSX", "CSX Corp");
    public static final Ticker ABNB = makeNasDaq("ABNB", "Airbnb Inc");
    public static final Ticker MRVL = makeNasDaq("MRVL", "Marvell Technology Inc");
    public static final Ticker ORLY = makeNasDaq("ORLY", "O'Reilly Automotive Inc");
    public static final Ticker ROP = makeNasDaq("ROP", "Roper Technologies Inc");
    public static final Ticker PCAR = makeNasDaq("PCAR", "PACCAR Inc");
    public static final Ticker MNST = makeNasDaq("MNST", "Monster Beverage Corp");
    public static final Ticker CPRT = makeNasDaq("CPRT", "Copart Inc");
    public static final Ticker WDAY = makeNasDaq("WDAY", "Workday Inc");
    public static final Ticker MCHP = makeNasDaq("MCHP", "Microchip Technology Inc");
    public static final Ticker DXCM = makeNasDaq("DXCM", "Dexcom Inc");
    public static final Ticker MRNA = makeNasDaq("MRNA", "Moderna Inc");
    public static final Ticker AEP = makeNasDaq("AEP", "American Electric Power Co Inc");
    public static final Ticker ADSK = makeNasDaq("ADSK", "Autodesk Inc");
    public static final Ticker KDP = makeNasDaq("KDP", "Keurig Dr Pepper Inc");
    public static final Ticker FTNT = makeNasDaq("FTNT", "Fortinet Inc");
    public static final Ticker IDXX = makeNasDaq("IDXX", "IDEXX Laboratories Inc");
    public static final Ticker PAYX = makeNasDaq("PAYX", "Paychex Inc");
    public static final Ticker AZN = makeNasDaq("AZN", "AstraZeneca PLC ADR");
    public static final Ticker ROST = makeNasDaq("ROST", "Ross Stores Inc");
    public static final Ticker DASH = makeNasDaq("DASH", "DoorDash Inc");
    public static final Ticker KHC = makeNasDaq("KHC", "Kraft Heinz Co/The");
    public static final Ticker LULU = makeNasDaq("LULU", "Lululemon Athletica Inc");
    public static final Ticker TTD = makeNasDaq("TTD", "Trade Desk Inc/The");
    public static final Ticker ODFL = makeNasDaq("ODFL", "Old Dominion Freight Line Inc");
    public static final Ticker CHTR = makeNasDaq("CHTR", "Charter Communications Inc");
    public static final Ticker EXC = makeNasDaq("EXC", "Exelon Corp");
    public static final Ticker FAST = makeNasDaq("FAST", "Fastenal Co");
    public static final Ticker GEHC = makeNasDaq("GEHC", "GE HealthCare Technologies Inc");
    public static final Ticker DDOG = makeNasDaq("DDOG", "Datadog Inc");
    public static final Ticker CSGP = makeNasDaq("CSGP", "CoStar Group Inc");
    public static final Ticker VRSK = makeNasDaq("VRSK", "Verisk Analytics Inc");
    public static final Ticker FANG = makeNasDaq("FANG", "Diamondback Energy Inc");
    public static final Ticker CCEP = makeNasDaq("CCEP", "Coca-Cola Europacific Partners PLC");
    public static final Ticker CTSH = makeNasDaq("CTSH", "Cognizant Technology Solutions Corp");
    public static final Ticker BIIB = makeNasDaq("BIIB", "Biogen Inc");
    public static final Ticker EA = makeNasDaq("EA", "Electronic Arts Inc");
    public static final Ticker BKR = makeNasDaq("BKR", "Baker Hughes Co");
    public static final Ticker ON = makeNasDaq("ON", "ON Semiconductor Corp");
    public static final Ticker XEL = makeNasDaq("XEL", "Xcel Energy Inc");
    public static final Ticker CDW = makeNasDaq("CDW", "CDW Corp/DE");
    public static final Ticker GFS = makeNasDaq("GFS", "GLOBALFOUNDRIES Inc");
    public static final Ticker TEAM = makeNasDaq("TEAM", "Atlassian Corp");
    public static final Ticker ANSS = makeNasDaq("ANSS", "ANSYS Inc");
    public static final Ticker MDB = makeNasDaq("MDB", "MongoDB Inc");
    public static final Ticker ZS = makeNasDaq("ZS", "Zscaler Inc");
    public static final Ticker DLTR = makeNasDaq("DLTR", "Dollar Tree Inc");
    public static final Ticker TTWO = makeNasDaq("TTWO", "Take-Two Interactive Software Inc");
    public static final Ticker WBD = makeNasDaq("WBD", "Warner Bros Discovery Inc");
    public static final Ticker ILMN = makeNasDaq("ILMN", "Illumina Inc");
    public static final Ticker WBA = makeNasDaq("WBA", "Walgreens Boots Alliance Inc");
    public static final Ticker SIRI = makeNasDaq("SIRI", "Sirius XM Holdings Inc");


    private static Ticker makeNasDaq(String ticker, String description) {
        Ticker item = make(ticker, description);
        NASDAQ_CONSTITUENTS.add(item);
        return item;
    }
}
