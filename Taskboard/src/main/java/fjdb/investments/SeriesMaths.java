package fjdb.investments;

import fjdb.series.Series;

public class SeriesMaths {

    public static  <T extends Comparable<? super T>> Double max(Series<T, Double> series) {
        int size = series.getSize();
        Double max = Double.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            Double value = series.get(i);
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static <T extends Comparable<? super T>> Double min(Series<T, Double> series) {
        int size = series.getSize();
        Double min = Double.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            Double value = series.get(i);
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static <T extends Comparable<? super T>> Double mean(Series<T, Double> series) {
        Double sum = 0.0;
        int size = series.getSize();
        for (int i = 0; i < size; i++) {
            sum += series.get(i);
        }
        return sum / size;
    }
}
