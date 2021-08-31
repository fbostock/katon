package fjdb.mealplanner.fx;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fjdb.mealplanner.*;
import fjdb.mealplanner.dao.DishTagDao;
import fjdb.mealplanner.swing.MealPlannerTest;
import fjdb.util.ListUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static fjdb.mealplanner.fx.DragUtils.DISH_FORMAT;
import static fjdb.mealplanner.fx.DragUtils.MEAL_FORMAT;

public class MealPlanPanel extends FlowPane {

    private static final Logger log = LoggerFactory.getLogger(MealPlanPanel.class);

    private final MealPlanBuilder mealPlanBuilder;
    private final ObservableList<Dish> dishList;
    private static final double PREFERRED_COL_WIDTH = 150.0;

    public MealPlanPanel(MealPlanConfigurator.Configuration configuration, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this(new MealPlanBuilder(), configuration.getDate(), configuration.getDays(), dishList, mealPlanManager);
    }

    public MealPlanPanel(MealPlan mealPlan, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this(new MealPlanBuilder(mealPlan), mealPlan.getStart(), mealPlan.getDates().size(), dishList, mealPlanManager);
    }

    private MealPlanPanel(MealPlanBuilder builder, LocalDate startDate, int days, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this.dishList = dishList;
        mealPlanBuilder = builder;

        TableView<DatedDayPlan> dayPlansTable = new TableView<>();
        dishList.addListener((ListChangeListener<Dish>) change -> dayPlansTable.refresh());

        DishListener dishListener;
        dishListener = dish -> {
        };

        /* TODO
What is the best way to know the properties of the selected cell e.g. whether it's dinner or lunch, whcih day etc.
                 */
        /*dishListener = dish -> {
            ObservableList<TablePosition> selectedCells = dayPlansTable.getSelectionModel().getSelectedCells();
            for (TablePosition selectedCell : selectedCells) {

                DatedDayPlan selectedItem = dayPlansTable.getSelectionModel().getSelectedItem();
                LocalDate date = selectedItem.getDate();
                //TODO replace magic constants.
                if (selectedCell.getColumn() == 3) {
                    mealPlanBuilder.setBreakfast(date, new Meal(dish, ""));
                } else if (selectedCell.getColumn() == 4) {
                    mealPlanBuilder.setLunch(date, new Meal(dish, ""));
                } else if (selectedCell.getColumn() == 5) {
                    mealPlanBuilder.setDinner(date, new Meal(dish, ""));
//                } else if (selectedCell.getColumn() == 6) {//TODO remove once done testing
//                    mealPlanBuilder.setDinner(date, new Meal(dish, ""));
                }
                dayPlansTable.refresh();
            }

        };*/
        LocalDate endDate = startDate.plusDays(days - 1);
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            DayPlanIF dayPlan = mealPlanBuilder.getDayPlan(date);
            dayPlansTable.getItems().add(new DatedDayPlan(date, dayPlan));
            date = date.plusDays(1);
        }

        dayPlansTable.setEditable(true);
        //required to show individual cells highlighted on their own, rather than the whole row:
        dayPlansTable.getSelectionModel().setCellSelectionEnabled(true);

        TableColumn<DatedDayPlan, String> dateColumn = new TableColumn<>("Date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E yyyyMMdd");
        dateColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> formatter.format(x.getValue().getDate())));

        Callback<TableColumn<DatedDayPlan, String>, TableCell<DatedDayPlan, String>> cellFactory = p -> new EditingCell();

        TableColumn<DatedDayPlan, String> unfreeze = new TableColumn<>("Unfreeze");
        unfreeze.setPrefWidth(PREFERRED_COL_WIDTH);
        TableColumn<DatedDayPlan, String> cook = new TableColumn<>("Cook");
        unfreeze.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDayPlan().getUnfreeze()));
        cook.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().dayPlan.getToCook()));
        cook.setPrefWidth(PREFERRED_COL_WIDTH);

        unfreeze.setEditable(true);
        unfreeze.setCellFactory(cellFactory);
        unfreeze.setOnEditCommit(t -> mealPlanBuilder.setUnfreeze(t.getRowValue().getDate(), t.getNewValue()));

        cook.setEditable(true);//TODO check we need this
        cook.setCellFactory(cellFactory);
        cook.setOnEditCommit(t -> mealPlanBuilder.setCook(t.getRowValue().getDate(), t.getNewValue()));
        dayPlansTable.getColumns().addAll(dateColumn, unfreeze, cook);

        TableColumn<DatedDayPlan, Meal> breakfastMeal = makeMealColumn("Breakfast", dayPlansTable, MealType.BREAKFAST);
        TableColumn<DatedDayPlan, Meal> lunchMeal = makeMealColumn("Lunch", dayPlansTable, MealType.LUNCH);
        TableColumn<DatedDayPlan, Meal> dinnerMeal = makeMealColumn("Dinner", dayPlansTable, MealType.DINNER);

        dayPlansTable.getColumns().addAll(breakfastMeal, lunchMeal, dinnerMeal);
        dayPlansTable.getColumns().forEach(c -> c.setSortable(false));

        dayPlansTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (KeyCode.DELETE.equals(keyEvent.getCode()) || KeyCode.BACK_SPACE.equals(keyEvent.getCode())) {
                    DatedDayPlan selectedItem = dayPlansTable.getSelectionModel().getSelectedItem();
                    LocalDate date = selectedItem.getDate();
                    ObservableList<TablePosition> selectedCells = dayPlansTable.getSelectionModel().getSelectedCells();
                    for (TablePosition selectedCell : selectedCells) {
                        TableColumn tableColumn = selectedCell.getTableColumn();
                        if (tableColumn instanceof MealColumn) {
                            MealColumn column = (MealColumn) tableColumn;
                            MealType mealType = column.mealType;
                            mealPlanBuilder.setMeal(date, mealType, Meal.stub());
                        }
                    }
                    dayPlansTable.refresh();
                }
            }
        });

        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Add extra date");
        cm.getItems().add(mi1);
        mi1.setOnAction(actionEvent -> {
            List<LocalDate> dates = mealPlanBuilder.getDates();
            LocalDate endDate1 = ListUtil.last(dates).plusDays(1);
            DayPlanIF dayPlan = mealPlanBuilder.getDayPlan(endDate1);
            dayPlansTable.getItems().add(new DatedDayPlan(endDate1, dayPlan));
        });
        MenuItem mi2 = new MenuItem("Remove first date");
        cm.getItems().add(mi2);
        mi2.setOnAction(actionEvent -> {
            LocalDate firstDate = ListUtil.first(mealPlanBuilder.getDates());
            mealPlanBuilder.remove(firstDate);
            dayPlansTable.getItems().remove(0);
        });
        MenuItem mi3 = new MenuItem("Remove last date");
        cm.getItems().add(mi3);
        mi3.setOnAction(actionEvent -> {
            LocalDate lastDate = ListUtil.last(mealPlanBuilder.getDates());
            mealPlanBuilder.remove(lastDate);
            dayPlansTable.getItems().remove(dayPlansTable.getItems().size() - 1);
        });
        dayPlansTable.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                cm.show(dayPlansTable, t.getScreenX(), t.getScreenY());
            }
        });

        Button makePlan = new Button("Make MealPlan");
        makePlan.setOnAction(actionEvent -> {
            MealPlan mealPlan = mealPlanBuilder.makePlan();
            mealPlanManager.addMealPlan(mealPlan);
        });
        Button csvPlan = new Button("Create CSV");
        csvPlan.setOnAction(actionEvent -> {
            MealPlan mealPlan = mealPlanBuilder.makePlan();
            mealPlanManager.toCSV(mealPlan);
            try {
                Runtime.getRuntime().exec("open " + mealPlanManager.getDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button print = new Button("print meals");
        print.setOnAction(actionEvent -> {
            MealPlan mealPlan = mealPlanBuilder.makePlan();
            List<LocalDate> dates = mealPlan.getDates();
            for (LocalDate localDate : dates) {
                DayPlanIF plan = mealPlan.getPlan(localDate);
                Meal dinner = plan.getDinner();
                Dish dish = dinner.getDish();
                String notes = dinner.getNotes();
                if (!notes.isEmpty() && !notes.contains(dish.getName())) {
                    System.out.printf("%s DINNER %s %s%n", localDate, dish, notes);
                }
                dinner = plan.getLunch();
                dish = dinner.getDish();
                notes = dinner.getNotes();
                if (!notes.isEmpty() && !notes.contains(dish.getName())) {
                    System.out.printf("%s LUNCH %s %s%n", localDate, dish, notes);
                }
            }
        });

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.getChildren().add(dayPlansTable);
        flowPane.getChildren().add(getDishSidePane(dishListener));
        getChildren().add(flowPane);
        getChildren().add(makePlan);
        getChildren().add(csvPlan);
        getChildren().add(print);
        getChildren().add(makeDishHolderPanel());
        getChildren().add(makeNotesPanel());

    }

    private static class MealColumn extends TableColumn<DatedDayPlan, Meal> {
        private final MealType mealType;

        public MealColumn(String s, MealType mealType) {
            super(s);
            this.mealType = mealType;
        }
    }

    private TableColumn<DatedDayPlan, Meal> makeMealColumn(String label, TableView<DatedDayPlan> dayPlansTable, MealType mealType) {
        TableColumn<DatedDayPlan, Meal> dinnerMeal = new MealColumn(label, mealType);
        dinnerMeal.setCellValueFactory(x -> Bindings.createObjectBinding(() -> {
            DatedDayPlan value = x.getValue();
            return value.getMeal(mealType);
        }));
        dinnerMeal.setEditable(true);
        dinnerMeal.setOnEditCommit(t -> {
            mealPlanBuilder.setMeal(t.getRowValue().getDate(), mealType, t.getNewValue());
            dayPlansTable.refresh();
        });

        Callback<TableColumn<DatedDayPlan, Meal>, TableCell<DatedDayPlan, Meal>> mealCellFactory = p -> new MealCell(dishList, mealType, mealPlanBuilder);

        dinnerMeal.setCellFactory(mealCellFactory);
        dinnerMeal.setPrefWidth(PREFERRED_COL_WIDTH);
        dinnerMeal.setEditable(true);
        return dinnerMeal;
    }

    public LocalDate getStart() {
        return ListUtil.first(mealPlanBuilder.getDates());
    }

    /*
   Add a side panel to the meal planner containing all the dishes, and a field at the top to filter the list.
   Also, there should be a dropdown of tags to add to the filter list. Adding a tag should add a button
   towards the top.
   Clicking on that button should remove the filter/tag. Clicking on any dish in the list should
   automatically populate the selected (or last selected) field in the table).
     */
    private FlowPane getDishSidePane(DishListener dishListener) {
        DishTagDao dishTagDao = DaoManager.PRODUCTION.getDishTagDao();
        Set<DishTag> tags = dishTagDao.getTags(false);
        Multimap<Dish, DishTag> dishesToTags = dishTagDao.getDishesToTags();
        AtomicBoolean andCondition = new AtomicBoolean(true);
        ToggleButton and = new ToggleButton("AND");
        ToggleButton or = new ToggleButton("OR");
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(and, or);
        and.setOnAction(actionEvent -> andCondition.set(true));
        or.setOnAction(actionEvent -> andCondition.set(false));
        TextField searchString = new TextField();

        HBox andOrBox = new HBox();
        andOrBox.getChildren().add(and);
        andOrBox.getChildren().add(or);


        FilterPanel filterPanel = new FilterPanel(tags, ArrayListMultimap.create());
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        ObservableList<Dish> mutableList = FXCollections.observableList(Lists.newArrayList(dishList));
        TableView<Dish> table = new TableView<>(mutableList);
        flowPane.getChildren().add(andOrBox);
        flowPane.getChildren().add(filterPanel);
        HBox searchRow = new HBox();
        searchRow.getChildren().add(new Label("Filter string"));
        searchRow.getChildren().add(searchString);
        flowPane.getChildren().add(searchRow);
        flowPane.getChildren().add(table);

        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldDish, newDish) -> dishListener.update(newDish));
        dishList.addListener((ListChangeListener<Dish>) change -> {
            filterPanel.fireListeners();
            table.refresh();
        });
        TableColumn<Dish, String> column = new TableColumn<>("Dish");
        column.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getName()));
        table.getColumns().add(column);
        column.setCellFactory(new Callback<TableColumn<Dish, String>, TableCell<Dish, String>>() {
            @Override
            public TableCell<Dish, String> call(TableColumn<Dish, String> dishStringTableColumn) {
                TableCell<Dish, String> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            setGraphic(null);
                        }
                    }
                };
                tableCell.setOnDragDetected(eh -> {
                    // Get the row index of this cell
                    Dish rowDish = tableCell.getTableRow().getItem();
                    int rowIndex = tableCell.getIndex();
                    System.out.println("Cell row index: " + rowIndex);

                    Dragboard db = tableCell.startDragAndDrop(TransferMode.ANY);
                    db.setContent(DragUtils.makeContent(DISH_FORMAT, rowDish));

                    // Get the column index of this cell.
                    int columnIndex = tableCell.getTableView().getColumns().indexOf(tableCell.getTableColumn());
                    System.out.println("Cell column index: " + columnIndex);
                });
                return tableCell;
            }
        });

        FilterPanel.FilterListener listener = new FilterPanel.FilterListener() {
            @Override
            public void filterChanged() {
                List<DishTag> selectedTags = filterPanel.getSelectedTags();
                List<Dish> tempDishes = Lists.newArrayList();
                List<Dish> dishesToUse = Lists.newArrayList(dishList);
                String text = searchString.getText().toLowerCase();
//                if (!text.isBlank()) {//TODO use isBlank. As of java 11, need to make sure project compiles.
                if (!text.isEmpty()) {
                    dishesToUse = dishesToUse.stream().filter(dish -> dish.getName().toLowerCase().contains(text)).collect(Collectors.toList());
                }
                if (selectedTags.isEmpty()) {
                    tempDishes.addAll(dishesToUse);
                } else {
                    for (Dish dish : dishesToUse) {
                        Collection<DishTag> dishTags = dishesToTags.get(dish);
                        if (andCondition.get()) {
                            if (dishTags.containsAll(selectedTags)) {
                                tempDishes.add(dish);
                            }
                        } else {
                            for (DishTag selectedTag : selectedTags) {
                                if (dishTags.contains(selectedTag)) {
                                    tempDishes.add(dish);
                                    break;
                                }

                            }
                        }
                    }
                }
                mutableList.clear();
                mutableList.addAll(tempDishes);
            }
        };
        filterPanel.addListener(listener);
        searchString.setOnAction(actionEvent -> listener.filterChanged());
        return flowPane;
    }

    /*A panel that holds a list of dishes as a temporary storage area.*/
    private FlowPane makeDishHolderPanel() {
        FlowPane flowPane = new FlowPane();
        flowPane.setStyle("-fx-border-color: black");
        VBox vBox = new VBox();
        HBox dishListBox = new HBox();
        Text text = new Text("DishList");

        class DishButton extends Button {
            private final Dish dish;

            public DishButton(Dish dish) {
                super(dish.getName());
                this.dish = dish;
                mealPlanBuilder.addTempDish(dish);
                setOnAction(actionEvent -> {
                    remove();
                });
                setTooltip(new Tooltip("Click to remove"));
                setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Dragboard db = startDragAndDrop(TransferMode.COPY);
                        db.setContent(DragUtils.makeContent(DISH_FORMAT, dish));
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
                mealPlanBuilder.removeTempDish(dish);
                dishListBox.getChildren().remove(DishButton.this);
            }
        }


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


        for (Dish tempDish : mealPlanBuilder.getTempDishes()) {
            dishListBox.getChildren().add(new DishButton(tempDish));
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
                    dishListBox.getChildren().add(new DishButton(dish));
                    success = true;
                } else if (hasMeal) {
                    Meal content = DragUtils.getContent(dragboard, MEAL_FORMAT);
                    //TODO allow meals to be stored here as well. Either store two lists, or just meals.
                    if (!Dish.isStub(content.getDish())) {
                        dishListBox.getChildren().add(new DishButton(content.getDish()));
                        success = true;
                    }
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
        return flowPane;
    }

    private FlowPane makeNotesPanel() {
        FlowPane flowPane = new FlowPane();
        TextArea textField = new TextArea(mealPlanBuilder.getNotes());
//        TextField textField = new TextField(mealPlanBuilder.getNotes());
        textField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1) {
                mealPlanBuilder.setNotes(textField.getText());
            }
        });
        flowPane.getChildren().add(textField);
        return flowPane;
    }

    static class EditingCell extends TableCell<DatedDayPlan, String> {

        private TextField textField;

        public EditingCell() {
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

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
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

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                                    Boolean arg1, Boolean arg2) {
                    System.out.println("Focus changed");
                    if (!arg2) {
                        commitEdit(textField.getText());
                    }
                }
            });
            //Allow Enter key to commit change and stop editing.
            textField.setOnAction(event -> {
                commitEdit(textField.getText());
                event.consume();
            });
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    System.out.println(keyEvent);
                    //TODO ideally, when left or right pressed, we want to also commit the edit, AND make sure
                    //the next cell has the focus. Not sure how to do that.
                }
            });

        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    static class MealCell extends TableCell<DatedDayPlan, Meal> {

        private Dish currentDish;
        private TextField textField;
        private final ObservableList<Dish> dishes;

        public MealCell(ObservableList<Dish> dishes, MealType mealType, MealPlanBuilder mealPlanBuilder) {
            this.dishes = dishes;

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
                    TableRow<DatedDayPlan> tableRow = getTableRow();
                    DatedDayPlan item = tableRow.getItem();
                    LocalDate date = item.getDate();
                    mealPlanBuilder.setMeal(date, mealType, Meal.stub());
                    getTableView().refresh();
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
                    TableRow<DatedDayPlan> tableRow = getTableRow();
                    LocalDate date = tableRow.getItem().getDate();
                    mealPlanBuilder.setMeal(date, mealType, new Meal(dish, ""));
                    getTableView().refresh();
                    success = true;
                } else if (hasMeal) {
                    Meal meal = DragUtils.getContent(dragboard, MEAL_FORMAT);
                    TableRow<DatedDayPlan> tableRow = getTableRow();
                    LocalDate date = tableRow.getItem().getDate();
                    mealPlanBuilder.setMeal(date, mealType, meal);
                    getTableView().refresh();
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);
                event.consume();
            });
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
                Dish dish = (text.isEmpty() || currentDish == null) ? MealPlannerTest.stub() : currentDish;
                meal = new Meal(dish, text);
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

    static class DatedDayPlan implements DayPlanIF {
        final LocalDate date;
        final DayPlanIF dayPlan;

        public DatedDayPlan(LocalDate date, DayPlanIF dayPlan) {
            this.date = date;
            this.dayPlan = dayPlan;
        }

        public LocalDate getDate() {
            return date;
        }

        public DayPlanIF getDayPlan() {
            return dayPlan;
        }

        @Override
        public String getToCook() {
            return dayPlan.getToCook();
        }

        @Override
        public String getUnfreeze() {
            return dayPlan.getUnfreeze();
        }

        @Override
        public Meal getBreakfast() {
            return dayPlan.getBreakfast();
        }

        @Override
        public Meal getLunch() {
            return dayPlan.getLunch();
        }

        @Override
        public Meal getDinner() {
            return dayPlan.getDinner();
        }

        public Meal getMeal(MealType type) {
            switch (type) {
                case BREAKFAST:
                    return getBreakfast();
                case LUNCH:
                    return getLunch();
                case DINNER:
                    return getDinner();
            }
            return null;
        }
    }
}
