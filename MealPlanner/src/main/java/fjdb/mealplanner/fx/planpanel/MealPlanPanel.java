package fjdb.mealplanner.fx.planpanel;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fjdb.mealplanner.*;
import fjdb.mealplanner.dao.DishTagDao;
import fjdb.mealplanner.fx.*;
import fjdb.util.ListUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static fjdb.mealplanner.fx.DragUtils.DISH_FORMAT;
import static fjdb.mealplanner.fx.DragUtils.MEAL_FORMAT;

public class MealPlanPanel extends FlowPane implements MealPlanProxy {

    private static final Logger log = LoggerFactory.getLogger(MealPlanPanel.class);
    private static final double PREFERRED_COL_WIDTH = 150.0;

    private final MealPlanBuilder mealPlanBuilder;
    private final DishActionFactory dishActionFactory;
    private final ObservableList<Dish> dishList;
    private final DishHolderPanel dishHolderPanel;
    private final TableView<DatedDayPlan> tableView;
    private final boolean isTemplate;

    private final HashMap<TableColumn<DatedDayPlan, ?>, MealType> columnMap = new HashMap<>();

    public MealPlanPanel(MealPlanConfigurator.Configuration configuration, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this(new MealPlanBuilder(), configuration.getDate(), configuration.getDays(), dishList, mealPlanManager, false);
    }

    public MealPlanPanel(MealPlan mealPlan, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this(new MealPlanBuilder(mealPlan), mealPlan.getStart(), mealPlan.getDates().size(), dishList, mealPlanManager, false);
    }

    public MealPlanPanel(MealPlan mealPlan, ObservableList<Dish> dishList, MealPlanManager mealPlanManager, boolean isTemplate) {
        this(new MealPlanBuilder(mealPlan), mealPlan.getStart(), mealPlan.getDates().size(), dishList, mealPlanManager, isTemplate);
    }

    private MealPlanPanel(MealPlanBuilder builder, LocalDate startDate, int days, ObservableList<Dish> dishList, MealPlanManager mealPlanManager, boolean isTemplate) {
        super(Orientation.VERTICAL);
        this.isTemplate = isTemplate;
        this.dishList = dishList;
        mealPlanBuilder = builder;
        this.dishActionFactory = mealPlanManager.getDishActionFactory();
        dishActionFactory.setCurrentMealPlan(this);


        TableView<DatedDayPlan> dayPlansTable = new TableView<>();
        tableView = dayPlansTable;
//        tableView.getSelectionModel().setSelectionMode(
//                SelectionMode.MULTIPLE
//        );

        dishList.addListener((ListChangeListener<Dish>) change -> dayPlansTable.refresh());

        LocalDate endDate = startDate.plusDays(days - 1);
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            DayPlanIF dayPlan = mealPlanBuilder.getDayPlan(date);
            dayPlansTable.getItems().add(new DatedDayPlan(date, dayPlan));
            date = date.plusDays(1);
        }
        //TODO for dayPlaysTable, can we call setItems and pass in an observable list from the builder. The builder, when making any changes
        //to a day plan, should reset the element in that list. In doing so, will that then get the table to update the particular
        //row that has changed, rather than having to call tableView.refresh() all the time?

        dayPlansTable.setEditable(true);
        //required to show individual cells highlighted on their own, rather than the whole row:
        dayPlansTable.getSelectionModel().setCellSelectionEnabled(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E yyyyMMdd");

        LocalDate dateToday = LocalDate.now();
        Callback<TableColumn<DatedDayPlan, String>, TableCell<DatedDayPlan, String>> cellFactory = p -> new EditingCell();

        TableColumn<DatedDayPlan, LocalDate> dateColumn = new TableColumn<>("Date");
        Callback<TableColumn<DatedDayPlan, LocalDate>, TableCell<DatedDayPlan, LocalDate>> dateCellFactory = new Callback<>() {
            @Override
            public TableCell<DatedDayPlan, LocalDate> call(TableColumn<DatedDayPlan, LocalDate> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(formatter.format(item));
                            if (dateToday.equals(item)) {
                                TableRow<DatedDayPlan> currentRow = getTableRow();
                                if (currentRow != null) {
                                    currentRow.setStyle("-fx-background-color:lightblue");
                                }
                            }

                        }
                    }
                };
            }
        };

        dateColumn.setCellFactory(dateCellFactory);
        dateColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDate()));

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

        columnMap.put(breakfastMeal, MealType.BREAKFAST);
        columnMap.put(lunchMeal, MealType.LUNCH);
        columnMap.put(dinnerMeal, MealType.DINNER);
        dayPlansTable.getColumns().addAll(breakfastMeal, lunchMeal, dinnerMeal);
        dayPlansTable.getColumns().forEach(c -> c.setSortable(false));

        dayPlansTable.setOnKeyPressed(keyEvent -> {
            if (KeyCode.DELETE.equals(keyEvent.getCode()) || KeyCode.BACK_SPACE.equals(keyEvent.getCode())) {
                DatedDayPlan selectedItem = dayPlansTable.getSelectionModel().getSelectedItem();
                LocalDate date1 = selectedItem.getDate();
                ObservableList<TablePosition> selectedCells = dayPlansTable.getSelectionModel().getSelectedCells();
                for (TablePosition selectedCell : selectedCells) {
                    TableColumn tableColumn = selectedCell.getTableColumn();
                    if (tableColumn instanceof MealColumn) {
                        MealColumn column = (MealColumn) tableColumn;
                        MealType mealType = column.mealType;
                        mealPlanBuilder.setMeal(date1, mealType, Meal.stub());
                    }
                }
                dayPlansTable.refresh();
            }
        });
        dayPlansTable.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.isControlDown()) {
                List<LocalDate> dates = builder.getDates();
                ObservableList<DatedDayPlan> selectedItems = dayPlansTable.getSelectionModel().getSelectedItems();
                for (DatedDayPlan selectedItem : selectedItems) {
                    ObservableList<TablePosition> selectedCells = dayPlansTable.getSelectionModel().getSelectedCells();
                    if (selectedCells.size() == 1) {
                        TableColumn tableColumn = selectedCells.get(0).getTableColumn();
                        MealType type = columnMap.get(tableColumn);
                        Meal meal = selectedItem.getMeal(type);
                        LocalDate date12 = selectedItem.getDate();

                        if (KeyCode.D.equals(keyEvent.getCode())) {

                            if (ListUtil.last(dates).isAfter(date12)) {
                                builder.setMeal(date12.plusDays(1), type, meal);
                                dayPlansTable.refresh();
                            }
                        } else if (KeyCode.F.equals(keyEvent.getCode())) {
                            MealPlanProxy currentMealPlan = dishActionFactory.getCurrentMealPlan();
                            if (currentMealPlan != null) {
                                LocalDate dateInNextPlan = getDateInNextPlan(builder, date12, currentMealPlan.getStart());
                                currentMealPlan.addDish(meal, dateInNextPlan, type);
                            }

                        } else if (KeyCode.C.equals(keyEvent.getCode())) {
                            Meal item = meal;
                            if (item != null) {
                                Clipboard.getSystemClipboard().setContent(DragUtils.makeContent(MEAL_FORMAT, item));
                            }
                        } else if (KeyCode.V.equals(keyEvent.getCode())) {
                            if (Clipboard.getSystemClipboard().hasContent(DISH_FORMAT)) {
                                Dish dish = DragUtils.getContent(Clipboard.getSystemClipboard(), DISH_FORMAT);
                                mealPlanBuilder.setMeal(date12, type, new Meal(dish, ""));
                                dayPlansTable.refresh();
                            } else if (Clipboard.getSystemClipboard().hasContent(MEAL_FORMAT)) {
                                Meal mealPaste = DragUtils.getContent(Clipboard.getSystemClipboard(), MEAL_FORMAT);
                                mealPlanBuilder.setMeal(date12, type, mealPaste);
                                dayPlansTable.refresh();
                            }

                        }
                    }
                }
            }
        });

        ContextMenu cm = new ContextMenu();
        MenuItem addDayBefore = new MenuItem("Add extra date before");
        cm.getItems().add(addDayBefore);
        addDayBefore.setOnAction(actionEvent -> {
            List<LocalDate> dates = mealPlanBuilder.getDates();
            LocalDate newFirstDate = ListUtil.first(dates).minusDays(1);
            DayPlanIF dayPlan = mealPlanBuilder.getDayPlan(newFirstDate);
            ObservableList<DatedDayPlan> items = dayPlansTable.getItems();
            items.add(0, new DatedDayPlan(newFirstDate, dayPlan));
        });
        MenuItem mi1 = new MenuItem("Add extra date after");
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
//        dayPlansTable.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
//            if (t.getButton() == MouseButton.SECONDARY) {
//                cm.show(dayPlansTable, t.getScreenX(), t.getScreenY());
//            }
//        });


        dayPlansTable.setContextMenu(cm);
        Button makePlan = new Button(isTemplate ? "Save Template" : "Make MealPlan");

        makePlan.setOnAction(actionEvent -> {
            if (isTemplate) {
                mealPlanManager.saveTemplate(mealPlanBuilder.makePlan());
            } else {
                MealPlan mealPlan = mealPlanBuilder.makePlan();
                mealPlanManager.addMealPlan(mealPlan);
            }
        });
        Button csvPlan = createButton("Create CSV", mealPlanManager::toCSV, mealPlanManager.getCSVDirectory(), mealPlanManager);
        Button pdfPlan = createButton("Create PDF", mealPlanManager::toPdf, mealPlanManager.getCSVDirectory(), mealPlanManager);
        Button pdfEmailPlan = createButton("Make & Email PDF", mealPlanManager::toPdfAndEmail, mealPlanManager.getCSVDirectory(), mealPlanManager);
        Button xlsPlan = createButton("Create XLS", mealPlanManager::toExcel, mealPlanManager.getCSVDirectory(), mealPlanManager);
        Button print = new Button("print meals");

        print.setOnAction(actionEvent -> {
            mealPlanManager.printAllMeals();
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
        Button showDishHistory = new Button("Dish History");
        showDishHistory.setOnAction(actionEvent -> dishActionFactory.showDishHistory(dishList));


        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.getChildren().add(dayPlansTable);
//        flowPane.getChildren().add(getDishSidePane());


        FlowPane bottomPanel = new FlowPane(Orientation.VERTICAL);
//        flowPane.setStyle("-fx-border-color: black");
//        FlowPane controls = new FlowPane();
        HBox controls = new HBox();
        controls.setSpacing(5.0);
        controls.getChildren().add(makePlan);
        controls.getChildren().add(csvPlan);
        controls.getChildren().add(pdfPlan);
        controls.getChildren().add(pdfEmailPlan);
        controls.getChildren().add(xlsPlan);
        controls.getChildren().add(print);
        controls.getChildren().add(showDishHistory);
        bottomPanel.getChildren().add(controls);


        //        getChildren().add(makeDishHolderPanel());
        dishHolderPanel = new DishHolderPanel();
        bottomPanel.getChildren().add(dishHolderPanel);
        bottomPanel.getChildren().add(makeNotesPanel());
//        getChildren().add(bottomPanel);

        flowPane.getChildren().add(bottomPanel);

        getChildren().add(flowPane);
        getChildren().add(getDishSidePane());


        flowPane.setPadding(new Insets(5.0));
        controls.setPadding(new Insets(5.0));
        bottomPanel.setPadding(new Insets(5.0));
        setPadding(new Insets(5.0));

    }

    private Button createButton(String label, Consumer<MealPlan> operation, File directory, MealPlanManager mealPlanManager) {
        Button button = new Button(label);
        button.setOnAction(actionEvent -> {
            MealPlan mealPlan = mealPlanBuilder.makePlan();
            mealPlanManager.addMealPlan(mealPlan);
            operation.accept(mealPlan);
            try {
                Runtime.getRuntime().exec("open " + directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    private void removeMeal(TableRow<DatedDayPlan> tableRow, MealType mealType) {
        DatedDayPlan item = tableRow.getItem();
        LocalDate date = item.getDate();
        mealPlanBuilder.setMeal(date, mealType, Meal.stub());
        tableRow.getTableView().refresh();
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
        Callback<TableColumn.CellDataFeatures<DatedDayPlan, Meal>, ObservableValue<Meal>> cellDataFeaturesObservableValueCallback = x -> {
            ObjectBinding<Meal> objectBinding = Bindings.createObjectBinding(() -> {
                DatedDayPlan value = x.getValue();
                return value.getMeal(mealType);
            });
            return objectBinding;
        };
        dinnerMeal.setCellValueFactory(cellDataFeaturesObservableValueCallback);

        dinnerMeal.setEditable(true);
        dinnerMeal.setOnEditCommit(t -> {
            mealPlanBuilder.setMeal(t.getRowValue().getDate(), mealType, t.getNewValue());
            dayPlansTable.refresh();
        });

        Callback<TableColumn<DatedDayPlan, Meal>, TableCell<DatedDayPlan, Meal>> mealCellFactory = p -> new MealCell(dishList, mealType, mealPlanBuilder, dishActionFactory);

        dinnerMeal.setCellFactory(mealCellFactory);
        dinnerMeal.setPrefWidth(PREFERRED_COL_WIDTH);
        dinnerMeal.setEditable(true);
        return dinnerMeal;
    }


    /*
   Add a side panel to the meal planner containing all the dishes, and a field at the top to filter the list.
   Also, there should be a dropdown of tags to add to the filter list. Adding a tag should add a button
   towards the top.
   Clicking on that button should remove the filter/tag. Clicking on any dish in the list should
   automatically populate the selected (or last selected) field in the table).
     */
    private FlowPane getDishSidePane() {
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


//        FilterPanel filterPanel = new FilterPanel(tags, ArrayListMultimap.create());
        DishTagSelectionPanel filterPanel = new DishTagSelectionPanel(tags, ArrayListMultimap.create());
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

//        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldDish, newDish) -> dishListener.update(newDish));
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
                tableCell.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2) {
                        Dish rowDish = tableCell.getTableRow().getItem();
                        dishHolderPanel.addDish(rowDish);
                    }
                });
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

        SelectionPanel.SelectionListener listener = () -> {
            List<DishTag> selectedTags = filterPanel.getSelectedItems();
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
        };
        filterPanel.addListener(listener);
        searchString.setOnAction(actionEvent -> listener.selectionChanged());
        return flowPane;
    }

    private class DishHolderPanel extends FlowPane {

        private final HBox dishListBox;

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

        public DishHolderPanel() {
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
            textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
                System.out.println("Focus changed");
                if (!arg2) {
                    commitEdit(textField.getText());
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

    /**
     * A wrapper class for a DayPlanIF, associating a specific LocalDate to it.
     */
    public static class DatedDayPlan implements DayPlanIF {
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

    protected static LocalDate getDateInNextPlan(MealPlanBuilder currentPlan, LocalDate currentDate, LocalDate nextStart) {
        LocalDate start = currentPlan.getStart();
        return nextStart.plusDays(start.datesUntil(currentDate).count());
    }

    //########## MealPlanProxy ##########

    @Override
    public LocalDate getStart() {
        return ListUtil.first(mealPlanBuilder.getDates());
    }

    public LocalDate getEnd() {
        return ListUtil.last(mealPlanBuilder.getDates());
    }

    @Override
    public void addDishToHolder(Meal dish) {
        //TODO this creates a DishButton in the holder panel, which then adds it to the mealPlanBuilder. I would
        //prefer this method adds it to the builder (the model), and the model uses listeners to then update
        //the appropriate panels.
        dishHolderPanel.addMeal(dish);
    }

    @Override
    public void addDish(Meal meal, LocalDate date, MealType type) {
        mealPlanBuilder.setMeal(date, type, meal);
        tableView.refresh();
    }

    @Override
    public Set<Meal> getRecentMeals() {
        return mealPlanBuilder.getRecentMeals();
    }

    public List<Meal> getMealList() {
        return new UnmodifiableList<>(new ArrayList<>(mealPlanBuilder.getTempMeals()));
    }

    public void addTempDish(Dish dish) {
        dishHolderPanel.addDish(dish);
    }

    public MealPlan makePlan() {
        return mealPlanBuilder.makePlan();
    }

}
