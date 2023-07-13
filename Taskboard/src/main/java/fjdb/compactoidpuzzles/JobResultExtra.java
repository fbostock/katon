package fjdb.compactoidpuzzles;

import java.util.List;

public class JobResultExtra extends JobResult {
    private final List<Integer> data;

    public JobResultExtra(Integer steps, List<Position> bestPositions, List<Integer> data) {
        super(steps, bestPositions);
        this.data = data;
    }

    public List<Integer> getData() {
        return data;
    }

    public GameSolving.Histogram getHistogram(String title) {
        double[] doubleData = new double[data.size()];
        int maxSteps = 0;
        for (int i = 0; i < data.size(); i++) {
            Integer integer = data.get(i);
            doubleData[i] = integer;
            maxSteps = Math.max(maxSteps, integer);
        }
        GameSolving.Histogram histogram = new GameSolving.Histogram(doubleData, maxSteps, title);
        return histogram;
    }
}
