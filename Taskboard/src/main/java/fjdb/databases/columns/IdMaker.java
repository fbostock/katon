package fjdb.databases.columns;

import fjdb.databases.DataId;

import java.util.function.Function;

public abstract class IdMaker <I extends DataId> implements Function<Integer, I> {

    protected Class<I> idType;

    public IdMaker(Class<I> type) {
        this.idType = type;
    }

    @Override
    public abstract I apply(Integer integer);

    public Class<I> getIdType() {
        return idType;
    }
}
