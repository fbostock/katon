package fjdb.mealplanner.fx;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.DishTag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

public class FilterPanel extends VBox {

    private final List<DishTag> selectedTags;
    private final Set<DishTag> tags = new HashSet<>();
    private final Multimap<Dish, DishTag> tagMap;
    private final List<FilterListener> listeners = Lists.newArrayList();
    FlowPane unselectedBox = new FlowPane(Orientation.HORIZONTAL);
    FlowPane selectedBox = new FlowPane(Orientation.HORIZONTAL);
    Map<DishTag, FilterButton> buttons = new HashMap<>();


    public FilterPanel(Set<DishTag> tags, Multimap<Dish, DishTag> tagMap) {

        getChildren().add(new Text("Unused Tags"));
        getChildren().add(unselectedBox);
        getChildren().add(new Text("Selected Tags"));
        getChildren().add(selectedBox);
        this.tagMap = tagMap;
        selectedTags = Lists.newArrayList();
        this.tags.addAll(tags);

        for (DishTag tag : tags) {
            FilterButton button = new FilterButton(tag);
            buttons.put(tag, button);
            unselectedBox.getChildren().add(button);
        }

    }

    public List<DishTag> getSelectedTags() {
        return selectedTags;
    }

    public void update(Dish dish) {
        unselectedBox.getChildren().clear();
        selectedBox.getChildren().clear();

        Collection<DishTag> appliedTags = tagMap.get(dish);
        for (DishTag dishTag : tags) {
            buttons.get(dishTag).select(appliedTags.contains(dishTag));
        }

    }

    public void addTags(Dish dish, List<DishTag> tags) {
        tagMap.putAll(dish, tags);
    }

    public void addListener(FilterListener listener) {
        listeners.add(listener);
    }

    public void fireListeners() {
        for (FilterListener listener : listeners) {
            listener.filterChanged();
        }
    }

    public void removeListener(FilterListener listener) {
        listeners.remove(listener);
    }

    private class FilterButton extends Button {
        private boolean selected = false;
        private final DishTag tag;

        public FilterButton(DishTag tag) {
            super(tag.getLabel());
            this.tag = tag;
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    updateControls(selected);
                    selected = !selected;
                    //TODO do the reverse, e.g. if the button is pressed while in the selected row, the tag gets removed.
                }
            });
        }

        public void select(boolean select) {
            selected = select;
            updateControls(!selected);
        }


        private void updateControls(boolean isSelected) {
            if (isSelected) {
                selectedBox.getChildren().remove(FilterButton.this);
                unselectedBox.getChildren().add(FilterButton.this);
                selectedTags.remove(tag);
            } else {
                unselectedBox.getChildren().remove(FilterButton.this);
                selectedBox.getChildren().add(FilterButton.this);
                selectedTags.add(tag);
            }
            fireListeners();
        }

    }

    public interface FilterListener {
        void filterChanged();
    }
}
