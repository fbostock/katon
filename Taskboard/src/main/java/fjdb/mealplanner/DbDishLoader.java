package fjdb.mealplanner;

import fjdb.mealplanner.loaders.DishLoader;

import java.util.List;

/**
 * Class to load dishes from a database
 */
public class DbDishLoader implements DishLoader {
    private final DishDao dao;

    /*
    TODO list
    - Add method to delete entry from DishDao.
    -
    - Create a column dao structure for a table without a primary id - e.g. for storing ingredients for a dish where the table may contain a dish id and
    an ingredient id.
    -
    - (having created a table for Meals) configure the path so that the working directory can be a folder on the desktop
    or anywhere else.
    -
    - For a Dao built using Column objects, require default machinery to generate JTables in which we can edit the data in the
    db tables, rather than have to manually configure stuff for every table we make.
    -
    - a mechanism for modifying tables. e.g. if I want to add a new column to a table, we need a way to "upgrade"
    the existing tables, e.g. to add a new column with default values. To be done on as as-is basis (an advanced mechanism
    would be to have a mechanism which upgrades db tables based on a version number for the application, as we did
    at ikon.
    - a mechanism for determining what tables/daos should be persisted, and which should be just in memory. e.g.
    there may be some things which just want to exist in temporary tables rather than get persisted outside the application
    (or possibly not - i.e. if they are only temporary, why store them in db tables at all? For now, consider this low priority
    or disregard entirely unless we have a use case.
    - HSQL persistance: are there other methods rather than an sql file? e.g. some binary object it can manipulate in memory


     */


    public DbDishLoader(DaoManager daoManager) {
        dao = daoManager.getDishDao();
    }

    public DishDao getDao() {
        return dao;
    }

    @Override
    public List<Dish> getDishes() {
        return loadDishes();
    }

    public List<Dish> loadDishes() {
        return dao.load();
    }

    public void addDish(Dish dish) {
        try {
            dao.insert(dish);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateDish(Dish dish) {
        dao.update(dish);
    }

}
