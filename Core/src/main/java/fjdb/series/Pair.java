package fjdb.series;

public class Pair<A, B> implements PairI<A, B> {

    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getLeft() {
        return a;
    }

    public B getRight() {
        return b;
    }

    public A first() {
        return getLeft();
    }

    public B second() {
        return getRight();
    }
}
