package fjdb.maths.collatz;

public interface ResultFactory<T extends Result> {

    public T makeResult(long value);
}
