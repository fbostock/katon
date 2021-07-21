package fjdb.mealplanner.fx;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.Meal;
import fjdb.mealplanner.MealType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class MealEditingCell extends TableCell<MealPlanPanel.DatedDayPlan, Meal> {

    private TextField textField;
    //TODO replace with constant stub?
    private Dish currentDish = new Dish("", "");
    private final MealType type;
    private List<Dish> dishes;

    public MealEditingCell(MealType type, List<Dish> dishes) {
        this.type = type;
        this.dishes = dishes;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.requestFocus();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        Meal item = getItem();
        //TODO reset the display field to the item?
//            setText((String) getItem());
        setGraphic(null);
    }


    @Override
    public void updateItem(Meal item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private Meal getMeal() {
        Dish dish = currentDish;
        String text = textField.getText();

        //TODO add notes
        return new Meal(dish,"");
    }

    private void createTextField() {
        textField = new DishSelector(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean arg1, Boolean arg2) {
                if (!arg2) {
                    commitEdit(getMeal());
                }
            }
        });
        //Allow Enter key to commit change and stop editing.
        textField.setOnAction(event -> {
            commitEdit(getMeal());
            event.consume();
        });
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();
                if (KeyCode.DOWN.equals(code)) {

                } else if (KeyCode.UP.equals(code)) {
                } else if (KeyCode.RIGHT.equals(code)) {
                } else if (KeyCode.LEFT.equals(code)) {

                } else {
                    String text = textField.getText();
                    Searcher<Dish> dishSearcher = new Searcher<>(dishes);
                    List<Dish> results = dishSearcher.results(text);
                    //TODO add a floating panel containing the results, and when selected should update the currentDish
                    //and commit the edit.
                }
                //TODO ideally, when left or right pressed, we want to also commit the edit, AND make sure
                //the next cell has the focus. Not sure how to do that.
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }

    private static class DishSelector extends TextField {

        public DishSelector(String text) {
            super(text);
            //TODO add dishField
        }
    }
}
