package fjdb.investments.utils;

import java.text.DecimalFormat;

public class Formatters {

    private static final DecimalFormat formatter = new DecimalFormat("£#,##0.00");

    public static String currency£(double value) {
        return formatter.format(value);
    }
}
