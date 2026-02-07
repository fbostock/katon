package fjdb.mealplanner.fx.planpanel;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.Meal;
import fjdb.mealplanner.MealPlanBuilder;
import fjdb.mealplanner.fx.DragUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Set;

import static fjdb.mealplanner.fx.DragUtils.DISH_FORMAT;
import static fjdb.mealplanner.fx.DragUtils.MEAL_FORMAT;

public class DishHolderPanel extends FlowPane {

//    private final MealPlanPanel mealPlanPanel;
    private final MealPlanBuilder mealPlanBuilder;
    private final HBox dishListBox;

    public void clear() {
        mealPlanBuilder.clearTempDishes();
        dishListBox.getChildren().clear();
    }

    class DishButton extends Button {
        private final Meal meal;

        public DishButton(Dish dish) {
            this(new Meal(dish, ""));
        }

        public DishButton(Meal dish) {
            super(dish.getDescription());
            this.meal = dish;
            mealPlanBuilder.addTempDish(dish);
            setOnAction(actionEvent -> {
                remove();
            });
            setTooltip(new Tooltip("Click to remove"));
            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Dragboard db = startDragAndDrop(TransferMode.COPY);
                    db.setContent(DragUtils.makeContent(MEAL_FORMAT, dish));
                }
            });
            setOnDragDone(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    /* the drag and drop gesture ended */
                    /* if the data was successfully moved, clear it */
                    if (event.getTransferMode() == TransferMode.COPY) {
                        remove();
                    }
                    event.consume();
                }
            });
        }

        private void remove() {
            mealPlanBuilder.removeTempDish(meal);
            dishListBox.getChildren().remove(DishButton.this);
        }
    }

    public DishHolderPanel(MealPlanBuilder mealPlanBuilder) {
        this.mealPlanBuilder = mealPlanBuilder;
        FlowPane flowPane = this;
        flowPane.setStyle("-fx-border-color: black");
        VBox vBox = new VBox();
        dishListBox = new HBox();
        Text text = new Text("DishList");


        flowPane.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                /* show to the user that it is an actual gesture target */
                boolean hasDish = event.getDragboard().hasContent(DISH_FORMAT);
                boolean hasMeal = event.getDragboard().hasContent(MEAL_FORMAT);
                if (event.getGestureSource() != flowPane && (hasDish || hasMeal)) {
                    flowPane.setStyle("-fx-border-color: green");
                }
                event.consume();
            }
        });
        flowPane.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */
            flowPane.setStyle("-fx-border-color: black");
            event.consume();
        });

        flowPane.setOnDragOver(event -> {
            /* data is dragged over the target */
            /* accept it only if it is not dragged from the same node
             * and if it has a string data */
            if (event.getGestureSource() != flowPane && event.getGestureSource().getClass() != DishButton.class &&
                    (event.getDragboard().hasContent(DISH_FORMAT) || event.getDragboard().hasContent(MEAL_FORMAT))) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });


        for (Meal tempDish : mealPlanBuilder.getTempMeals()) {
            addMeal(tempDish);
        }

        flowPane.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data dropped */
                /* if there is a string data on dragboard, read it and use it */
                System.out.println("Dropping");
                Dragboard dragboard = event.getDragboard();
                boolean hasDish = dragboard.hasContent(DISH_FORMAT);
                boolean hasMeal = dragboard.hasContent(MEAL_FORMAT);
                boolean success = false;
                if (hasDish) {
                    Dish dish = DragUtils.getContent(dragboard, DISH_FORMAT);
                    addDish(dish);
                    success = true;
                } else if (hasMeal) {
                    Meal content = DragUtils.getContent(dragboard, MEAL_FORMAT);
                    //TODO allow meals to be stored here as well. Either store two lists, or just meals.
                    addMeal(content);
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);
                event.consume();
            }
        });
        vBox.getChildren().add(text);
        vBox.getChildren().add(dishListBox);
        flowPane.getChildren().add(vBox);


    }

    public void addDish(Dish tempDish) {
        dishListBox.getChildren().add(new DishButton(tempDish));
    }

    public void addMeal(Meal tempMeal) {
        dishListBox.getChildren().add(new DishButton(tempMeal));
    }

    public Set<Meal> getMeals() {
        return mealPlanBuilder.getTempMeals();
    }

}
