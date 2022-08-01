package fjdb.mealplanner;

import com.google.common.collect.Lists;
import fjdb.databases.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DishDao extends IdColumnDao<Dish, DishId> implements DaoIF<Dish> {

    private static final Logger log = LoggerFactory.getLogger(DishDao.class);

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

    /**
     * Overridden to insert if Dish not found.
     */
    public DishId findId(Dish dish) {
        DishId dishId = super.findId(dish);
        if (dishId == null) {
            log.info("Inserting dish {}", dish);
            insert(dish);
        }
        return dishId;
    }

    public Dish find(DishId id) {
        Dish dish = idBeanMap.get(id);
        if (dish == null) {
            super.load();
            dish = idBeanMap.get(id);
        }
        return dish;
    }

    @Override
    public List<Dish> load() {
        if (idBeanMap.isEmpty()) {
            super.load();
        }
        return Lists.newArrayList(idBeanMap.values());
    }

    @Override
    public String getTableName() {
        return "DISHES";
    }

    //TODO when we call column.dbElement(object) from something extracted from dish, can we optionally define
    //the column with an extractor method, defined using a lambda on dish? (That way, we might be able to avoid
    //having to manually tell the machinery how to convert the Dish into db args, since the information is already stored
    //in the columns. Also, when I first wrote the columns, I think it predated lambdas anyway (e.g. Java 7).

    public void insert(Dish dish) {//throws SQLException {
        super.insert(dish);
//        synchronized (lock) {
//            if (localIdBeanMap.inverse().get(dish) == null) {
//                super.insert(dish);
//                DataId id = super.findId(dish);
//                localIdBeanMap.put((DishId) id, dish);
//            }
//        }
    }

    /*
    TODO Check that the dish objects are all cached in the IdColumnDao.idBeanMap. Then remove this local one.
    The super.insert method will need to add the new dataItem to the cache. After doing the insertion, it will need
    to do findId(dataItem) (at the parent level above any caching) to get the id, then populate the cache with the id.
     */
//    private void cache() {
//        if (localIdBeanMap.isEmpty()) {
//            synchronized (lock) {
//                if (localIdBeanMap.isEmpty()) {
//                    List<Dish> load = super.load();
//                    Map<DishId, Dish> dishes = new HashMap<>();
//                    load.forEach(d -> dishes.put(d.getId(), d));
//                    localIdBeanMap.putAll(dishes);
//                }
//            }
//        }
//        System.out.println();
//    }
    /*
    We want to retrieve both the id and bean details
     */


    private static class Columns extends IdColumnGroup<Dish, DishId> {

        public StringColumn nameColumn = new StringColumn("NAME", "VARCHAR(256)");
        public StringColumn descriptionColumn = new StringColumn("DESCRIPTION", "VARCHAR(1024)");
        private final IdColumn<DishId> idColumn;

        public static Columns of() {
            IdColumn<DishId> idColumn = new IdColumn<>("ID", DishId::new, DishId.class);
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
//            DishId resolve = resolve(idColumn, rs);
//            return new Dish(resolve, resolve(nameColumn, rs), resolve(descriptionColumn, rs));
            return new Dish(resolve(nameColumn, rs), resolve(descriptionColumn, rs));
        }

        @Override
        public DishId handleId(ResultSet rs) throws SQLException {
            return resolve(idColumn, rs);
        }

        @Override
        public List<Object> getDataItemObjects(Dish dataItem) {
            return Lists.newArrayList(nameColumn.dbElement(dataItem.getName()), descriptionColumn.dbElement(dataItem.getDetails()));
        }
    }


}
