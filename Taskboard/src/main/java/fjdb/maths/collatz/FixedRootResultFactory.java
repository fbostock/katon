package fjdb.maths.collatz;

import java.util.concurrent.atomic.AtomicInteger;

public class FixedRootResultFactory implements ResultFactory<FixedRootResultFactory.ResultWithRoot> {

    private long rootValue;

    public FixedRootResultFactory(long rootValue) {
        this.rootValue = rootValue;
    }

    public ResultWithRoot makeResult(long value) {
        return new ResultWithRoot(value);
    }

    class ResultWithRoot implements Result<ResultWithRoot> {
        private final long value;

        //method to total number of 3n+1 operations
        //method to total number of steps to 1

        private ResultWithRoot dependentResult;

        public ResultWithRoot(long value) {
            if (value < 0) {
                throw new RuntimeException("");
            }
            this.value = value;
        }

        public void setDependentResult(ResultWithRoot result) {
            dependentResult = result;
        }

        public long getValue() {
            return value;
        }

        public ResultWithRoot getDependentResult() {
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
            int count = isEven() ? 1 : 0;
            if (value == rootValue) {
                return count;
            } else {
                return count + dependentResult.numEvenOperations();
            }
        }

        public int numEvenOperations() {
            if (evens.get() < 0) {
                evens.set(calcEvens());
            }
            return evens.get();
        }

        private int calcOdds() {
            int count = isEven() ? 0 : 1;
            if (value == rootValue) {
                return 0;
            } else {
                return count + dependentResult.numOddOperations();
            }
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
            if (value == rootValue) {
                return 0;
            }
            return 1 + dependentResult.numDependents();
        }

        public int numDependents() {
            if (numDepends.get() < 0) {
                numDepends.set(calcNumDependents());
            }
            return numDepends.get();
        }


        @Override
        public boolean isDependent(ResultWithRoot other) {
            if (this.value == other.value) return true;
            while(rootValue != getDependentResult().getValue()) {
                return getDependentResult().isDependent(other);
            }
            return false;
        }

        public String printout() {
            String message = String.format("%s", value);
            int count = 1;
            ResultWithRoot dependentResult;
            dependentResult = this;
            do {
                if (count > 5) {
                    message += "\n";
                    count = 0;
                }
                dependentResult = dependentResult.getDependentResult();
                message += String.format("-> %s", dependentResult.value);
                count++;
            }
            while (dependentResult.value != rootValue);
            return message;
        }

        @Override
        public ResultWithRoot getRoot() {
            ResultWithRoot dependentResult = getDependentResult();
            while(true) {
                if (dependentResult.getValue() ==rootValue) {
                    return dependentResult;
                }
                dependentResult = dependentResult.getDependentResult();
            }
        }

        public String details() {
            return String.format("%s Odds %s Evens %s Total %s", this, this.numOddOperations(), this.numEvenOperations(), this.numDependents());
        }
        //        public void add(Resul)

    }
}
