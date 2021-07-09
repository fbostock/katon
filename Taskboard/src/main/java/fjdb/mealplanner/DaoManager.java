package fjdb.mealplanner;

import fjdb.databases.DatabaseAccess;
import fjdb.mealplanner.dao.DishHistoryDao;

/**
 * a Class to hold and store all access to Daos
 */
public class DaoManager {

    private final DishDao dishDao;
    private final DishHistoryDao dishHistoryDao;

    public static DaoManager PRODUCTION = new DaoManager(new DatabaseAccess("Meals.sql"));

    private DaoManager(DatabaseAccess access) {
        dishDao = new DishDao(access);
        dishHistoryDao = new DishHistoryDao(access, dishDao);
        //TODO I think this class should be responsible for checking if the dao tables exist, rather than the dao
        //being responsible for checking. The dao provides the means to check, but shouldn't internally do that...perhaps.
    }

    public DishDao getDishDao() {
        return dishDao;
    }

    public DishHistoryDao getDishHistoryDao() {
        return dishHistoryDao;
    }
}
