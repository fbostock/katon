package fjdb.mealplanner;

import fjdb.databases.DatabaseAccess;
import fjdb.mealplanner.dao.DishHistoryDao;
import fjdb.mealplanner.dao.DishTagDao;

/**
 * a Class to hold and store all access to Daos
 */
public class DaoManager {

    private final DishDao dishDao;
    private final DishHistoryDao dishHistoryDao;
    private final DishTagDao dishTagDao;

    public static DaoManager PRODUCTION = new DaoManager(new DatabaseAccess("Meals.sql"));

    private DaoManager(DatabaseAccess access) {
        dishDao = new DishDao(access);
        dishHistoryDao = new DishHistoryDao(access, dishDao);
        dishTagDao = new DishTagDao(access, dishDao);
        //TODO I think this class should be responsible for checking if the dao tables exist, rather than the dao
        //being responsible for checking. The dao provides the means to check, but shouldn't internally do that...perhaps.
    }

    public DishDao getDishDao() {
        return dishDao;
    }

    public DishHistoryDao getDishHistoryDao() {
        return dishHistoryDao;
    }

    public DishTagDao getDishTagDao() {
        return dishTagDao;
    }
}
