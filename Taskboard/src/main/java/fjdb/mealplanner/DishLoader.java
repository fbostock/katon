package fjdb.mealplanner;

import com.google.common.collect.Lists;
import fjdb.databases.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class to load dishes from a database
 */
public class DishLoader {
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


    //TODO configure a full path to store the file somewhere and have the machinery resolve to this particular
    // directory.
    public static void main(String[] args) throws SQLException {
        DishLoader dishLoader = new DishLoader();
        List<List<Object>> lists = dishLoader.dao.debugLoad();
        for (List<Object> list : lists) {
            System.out.println(list);
        }
        //TODO save and shutdown
        //shutdown required to write table creation and inserts to file.
        //Need to check tables exist before writing them, having loaded the file in the first place.
        dishLoader.dao.shutdown();

    }

    public DishLoader() {
        DatabaseAccess access = new DatabaseAccess("Meals.sql");
        dao = new DishDao(access);
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

    private static class DishDao extends IdColumnDao<Dish> implements DaoIF<Dish> {

        public DishDao(DatabaseAccess access) {
            super(access, Columns.of());
            try {
                if (!checkTableExists()) {
                    writeTable();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public String getTableName() {
            return "DISHES";
        }

        public void insert(Dish dish) {//throws SQLException {
            //TODO when we call column.dbElement(object) from something extracted from dish, can we optionally define
            //the column with an extractor method, defined using a lambda on dish? (That way, we might be able to avoid
            //having to manually tell the machinery how to convert the Dish into db args, since the information is already stored
            //in the columns. Also, when I first wrote the columns, I think it predated lambdas anyway (e.g. JAva 7).
            super.insert(dish);
        }

        private static class Columns extends IdColumnGroup<Dish> {

            public StringColumn nameColumn = new StringColumn("NAME", "VARCHAR(256)");
            public StringColumn descriptionColumn = new StringColumn("DESCRIPTION", "VARCHAR(1024)");
            private final IdColumn<DishId> idColumn;

            public static Columns of() {
                IdColumn<DishId> idColumn = new IdColumn<>("ID", DishId::new);
                return new Columns(idColumn);
            }

            public Columns(IdColumn<DishId> idColumn) {
                super(idColumn);
                this.idColumn = idColumn;
                addColumn(nameColumn);
                addColumn(descriptionColumn);
            }

            @Override
            public Dish handle(ResultSet rs) throws SQLException {
                DishId resolve = resolve(idColumn, rs);
                return new Dish(resolve, resolve(nameColumn, rs), resolve(descriptionColumn, rs));
            }

            @Override
            public List<Object> getDataItemObjects(Dish dataItem) {
                return Lists.newArrayList(nameColumn.dbElement(dataItem.getName()), descriptionColumn.dbElement(dataItem.getDescription()));
            }
        }


    }

}
