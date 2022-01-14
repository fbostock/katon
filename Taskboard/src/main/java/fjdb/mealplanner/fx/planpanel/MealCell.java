package fjdb.mealplanner.fx.planpanel;

import com.google.common.collect.Lists;
import fjdb.mealplanner.*;
import fjdb.mealplanner.fx.DishUtils;
import fjdb.mealplanner.fx.DragUtils;
import fjdb.mealplanner.swing.MealPlannerTest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fjdb.mealplanner.fx.DragUtils.DISH_FORMAT;
import static fjdb.mealplanner.fx.DragUtils.MEAL_FORMAT;

class MealCell extends TableCell<MealPlanPanel.DatedDayPlan, Meal> {

    private Dish currentDish;
    private TextField textField;
    private final ObservableList<Dish> dishes;
    private DishActionFactory factory;

    public MealCell(ObservableList<Dish> dishes, MealType mealType, MealPlanBuilder mealPlanBuilder, DishActionFactory factory) {
        this.dishes = dishes;
        this.factory = factory;

        setOnDragDetected(eh -> {
            // Get the row index of this cell
            Meal item = getItem();
//                if (item != null && !Dish.isStub(item.getDish())) {
            if (item != null && !Meal.isStub(item)) {
                Dragboard db = startDragAndDrop(TransferMode.ANY);
                db.setContent(DragUtils.makeContent(MEAL_FORMAT, item));
            }
        });
        setOnDragDone(dragEvent -> {
            if (dragEvent.getTransferMode() == TransferMode.COPY) {
                removeMeal(mealPlanBuilder, mealType);
            }
        });


        setOnDragOver(event -> {
            /* data is dragged over the target */
            /* accept it only if it is not dragged from the same node
             * and if it has a string data */
            if (event.getGestureSource() != MealCell.this &&
                    (event.getDragboard().hasContent(DISH_FORMAT) | event.getDragboard().hasContent(MEAL_FORMAT))) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        setOnDragDropped(event -> {
            /* data dropped */
            /* if there is a string data on dragboard, read it and use it */
            Dragboard dragboard = event.getDragboard();
            boolean hasDish = dragboard.hasContent(DISH_FORMAT);
            boolean hasMeal = dragboard.hasContent(MEAL_FORMAT);
            boolean success = false;
            if (hasDish) {
                Dish dish = DragUtils.getContent(dragboard, DISH_FORMAT);
                LocalDate date = getDate();
                mealPlanBuilder.setMeal(date, mealType, new Meal(dish, ""));
                getTableView().refresh();
                success = true;
            } else if (hasMeal) {
                Meal meal = DragUtils.getContent(dragboard, MEAL_FORMAT);
                LocalDate date = getDate();
                mealPlanBuilder.setMeal(date, mealType, meal);
                getTableView().refresh();
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);
            event.consume();
        });

        setOnMouseClicked(me -> {
            if (MouseButton.SECONDARY.equals(me.getButton())) {
                ContextMenu contextMenu = new ContextMenu();
                List<MenuItem> mealMenuItems = getMealMenuItems(mealPlanBuilder, mealType);
                if (!mealMenuItems.isEmpty()) {
                    contextMenu.getItems().addAll(mealMenuItems);
                    contextMenu.show(MealCell.this, me.getScreenX(), me.getScreenY());
                }
            }
        });
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.isControlDown()) {
                    if (KeyCode.D.equals(keyEvent.getCode())) {
                        mealPlanBuilder.setMeal(getDate().plusDays(1), mealType, getItem());
                        getTableView().refresh();
                    } else if (KeyCode.F.equals(keyEvent.getCode())) {
                        //TODO ideally, this would add a meal, not just a dish.
                        factory.getCurrentMealPlan().addDish(getItem().getDish(), getDateInNextPlan(mealPlanBuilder), mealType);
                    }
                    //TODO add controls to copy and paste meals as well.
                }
            }
        });

        itemProperty().addListener(new ChangeListener<Meal>() {
            @Override
            public void changed(ObservableValue<? extends Meal> observableValue, Meal meal, Meal t1) {
//TODO test this
                if (t1 != null) {
                    applyStyle(t1.getDish());

                }
            }
        });
    }

    private void applyStyle(Dish dish) {
        if (!Dish.isStub(dish)) {
            setStyle("-fx-text-fill: green;");
        } else {
            setStyle("-fx-text-fill: black;");
        }
    }

    private LocalDate getDate() {
        return getTableRow().getItem().getDate();
    }

    private LocalDate getDateInNextPlan(MealPlanBuilder currentPlan, LocalDate nextStart) {
        return MealPlanPanel.getDateInNextPlan(currentPlan, getDate(), nextStart);
    }

    private LocalDate getDateInNextPlan(MealPlanBuilder currentPlan) {
        return getDateInNextPlan(currentPlan, factory.getCurrentMealPlan().getStart());
    }

    private List<MenuItem> getMealMenuItems(MealPlanBuilder builder, MealType mealType) {
        List<MenuItem> list = Lists.newArrayList();
        final Meal meal = getItem();
        if (meal != null && !Meal.isStub(meal)) {
            MenuItem deleteItem = new MenuItem("Delete Meal");
            deleteItem.setOnAction(actionEvent -> removeMeal(builder, mealType));
            MenuItem addToCook = new MenuItem("Add Meal to Cook");
            addToCook.setOnAction(actionEvent -> {
                builder.addCook(getTableRow().getItem().getDate(), getName(meal));
                getTableView().refresh();
            });
            MenuItem addToUnfreeze = new MenuItem("Add Meal to Unfreeze");
            addToUnfreeze.setOnAction(actionEvent -> {
                builder.addUnfreeze(getTableRow().getItem().getDate(), getName(meal));
                getTableView().refresh();
            });
            list.add(addToCook);
            list.add(addToUnfreeze);
            list.add(deleteItem);
            Dish dish = meal.getDish();
            if (!Dish.isStub(dish)) {
                Menu dishMenu = factory.getDishMenu(dish);
                LocalDate date = getDateInNextPlan(builder);
                dishMenu.getItems().add(factory.addDishToMealPlan(dish, date, mealType));
                list.add(dishMenu);
            } else {
                List<Dish> candidates = DishUtils.getDishMatches(meal.getNotes(), dishes);
                if (candidates.size() > 0) {
                    for (Dish candidate : candidates) {
                        MenuItem dishOption = new MenuItem("Set dish to " + candidate);
                        dishOption.setOnAction(actionEvent -> {
                            Meal newMeal = new Meal(candidate, meal.getNotes());
                            builder.setMeal(getDate(), mealType, newMeal);
                            getTableView().refresh();
                        });
                        list.add(dishOption);
                    }

                }
            }
        }
        return list;
    }

    private String getName(Meal meal) {
        Dish dish = meal.getDish();
        String toCook = dish.getName();
        if (Dish.isStub(dish)) {
            toCook = meal.getNotes();
        }
        return toCook;
    }


    private void removeMeal(MealPlanBuilder mealPlanBuilder, MealType mealType) {
        TableRow<MealPlanPanel.DatedDayPlan> tableRow = getTableRow();
        MealPlanPanel.DatedDayPlan item = tableRow.getItem();
        LocalDate date = item.getDate();
        mealPlanBuilder.setMeal(date, mealType, Meal.stub());
        getTableView().refresh();
    }


    @Override
    public void startEdit() {
        Meal item = getItem();
        currentDish = item == null ? null : item.getDish();
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
        currentDish = item == null ? null : item.getDish();
        setText(getString());
        setGraphic(null);
    }

    @Override
    public void updateItem(Meal item, boolean empty) {
        super.updateItem(item, empty);
        currentDish = item == null ? null : item.getDish();
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
    /*
    TODO
     I want a cell to contain notes of a meal, without the dish title.
    When inferring a dish from a cell's contents, this would cause problems.
    We could use the currentDish field to track whether we should attempt to infer the contents.
    e.g. if it is null, we should try to infer. If it is set, then we don't.
    How would it be set? If you edit it manually, it may infer a dish. If it doesn't, you have the right-click option
    to set a "matching" dish. We would need a RC option to select ANY dish.
    If the currentDish field has been set, a user may edit the field to change the dish completely, but may
    not change the currentDish, so we would have an inconsistency.
     */

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
            if (!arg2) {
                commitEdit(getMeal(textField.getText()));
            }
        });
        //Allow Enter key to commit change and stop editing.
        textField.setOnAction(event -> {
            String text = textField.getText();
            commitEdit(getMeal(text));
            event.consume();
        });
        textField.setOnKeyPressed(keyEvent -> {
            //TODO ideally, when left or right pressed, we want to also commit the edit, AND make sure
            //the next cell has the focus. Not sure how to do that.
        });
    }

    private Meal getMeal(String text) {
        int index = text.indexOf(":");
        Meal meal;
        if (index >= 0) {
            String dishName = text.substring(0, index);
            Map<String, Dish> map = dishes.stream().collect(Collectors.toMap(Dish::getName, d -> d));
            Dish dish = map.get(dishName);
            if (dish == null) {
                meal = new Meal(MealPlannerTest.stub(), text);
            } else {
                meal = new Meal(dish, text.substring(index));
            }
        } else {
            Map<String, Dish> map = dishes.stream().collect(Collectors.toMap(dish -> dish.getName().toLowerCase(), d -> d));
            if (map.containsKey(text.toLowerCase())) {
                Dish dish = map.get(text.toLowerCase());
                meal = new Meal(dish, "");
//TODO else, for all dishes, check if any appear in the text, and if there is only one, apply that.
                //for multiples, is there a way we can add a right-click menu option to apply one of those dishes to it?
                //actually, YES-> we simply check for each dish which is a stub, find dishes that match and add the menus.
            } else {
                List<Dish> candidates = DishUtils.getDishMatches(text, map);
                if (candidates.size() == 1) {
                    meal = new Meal(candidates.get(0), text);
                } else {
//                        Dish dish = (text.isEmpty() || currentDish == null) ? MealPlannerTest.stub() : currentDish;
                    Dish dish = MealPlannerTest.stub();
                    meal = new Meal(dish, text);
                }
            }
        }
        return meal;
    }

    private String getString() {
        if (getItem() == null) {
            return "";
        } else {
            Meal item = getItem();
            if (item.getNotes().isEmpty()) {
                return String.format("%s", item.getDish());
            } else {
                //TODO should we return description (including dishname) or just notes? Feel it should be description,
                //to reflect what is shown in csv. Problem is we can't change the currentDish field easily by editing the cell.
//                    return item.getNotes();
                return item.getDescription();
            }
        }
    }
}
