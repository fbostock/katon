package fjdb.mealplanner.dao;

import com.google.common.collect.Lists;
import fjdb.databases.*;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.DishDao;
import fjdb.mealplanner.DishId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DishHistoryDao extends ColumnDao<DishHistoryDao.DishEntry> {

    private final DishDao dishDao;

    public DishHistoryDao(DatabaseAccess access, DishDao dishDao) {
        super(access, of(dishDao));
        try {
            if (!checkTableExists()) {
                writeTable();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        this.dishDao = dishDao;
    }

    //TODO this actually requires a different type of columns - one that doesn't use Ids, as the table will contain
    //multiple entries for each dish, differing by date the dish was had.


    @Override
    public String getTableName() {
        return "DISH_HISTORY";
    }

    private static ColumnGroup<DishEntry> of(DishDao dishDao) {
        return new HistoryColumnGroup(dishDao);
    }

    public static class DishEntry {
        private final Dish dish;
        private final LocalDate date;

        public DishEntry(Dish dish, LocalDate date) {
            this.date = date;
            this.dish = dish;
        }

        public Dish getDish() {
            return dish;
        }

        public LocalDate getDate() {
            return date;
        }
    }

    private static class HistoryColumnGroup extends ColumnGroup<DishEntry> {
        IdColumn<DishId> idColumn = new IdColumn<>("id", DishId::new);
        DateColumn dateColumn = new DateColumn("mydate");
        private final DishDao dishDao;

        public HistoryColumnGroup(DishDao dishDao) {
            this.dishDao = dishDao;
            addColumn(idColumn);
            addColumn(dateColumn);
        }

        @Override
        public DishEntry handle(ResultSet rs) throws SQLException {
            DishId dishId = resolve(idColumn, rs);
            Dish dish = dishDao.find(dishId);
            LocalDate date = resolve(dateColumn, rs);
            return new DishEntry(dish, date);
        }

        @Override
        public List<Object> getDataItemObjects(DishEntry dataItem) {
            Dish dish = dataItem.getDish();
            //TODO use the DishDao to get the id from the Dish
            DishId id = dishDao.findId(dish);
//            DishId id = dish.getId();
            return Lists.newArrayList(idColumn.dbElement(id), dateColumn.dbElement(dataItem.getDate()));
        }
    }
}
