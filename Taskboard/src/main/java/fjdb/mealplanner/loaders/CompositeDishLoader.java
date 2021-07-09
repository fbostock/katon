package fjdb.mealplanner.loaders;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.DbDishLoader;
import fjdb.mealplanner.Dish;

import java.util.List;
import java.util.Set;

public class CompositeDishLoader implements DishLoader {

    private DaoManager daoManager;

    public CompositeDishLoader(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    @Override
    public List<Dish> getDishes() {
        DbDishLoader dbDishLoader = new DbDishLoader(daoManager);
        InbuiltDishLoader inbuiltDishLoader = new InbuiltDishLoader();
        Set<Dish> dishes = Sets.newTreeSet();
        dishes.addAll(dbDishLoader.getDishes());
        dishes.addAll(inbuiltDishLoader.getDishes());
        //TODO should the db shutdown?...no, but only because the responsibility should not lay here.
        return Lists.newArrayList(dishes);
    }
}
