package fjdb.mealplanner.fx;

import com.google.common.collect.Lists;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;
import java.util.function.Function;

public class SelectionPanel<T> extends VBox {

    /*
    TODO refactor this to make it generic.
    There are two levels to this component:
    The first is it has two selections of buttons, "Selected" and "Unselected". Clicking on these moves the buttons from
    one area to the other, where each button corresponds to an underlying entity.
    The second is the ability to modify the underlying collection of entities based on some additional quantity. Here, the DishTags
    are the entities, and the factor is Dish.

    I want a component that yields just the first, such that I can retrieve the selected collection of entities, and register listeners
    to update to filter changes.
    Secondly, I want a component that provides pre-selection by the additional factor.

     */

    private final List<T> selectedItems;
    private final List<SelectionListener> listeners = Lists.newArrayList();
    FlowPane unselectedBox = new FlowPane(Orientation.HORIZONTAL);
    FlowPane selectedBox = new FlowPane(Orientation.HORIZONTAL);
    Map<T, FilterButton> buttons = new HashMap<>();
    private final Function<T, String> labeller;

    public SelectionPanel(Set<T> items, Function<T, String> labeller) {
        this.labeller = labeller;

        getChildren().add(new Text("Available Options"));
        getChildren().add(unselectedBox);
        getChildren().add(new Text("Selected"));
        getChildren().add(selectedBox);
        selectedItems = Lists.newArrayList();

        for (T item : new HashSet<>(items)) {
            FilterButton button = new FilterButton(item);
            buttons.put(item, button);
            unselectedBox.getChildren().add(button);
        }

    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }

    public void updateSelection(Map<T, Boolean> selected) {
        unselectedBox.getChildren().clear();
        selectedBox.getChildren().clear();
        for (Map.Entry<T, FilterButton> entry : buttons.entrySet()) {
            Boolean select = selected.get(entry.getKey());
            select = select != null && select;
            entry.getValue().select(select);
        }
    }

    public void addListener(SelectionListener listener) {
        listeners.add(listener);
    }

    public void fireListeners() {
        for (SelectionListener listener : listeners) {
            listener.selectionChanged();
        }
    }

    public void removeListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    private class FilterButton extends Button {
        private boolean selected = false;
        private final T item;

        public FilterButton(T item) {
            super(labeller.apply(item));
            this.item = item;
            setOnAction(actionEvent -> {
                updateControls(selected, this);
                selected = !selected;
            });
        }

        public void select(boolean select) {
            selected = select;
            updateControls(!selected, this);
        }

    }

    private void updateControls(boolean isSelected, FilterButton button) {
        if (isSelected) {
            selectedBox.getChildren().remove(button);
            unselectedBox.getChildren().add(button);
            selectedItems.remove(button.item);
        } else {
            unselectedBox.getChildren().remove(button);
            selectedBox.getChildren().add(button);
            selectedItems.add(button.item);
        }
        fireListeners();
    }


    public interface SelectionListener {
        void selectionChanged();
    }

}
