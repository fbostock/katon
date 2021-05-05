package fjdb.mealplanner;

import fjdb.databases.DataId;

public class DishId extends DataId {

    public static DishId STUB = new DishId(-1);

    public DishId(int id) {
        super(id);
    }

}
