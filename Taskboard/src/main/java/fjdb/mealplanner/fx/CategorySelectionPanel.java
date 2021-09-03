

package fjdb.mealplanner.fx;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CategorySelectionPanel<D, CATEGORY> extends SelectionPanel<CATEGORY> {

    private final Multimap<D, CATEGORY> tagMap;
    private ComboBox<D> dComboBox;

    public CategorySelectionPanel(Set<CATEGORY> tags, Multimap<D, CATEGORY> tagMap, Function<CATEGORY, String> labeller) {
        super(tags, labeller);
        this.tagMap = tagMap;
    }

    public void includeDishSelector() {
        includeDishSelector(FXCollections.observableList(Lists.newArrayList(tagMap.keySet())));
    }

    public void includeDishSelector(ObservableList<D> dishes) {
        dComboBox = new ComboBox<>(dishes);
        dComboBox.setOnAction(actionEvent -> {
            D selectedItem = dComboBox.getValue();
            update(selectedItem);
        });
        getChildren().add(0, dComboBox);
    }

    public D getSelectedDish() {
        return dComboBox == null ? null : dComboBox.getValue();
    }

    public void addTags(D dish, List<CATEGORY> tags) {
        tagMap.putAll(dish, tags);
    }

    public void update(D dish) {
        Collection<CATEGORY> dishTags = tagMap.get(dish);
        updateSelection(dishTags.stream().collect(Collectors.toMap(dishTag -> dishTag, dishTag -> true)));
    }

}