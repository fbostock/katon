package fjdb.investments.fairvalue;

public class BuffettIntrinsicValueCalculator {


    // --- Method to calculate terminal value ---
    public static double calculateTerminalValue(double finalCashFlow, double terminalGrowth, double discountRate) {
        return finalCashFlow * (1 + terminalGrowth) / (discountRate - terminalGrowth);
    }

    // --- Method to calculate discounted cashflows ---
    public static double calculateDCF(double baseCashFlow, double growthRate, double discountRate, int years) {
        double pv = 0.0;
        for (int t = 1; t <= years; t++) {
            double cashFlow = baseCashFlow * Math.pow(1 + growthRate, t);
            pv += cashFlow / Math.pow(1 + discountRate, t);
        }
        return pv;
    }

    // --- Method to calculate intrinsic value ---
    public static double calculateIntrinsicValue(
            double baseCashFlow,
            double growthRate,
            double discountRate,
            double terminalGrowth,
            int years,
            double netDebt,
            double sharesOutstanding
    ) {
        // 1. PV of 10-year cashflows
        double pvCashFlows = calculateDCF(baseCashFlow, growthRate, discountRate, years);

        // 2. Terminal value
        double finalCashFlow = baseCashFlow * Math.pow(1 + growthRate, years);
        double terminalValue = calculateTerminalValue(finalCashFlow, terminalGrowth, discountRate);

        // 3. Discount terminal value
        double pvTerminal = terminalValue / Math.pow(1 + discountRate, years);

        // 4. Enterprise and equity value
        double enterpriseValue = pvCashFlows + pvTerminal;
        double equityValue = enterpriseValue - netDebt;

        // 5. Per share intrinsic value
        return (equityValue * 1_000_000_000) / (sharesOutstanding * 1_000_000_000); // returns value in £ per share
    }

    // --- Example main method to run the analysis ---
    public static void main(String[] args) {
        // --- Example assumptions for Legal & General Plc ---
        double baseCashFlow = 2.0;        // £2.0 billion owner earnings
        double growthRate = 0.05;         // 5% annual growth for 10 years
        double discountRate = 0.05;       // Buffett-style 5% discount rate
        double terminalGrowth = 0.03;     // 3% perpetual growth
        int years = 10;                   // 10-year explicit period
        double netDebt = 5.0;             // £5 billion debt
        double sharesOutstanding = 5.0;   // 5 billion shares

        double intrinsicValuePerShare = calculateIntrinsicValue(
                baseCashFlow,
                growthRate,
                discountRate,
                terminalGrowth,
                years,
                netDebt,
                sharesOutstanding
        );

        System.out.printf("Intrinsic Value per Share: £%.2f%n", intrinsicValuePerShare);
    }
}
