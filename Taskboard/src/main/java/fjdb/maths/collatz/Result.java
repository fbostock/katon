package fjdb.maths.collatz;

import com.google.common.collect.Lists;

import java.util.List;

public interface Result<T extends Result<T>> {

    public String details();

    public String printout();

    public int numDependents();

    public int numEvenOperations();

    public int numOddOperations();

    long getValue();

    T getDependentResult();

    void setDependentResult(T result);

    T getRoot();

    boolean isDependent(T other);

    default int stepsForRoot() {
        if (getRoot().getValue() != getValue()) {
            return -1;
        }
        int steps = 1;
        T dependentResult = getDependentResult();
        while(true) {
            if (dependentResult.getValue() == getValue()) {
                return steps;
            } else {
                steps++;
                dependentResult = dependentResult.getDependentResult();
            }
        }

    }

    default List<Result<T>> results() {
        List<Result<T>> results = Lists.newArrayList();
        T root = getRoot();
            results.add(this);
        T dependent = getDependentResult();
        do {
            results.add(dependent);
            dependent = dependent.getDependentResult();
        } while (root.getValue() != dependent.getValue());
        results.add(root);
        return results;
    }
}
