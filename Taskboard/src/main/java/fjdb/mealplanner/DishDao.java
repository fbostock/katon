package fjdb.mealplanner;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import fjdb.databases.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO store the daos somewhere so we can reuse the caching.
public class DishDao extends IdColumnDao<Dish> implements DaoIF<Dish> {


    //TODO put appropriate locking around cache. Also, move id caching up to IdColumnDao.
//    private final Map<DishId, Dish> cache = new ConcurrentHashMap<>();
    private final BiMap<DishId, Dish> idBeanMap = HashBiMap.create();

    public static void main(String[] args) {
        DishDao dishDao = new DishDao(null);
        dishDao.insert(null);
    }
    private final Object lock = new Object();

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

    public DishId findId(Dish dish) {
        DishId dishId = idBeanMap.inverse().get(dish);
        if (dishId == null) {
            System.out.println(String.format("Inserting dish: %s", dish));
            insert(dish);
            dishId = idBeanMap.inverse().get(dish);
        }
        return dishId;
    }

    public Dish find(DishId id) {
        Dish dish = idBeanMap.get(id);
        if (dish == null) {
            cache();
            dish = idBeanMap.get(id);
        }
        return dish;
    }

    @Override
    public List<Dish> load() {
        cache();
        //TODO maybe the cache method should return values. All reads to idBeanMap at least need to be under the lock. We
        //may even want a readwrite lock mechanism.
        return Lists.newArrayList(idBeanMap.values());
    }

    @Override
    public String getTableName() {
        return "DISHES";
    }

    public void insert(Dish dish) {//throws SQLException {
        //TODO when we call column.dbElement(object) from something extracted from dish, can we optionally define
        //the column with an extractor method, defined using a lambda on dish? (That way, we might be able to avoid
        //having to manually tell the machinery how to convert the Dish into db args, since the information is already stored
        //in the columns. Also, when I first wrote the columns, I think it predated lambdas anyway (e.g. Java 7).

        synchronized (lock) {
            if (idBeanMap.inverse().get(dish) == null) {
                super.insert(dish);
                DataId id = super.findId(dish);
                idBeanMap.put((DishId) id, dish);
            }
        }
    }

    private void cache() {
        if (idBeanMap.isEmpty()) {
            synchronized (lock) {
                if (idBeanMap.isEmpty()) {
                    List<Dish> load = super.load();
                    Map<DishId, Dish> dishes = new HashMap<>();
                    load.forEach(d -> dishes.put(d.getId(), d));
                    idBeanMap.putAll(dishes);
                }
            }
        }
    }
    /*
    We want to retrieve both the id and bean details
     */


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
        public DataId handleId(ResultSet rs) throws SQLException {
            return resolve(idColumn, rs);
        }

        @Override
        public List<Object> getDataItemObjects(Dish dataItem) {
            return Lists.newArrayList(nameColumn.dbElement(dataItem.getName()), descriptionColumn.dbElement(dataItem.getDetails()));
        }
    }


}
