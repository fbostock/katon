package fjdb.mealplanner.dao;

import com.google.common.collect.Lists;
import fjdb.databases.*;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.DishDao;
import fjdb.mealplanner.DishId;
import fjdb.mealplanner.DishTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DishTagDao extends ColumnDao<DishTagDao.TagEntry> {

    public DishTagDao(DatabaseAccess access, DishDao dishDao) {
        super(access, of(dishDao));
        //TODO put this up in the abstract class, and do the same for other daos.
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
        return "DISH_TAG_ENTRIES";
    }

    private static ColumnGroup<TagEntry> of(DishDao dishDao) {
        return new TagColumns(dishDao);
    }

    private static class TagColumns extends ColumnGroup<TagEntry> {

        IdColumn<DishId> idColumn = new IdColumn<>("id", DishId::new);
        TagColumn tagColumn = new TagColumn("TAG");
        private final DishDao dishDao;

        public TagColumns(DishDao dishDao) {
            this.dishDao = dishDao;
            addColumn(idColumn).addColumn(tagColumn);
        }

        @Override
        public TagEntry handle(ResultSet rs) throws SQLException {
            DishId dishId = resolve(idColumn, rs);
            Dish dish = dishDao.find(dishId);
            DishTag tag = resolve(tagColumn, rs);
            return new TagEntry(dish, tag);
        }

        @Override
        public List<Object> getDataItemObjects(TagEntry dataItem) {
            return Lists.newArrayList(idColumn.dbElement(dishDao.findId(dataItem.getDish())),
                    tagColumn.dbElement(dataItem.getTag()));
        }
    }

    private static class TagColumn extends AbstractColumn<DishTag, String>{

        protected TagColumn(String dbName) {
            super(dbName, "VARCHAR(256)");
        }

        @Override
        public DishTag get(ResultSet rs, int index) throws SQLException {
            return DishTag.of(rs.getString(index));
        }

        @Override
        public String dbElement(DishTag input) {
            return input.getLabel();
        }
    }
    public static class TagEntry {
        private final Dish dish;
        private final DishTag tag;

        public TagEntry(Dish dish, DishTag tag) {
            this.dish = dish;
            this.tag = tag;
        }

        public Dish getDish() {
            return dish;
        }

        public DishTag getTag() {
            return tag;
        }
    }
}
