package fjdb.databases.columns;

import fjdb.databases.DefaultId;

public class DefaultIdMaker extends IdMaker<DefaultId> {
    public DefaultIdMaker() {
        super(DefaultId.class);
    }

    @Override
    public DefaultId apply(Integer integer) {
        return new DefaultId(integer, idType);
    }


}
