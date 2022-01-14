package fjdb.maths.collatz;

public class NullResult implements Result<NullResult> {

    @Override
    public String details() {
        return "NA";
    }

    @Override
    public String printout() {
        return "NA";
    }

    @Override
    public int numDependents() {
        return 0;
    }

    @Override
    public int numEvenOperations() {
        return 0;
    }

    @Override
    public int numOddOperations() {
        return 0;
    }

    @Override
    public long getValue() {
        return 0;
    }

    @Override
    public NullResult getDependentResult() {
        return null;
    }

    @Override
    public void setDependentResult(NullResult result) {
        //no op
    }

    @Override
    public NullResult getRoot() {
        return null;
    }

    @Override
    public boolean isDependent(NullResult other) {
        return false;
    }
}
