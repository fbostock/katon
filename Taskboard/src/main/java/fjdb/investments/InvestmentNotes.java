package fjdb.investments;

public class InvestmentNotes {


    public static void main(String[] args) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] split = line.split("\t");
            System.out.printf("public static final Ticker %s = make(\"%s\", \"%s\");\n", split[2], split[2], split[1]);
        }

    }

    static String content = "1\tMicrosoft Corp\tMSFT\t8.64%\t   419.84\t-1.15\t(-0.27%)\n" +
            "2\tApple Inc\tAAPL\t8.05%\t   190.08\t0.24\t(0.13%)\n" +
            "3\tNVIDIA Corp\tNVDA\t6.50%\t   923.88\t-19.71\t(-2.09%)\n" +
            "4\tAmazon.com Inc\tAMZN\t5.31%\t   184.11\t0.48\t(0.26%)\n" +
            "5\tBroadcom Inc\tAVGO\t4.73%\t   1,392.88\t-19.25\t(-1.36%)\n" +
            "6\tMeta Platforms Inc\tMETA\t4.63%\t   470.73\t-2.50\t(-0.53%)\n" +
            "7\tAlphabet Inc\tGOOGL\t2.80%\t   176.11\t1.93\t(1.11%)\n" +
            "8\tAlphabet Inc\tGOOG\t2.71%\t   177.31\t1.88\t(1.07%)\n" +
            "9\tCostco Wholesale Corp\tCOST\t2.48%\t   796.81\t3.73\t(0.47%)\n" +
            "10\tTesla Inc\tTSLA\t2.31%\t   177.22\t2.38\t(1.36%)\n" +
            "11\tNetflix Inc\tNFLX\t1.89%\t   620.16\t9.64\t(1.58%)\n" +
            "12\tAdvanced Micro Devices Inc\tAMD\t1.83%\t   164.32\t1.70\t(1.05%)\n" +
            "13\tPepsiCo Inc\tPEP\t1.75%\t   182.25\t-0.86\t(-0.47%)\n" +
            "14\tAdobe Inc\tADBE\t1.56%\t   483.40\t0.52\t(0.11%)\n" +
            "15\tQUALCOMM Inc\tQCOM\t1.54%\t   194.43\t1.16\t(0.60%)\n" +
            "16\tLinde PLC\tLIN\t1.47%\t   431.64\t1.95\t(0.45%)\n" +
            "17\tCisco Systems Inc\tCSCO\t1.43%\t   48.26\t-0.08\t(-0.16%)\n" +
            "18\tT-Mobile US Inc\tTMUS\t1.37%\t   163.76\t0.17\t(0.11%)\n" +
            "19\tIntuit Inc\tINTU\t1.30%\t   660.42\t7.05\t(1.08%)\n" +
            "20\tApplied Materials Inc\tAMAT\t1.28%\t   211.55\t-2.49\t(-1.16%)\n" +
            "21\tTexas Instruments Inc\tTXN\t1.26%\t   195.01\t0.04\t(0.02%)\n" +
            "22\tAmgen Inc\tAMGN\t1.21%\t   312.39\t-2.33\t(-0.74%)\n" +
            "23\tComcast Corp\tCMCSA\t1.10%\t   39.31\t-0.06\t(-0.15%)\n" +
            "24\tMicron Technology Inc\tMU\t1.00%\t   125.24\t-2.65\t(-2.07%)\n" +
            "25\tIntuitive Surgical Inc\tISRG\t1.00%\t   398.13\t1.70\t(0.43%)\n" +
            "26\tHoneywell International Inc\tHON\t0.95%\t   206.01\t-0.61\t(-0.30%)\n" +
            "27\tIntel Corp\tINTC\t0.94%\t   31.84\t-0.20\t(-0.61%)\n" +
            "28\tBooking Holdings Inc\tBKNG\t0.92%\t   3,698.77\t-36.24\t(-0.97%)\n" +
            "29\tLam Research Corp\tLRCX\t0.88%\t   911.53\t-31.37\t(-3.33%)\n" +
            "30\tVertex Pharmaceuticals Inc\tVRTX\t0.80%\t   445.24\t4.60\t(1.04%)\n" +
            "31\tAnalog Devices Inc\tADI\t0.76%\t   214.04\t-0.08\t(-0.04%)\n" +
            "32\tRegeneron Pharmaceuticals Inc\tREGN\t0.75%\t   979.65\t11.67\t(1.21%)\n" +
            "33\tKLA Corp\tKLAC\t0.73%\t   746.85\t-2.63\t(-0.35%)\n" +
            "34\tAutomatic Data Processing Inc\tADP\t0.72%\t   251.47\t1.41\t(0.56%)\n" +
            "35\tPalo Alto Networks Inc\tPANW\t0.72%\t   317.65\t0.87\t(0.27%)\n" +
            "36\tMondelez International Inc\tMDLZ\t0.68%\t   71.37\t-0.55\t(-0.76%)\n" +
            "37\tPDD Holdings Inc ADR\tPDD\t0.67%\t   145.63\t2.25\t(1.57%)\n" +
            "38\tSynopsys Inc\tSNPS\t0.63%\t   565.96\t-3.31\t(-0.58%)\n" +
            "39\tMercadoLibre Inc\tMELI\t0.62%\t   1,745.44\t6.29\t(0.36%)\n" +
            "40\tStarbucks Corp\tSBUX\t0.60%\t   77.70\t2.42\t(3.22%)\n" +
            "41\tGilead Sciences Inc\tGILD\t0.59%\t   67.50\t-0.36\t(-0.54%)\n" +
            "42\tASML Holding NV\tASML\t0.58%\t   926.01\t6.47\t(0.70%)\n" +
            "43\tCadence Design Systems Inc\tCDNS\t0.57%\t   288.53\t-0.40\t(-0.14%)\n" +
            "44\tCrowdstrike Holdings Inc\tCRWD\t0.56%\t   345.63\t6.57\t(1.94%)\n" +
            "45\tConstellation Energy Corp\tCEG\t0.50%\t   211.56\t-4.78\t(-2.21%)\n" +
            "46\tCintas Corp\tCTAS\t0.50%\t   690.77\t-0.61\t(-0.09%)\n" +
            "47\tNXP Semiconductors NV\tNXPI\t0.49%\t   267.72\t-1.89\t(-0.70%)\n" +
            "48\tPayPal Holdings Inc\tPYPL\t0.49%\t   64.44\t0.34\t(0.54%)\n" +
            "49\tMarriott International Inc/MD\tMAR\t0.49%\t   237.57\t-1.39\t(-0.58%)\n" +
            "50\tCSX Corp\tCSX\t0.47%\t   33.51\t-0.42\t(-1.25%)\n" +
            "51\tAirbnb Inc\tABNB\t0.45%\t   145.50\t-1.69\t(-1.15%)\n" +
            "52\tMarvell Technology Inc\tMRVL\t0.43%\t   72.22\t-0.86\t(-1.18%)\n" +
            "53\tO'Reilly Automotive Inc\tORLY\t0.42%\t   1,009.13\t3.15\t(0.31%)\n" +
            "54\tRoper Technologies Inc\tROP\t0.40%\t   542.90\t4.10\t(0.76%)\n" +
            "55\tPACCAR Inc\tPCAR\t0.40%\t   105.89\t-0.04\t(-0.04%)\n" +
            "56\tMonster Beverage Corp\tMNST\t0.40%\t   54.02\t-0.08\t(-0.14%)\n" +
            "57\tCopart Inc\tCPRT\t0.38%\t   54.40\t-0.18\t(-0.32%)\n" +
            "58\tWorkday Inc\tWDAY\t0.37%\t   258.42\t1.85\t(0.72%)\n" +
            "59\tMicrochip Technology Inc\tMCHP\t0.37%\t   94.37\t0.13\t(0.14%)\n" +
            "60\tDexcom Inc\tDXCM\t0.35%\t   130.89\t-1.03\t(-0.78%)\n" +
            "61\tModerna Inc\tMRNA\t0.35%\t   132.62\t-0.06\t(-0.04%)\n" +
            "62\tAmerican Electric Power Co Inc\tAEP\t0.34%\t   92.62\t0.08\t(0.08%)\n" +
            "63\tAutodesk Inc\tADSK\t0.34%\t   220.10\t-0.34\t(-0.15%)\n" +
            "64\tKeurig Dr Pepper Inc\tKDP\t0.33%\t   33.76\t-0.38\t(-1.10%)\n" +
            "65\tFortinet Inc\tFTNT\t0.33%\t   61.39\t0.15\t(0.24%)\n" +
            "66\tIDEXX Laboratories Inc\tIDXX\t0.32%\t   542.54\t1.60\t(0.30%)\n" +
            "67\tPaychex Inc\tPAYX\t0.32%\t   125.26\t0.07\t(0.06%)\n" +
            "68\tAstraZeneca PLC ADR\tAZN\t0.32%\t   76.89\t-0.16\t(-0.20%)\n" +
            "69\tRoss Stores Inc\tROST\t0.32%\t   132.40\t-1.94\t(-1.45%)\n" +
            "70\tDoorDash Inc\tDASH\t0.31%\t   117.49\t1.33\t(1.15%)\n" +
            "71\tKraft Heinz Co/The\tKHC\t0.31%\t   36.05\t-0.02\t(-0.07%)\n" +
            "72\tLululemon Athletica Inc\tLULU\t0.30%\t   334.30\t-3.98\t(-1.18%)\n" +
            "73\tTrade Desk Inc/The\tTTD\t0.28%\t   94.80\t1.61\t(1.73%)\n" +
            "74\tOld Dominion Freight Line Inc\tODFL\t0.28%\t   182.28\t0.11\t(0.06%)\n" +
            "75\tCharter Communications Inc\tCHTR\t0.28%\t   273.61\t-3.93\t(-1.42%)\n" +
            "76\tExelon Corp\tEXC\t0.28%\t   38.52\t-0.10\t(-0.27%)\n" +
            "77\tFastenal Co\tFAST\t0.27%\t   66.38\t-0.43\t(-0.65%)\n" +
            "78\tGE HealthCare Technologies Inc\tGEHC\t0.27%\t   81.42\t0.11\t(0.14%)\n" +
            "79\tDatadog Inc\tDDOG\t0.26%\t   120.05\t-0.56\t(-0.46%)\n" +
            "80\tCoStar Group Inc\tCSGP\t0.26%\t   87.57\t-0.56\t(-0.64%)\n" +
            "81\tVerisk Analytics Inc\tVRSK\t0.25%\t   251.45\t-0.03\t(-0.01%)\n" +
            "82\tDiamondback Energy Inc\tFANG\t0.25%\t   197.65\t3.06\t(1.57%)\n" +
            "83\tCoca-Cola Europacific Partners PLC\tCCEP\t0.24%\t   74.38\t-0.54\t(-0.72%)\n" +
            "84\tCognizant Technology Solutions Corp\tCTSH\t0.24%\t   68.70\t-1.63\t(-2.31%)\n" +
            "85\tBiogen Inc\tBIIB\t0.24%\t   230.18\t0.14\t(0.06%)\n" +
            "86\tElectronic Arts Inc\tEA\t0.24%\t   127.78\t-1.38\t(-1.07%)\n" +
            "87\tBaker Hughes Co\tBKR\t0.23%\t   33.46\t0.43\t(1.32%)\n" +
            "88\tON Semiconductor Corp\tON\t0.23%\t   73.31\t0.31\t(0.42%)\n" +
            "89\tXcel Energy Inc\tXEL\t0.22%\t   55.46\t-0.39\t(-0.69%)\n" +
            "90\tCDW Corp/DE\tCDW\t0.21%\t   223.96\t-0.18\t(-0.08%)\n" +
            "91\tGLOBALFOUNDRIES Inc\tGFS\t0.21%\t   53.91\t-0.27\t(-0.51%)\n" +
            "92\tAtlassian Corp\tTEAM\t0.21%\t   180.71\t-1.50\t(-0.83%)\n" +
            "93\tANSYS Inc\tANSS\t0.20%\t   327.30\t-0.23\t(-0.07%)\n" +
            "94\tMongoDB Inc\tMDB\t0.19%\t   370.13\t0.19\t(0.05%)\n" +
            "95\tZscaler Inc\tZS\t0.19%\t   179.18\t-0.13\t(-0.07%)\n" +
            "96\tDollar Tree Inc\tDLTR\t0.18%\t   117.29\t-4.01\t(-3.31%)\n" +
            "97\tTake-Two Interactive Software Inc\tTTWO\t0.18%\t   148.21\t2.13\t(1.46%)\n" +
            "98\tWarner Bros Discovery Inc\tWBD\t0.14%\t   8.05\t-0.19\t(-2.25%)\n" +
            "99\tIllumina Inc\tILMN\t0.13%\t   111.04\t-3.42\t(-2.99%)\n" +
            "100\tWalgreens Boots Alliance Inc\tWBA\t0.11%\t   18.17\t-0.17\t(-0.95%)\n" +
            "101\tSirius XM Holdings Inc\tSIRI\t0.08%\t   3.01\t-0.04\t(-1.48%)";
    /*
    ANGLE
    B&M
    Benchmark
    Byotrol
    Centrica
    Hikma
    Immupharma
    Ocado
    Plus500
    TekCapital
    Tremor - Nexxen
    Trustpilot

     */
}
