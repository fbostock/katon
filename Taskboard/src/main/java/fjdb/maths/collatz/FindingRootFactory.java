package fjdb.maths.collatz;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FindingRootFactory implements ResultFactory<FindingRootFactory.ResultUnknownRoot> {
    /*
    TODO this should produce Result objects that are capable of identifying their roots.

    e.g. suppose for a given {p,q}, there are values j,k,l which are roots for numbers {n}, i.e. 3 islands around which numbers
    circulate. We want the result objects to identify those roots.
    They would do this by storing a set of results which are their dependents, and once it tries to calculate a dependent
    which already exists, it stops.
    For default Collatz, this should therefore break at 1,2 or 4.

    Perhaps this should not be done by the Result object, but by the algorithm.


     */
    @Override
    public ResultUnknownRoot makeResult(long value) {
        return new ResultUnknownRoot(value);
    }

    //TODO where methods depend on a root value, it should instead have a mechanism where it recognises when its dependents
    //eventually revolve to a same root, e.g. calling a private method which takes a set where the set contains all
    //the current values
    class ResultUnknownRoot implements Result<ResultUnknownRoot> {
        private final long value;

        //method to total number of 3n+1 operations
        //method to total number of steps to 1

        private ResultUnknownRoot dependentResult;

        public ResultUnknownRoot(long value) {
            if (value < 0) {
                throw new RuntimeException(String.format("Negative value: %s", value));
            }
            this.value = value;
        }

        public void setDependentResult(ResultUnknownRoot result) {
            dependentResult = result;
        }

        public long getValue() {
            return value;
        }

        public ResultUnknownRoot getDependentResult() {
            return dependentResult;
        }

        public boolean isEven() {
            return value % 2 == 0;
        }

        @Override
        public String toString() {
            return String.format("int %s", value);
        }

        private int calcEvens() {
            if (getRoot().getValue() == getValue()) return 0;
            int count = isEven() ? 1 : 0;
            return count + dependentResult.calcEvens();
        }

        public int numEvenOperations() {
            if (evens.get() < 0) {
                evens.set(calcEvens());
            }
            return evens.get();
        }

        private int calcOdds() {
            if (getRoot().getValue() == getValue()) return 0;
            int count = isEven() ? 0 : 1;
            return count + dependentResult.calcOdds();
        }

        public int numOddOperations() {
            if (odds.get() < 0) {
                odds.set(calcOdds());
            }
            return odds.get();
        }

        private AtomicInteger numDepends = new AtomicInteger(-1);
        private AtomicInteger odds = new AtomicInteger(-1);
        private AtomicInteger evens = new AtomicInteger(-1);

        private int calcNumDependents() {
            if (getRoot().getValue() == getValue()) {
                return 0;
            }
            return 1 + dependentResult.calcNumDependents();
        }

        public int numDependents() {
            if (numDepends.get() < 0) {
                numDepends.set(calcNumDependents());
            }
            return numDepends.get();
        }


        @Override
        public boolean isDependent(ResultUnknownRoot other) {
            if (this.value == other.value) return true;
            ResultUnknownRoot dependentResult = this;
            while (true) {
                dependentResult = dependentResult.getDependentResult();
                if (getRoot().getValue() == dependentResult.getValue()) return false;

                if (other.getValue() == dependentResult.value) {
                    return true;
                }
            }
        }

        private AtomicReference<ResultUnknownRoot> _root = new AtomicReference<>(null);

        public ResultUnknownRoot getRoot() {
            if (_root.get() == null) {
                TreeMap<Long, ResultUnknownRoot> values = new TreeMap<>();
                ResultUnknownRoot dependentResult = this;
                while (true) {
                    if (values.containsKey(dependentResult.getValue())) {
                        break;
                    } else {
                        values.put(dependentResult.getValue(), dependentResult);
                        dependentResult = dependentResult.getDependentResult();
                    }
                }
                //TODO a root is a number which depends on itself. We should define it as the lowest number which dpends
                //on itself.
                for (Map.Entry<Long, ResultUnknownRoot> entry : values.entrySet()) {
                    //lowest value may not resolve to itself.
                    if (entry.getValue().stopAtRoot().getValue() == entry.getValue().getValue()) {
                        _root.set(entry.getValue());
                        break;
                    }
                }
            }
            return _root.get();
        }

        ResultUnknownRoot stopAtRoot() {
            Set<Long> values = new HashSet<>();
            ResultUnknownRoot dependentResult = this;
            while(true) {
                if (values.contains(dependentResult.getValue())) {
                    return dependentResult;
                } else {
                    values.add(dependentResult.getValue());
                    dependentResult = dependentResult.getDependentResult();
                }
            }
        }

        public String printout() {
            String message = String.format("%s", value);
            int count = 1;
            ResultUnknownRoot dependentResult;
            Set<Long> values = new HashSet<>();
            dependentResult = this;
            do {
                values.add(dependentResult.getValue());
                if (count > 5) {
                    message += "\n";
                    count = 0;
                }
                dependentResult = dependentResult.getDependentResult();
                message += String.format("-> %s", dependentResult.value);
                count++;
                if (getRoot().getValue() == dependentResult.getValue()) break;
            }
            while (true);
            return message;
        }

        public String details() {
            return String.format("%s Odds %s Evens %s Total %s", this, this.numOddOperations(), this.numEvenOperations(), this.numDependents());
        }
        //        public void add(Resul)

    }
}
