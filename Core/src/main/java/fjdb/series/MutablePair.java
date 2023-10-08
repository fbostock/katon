package fjdb.series;

public class MutablePair<A, B> implements PairI<A, B> {

    private A first;
    private B second;

    public MutablePair(A first, B second) {
        this.first = first;
        this.second = second;
    }


    @Override
    public A first() {
        return first;
    }

    @Override
    public B second() {
        return second;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public MutablePair<A, B> setBoth(A first, B second) {
        setFirst(first);
        setSecond(second);
        return this;
    }
}
