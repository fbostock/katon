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
}
