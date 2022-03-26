package fjdb.mealplanner;

import fjdb.databases.DataId;

import java.io.Serializable;

public class DishId extends DataId implements Serializable {
    //TODO once DishId has been removed from Dish, we may be able to remove this serialization - and from parent classes as well.
    private static final long serialVersionUID = 20210720L;

    public static DishId STUB = new DishId(-1);

    public DishId(int id) {
        super(id);
    }

}
