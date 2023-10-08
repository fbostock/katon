package fjdb.series;

public interface PairI<A, B> {
    A first();

    B second();

    default A left() {
        return first();
    }

    default B right() {
        return second();
    }

}
