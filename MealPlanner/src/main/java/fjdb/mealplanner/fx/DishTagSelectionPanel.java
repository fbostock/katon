package fjdb.mealplanner.fx;

import com.google.common.collect.Multimap;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.DishTag;

import java.util.Set;

public class DishTagSelectionPanel extends CategorySelectionPanel<Dish, DishTag> {

    public DishTagSelectionPanel(Set<DishTag> tags, Multimap<Dish, DishTag> tagMap) {
        super(tags, tagMap, DishTag::getLabel);
    }

}