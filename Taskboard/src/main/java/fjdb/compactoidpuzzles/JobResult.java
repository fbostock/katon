package fjdb.compactoidpuzzles;

import java.util.List;

public class JobResult {
    private final Integer steps;
    private List<Position> bestPositions;

    public JobResult(Integer steps, List<Position> bestPositions) {
        this.steps = steps;
        this.bestPositions = bestPositions;
    }

    public Integer getSteps() {
        return steps;
    }

    public List<Position> getBestPositions() {
        return bestPositions;
    }
}
