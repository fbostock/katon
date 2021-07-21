package fjdb.mealplanner.fx;

import fjdb.mealplanner.*;
import fjdb.util.ListUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MealPlanPanel extends FlowPane {

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
//        LocalDate startDate = builder.getDate();
//        int days = configuration.getDays();
//        mealPlanBuilder = new MealPlanBuilder();
        mealPlanBuilder = builder;

        TableView<DatedDayPlan> dayPlansTable = new TableView<>();
        DishListener dishListener = dish -> {
            ObservableList<TablePosition> selectedCells = dayPlansTable.getSelectionModel().getSelectedCells();
            for (TablePosition selectedCell : selectedCells) {
                /* TODO
What is the best way to know the properties of the selected cell e.g. whether it's dinner or lunch, whcih day etc.
                 */
                DatedDayPlan selectedItem = dayPlansTable.getSelectionModel().getSelectedItem();
                LocalDate date = selectedItem.getDate();
                //TODO replace magic constants.
                if (selectedCell.getColumn() == 3) {
                    mealPlanBuilder.setBreakfast(date, new Meal(dish, MealType.BREAKFAST, date, ""));
                } else if (selectedCell.getColumn() == 4) {
                    mealPlanBuilder.setLunch(date, new Meal(dish, MealType.LUNCH, date, ""));
                } else if (selectedCell.getColumn() == 5) {
                    mealPlanBuilder.setDinner(date, new Meal(dish, MealType.DINNER, date, ""));
                }
                dayPlansTable.refresh();
            }

        };
        LocalDate endDate = startDate.plusDays(days - 1);
        LocalDate date = startDate;
        while (date.isBefore(endDate)) {
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

        Callback<TableColumn<DatedDayPlan, String>, TableCell<DatedDayPlan, String>> cellFactory =
                new Callback<TableColumn<DatedDayPlan, String>, TableCell<DatedDayPlan, String>>() {
                    public TableCell<DatedDayPlan, String> call(TableColumn p) {
                        return new EditingCell();
                    }
                };

        TableColumn<DatedDayPlan, String> unfreeze = new TableColumn<>("Unfreeze");
        unfreeze.setPrefWidth(PREFERRED_COL_WIDTH);
        TableColumn<DatedDayPlan, String> cook = new TableColumn<>("Cook");
        unfreeze.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDayPlan().getUnfreeze()));
        cook.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().dayPlan.getToCook()));
        cook.setPrefWidth(PREFERRED_COL_WIDTH);

        unfreeze.setEditable(true);
        unfreeze.setCellFactory(cellFactory);
        unfreeze.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DatedDayPlan, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DatedDayPlan, String> t) {
                mealPlanBuilder.setUnfreeze(t.getRowValue().getDate(), t.getNewValue());
            }
        });

        cook.setEditable(true);//TODO check we need this
        cook.setCellFactory(cellFactory);
//        cook.setCellFactory(TextFieldTableCell.forTableColumn());
        cook.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DatedDayPlan, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DatedDayPlan, String> t) {
                mealPlanBuilder.setCook(t.getRowValue().getDate(), t.getNewValue());
            }
        });
        TableColumn<DatedDayPlan, Dish> breakfast = makeColumn("Breakfast", datedDayPlan -> datedDayPlan.getBreakfast().getDish());
        TableColumn<DatedDayPlan, Dish> lunchColumn = makeColumn("Lunch", datedDayPlan -> datedDayPlan.getLunch().getDish());
        TableColumn<DatedDayPlan, Dish> dinner = makeColumn("Dinner", datedDayPlan -> datedDayPlan.getDinner().getDish());


        breakfast.setEditable(true);
        lunchColumn.setEditable(true);
        dinner.setEditable(true);
//        lunchColumn.setCellFactory(cb -> new ComboBoxTableCell<>(dishList));

        breakfast.setCellFactory(cb -> getDishCombo(mealPlanBuilder::setBreakfast));
        lunchColumn.setCellFactory(cb -> getDishCombo(mealPlanBuilder::setLunch));
        dinner.setCellFactory(cb -> getDishCombo(mealPlanBuilder::setDinner));
        dayPlansTable.getColumns().addAll(dateColumn, unfreeze, cook, breakfast, lunchColumn, dinner);

        Button makePlan = new Button("Make MealPlan");
        makePlan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MealPlan mealPlan = mealPlanBuilder.makePlan();
                //TODO add to MealPlanManager, and get application tabs to update.
//                mealPlan.print();
                mealPlanManager.addMealPlan(mealPlan);
            }
        });
        Button csvPlan = new Button("Create CSV");
        csvPlan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MealPlan mealPlan = mealPlanBuilder.makePlan();
                //TODO add to MealPlanManager, and get application tabs to update.
                mealPlanManager.toCSV(mealPlan);
                try {
                    Runtime.getRuntime().exec("open " + mealPlanManager.getDirectory());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.getChildren().add(dayPlansTable);
        flowPane.getChildren().add(getDishSidePane(dishListener));
        getChildren().add(flowPane);
        getChildren().add(makePlan);
        getChildren().add(csvPlan);

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
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        TableView<Dish> table = new TableView<>(FXCollections.observableArrayList(dishList));
        flowPane.getChildren().add(table);
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Dish>() {
            @Override
            public void changed(ObservableValue<? extends Dish> observableValue, Dish oldDish, Dish newDish) {
                dishListener.update(newDish);
            }
        });


        TableColumn<Dish, String> column = new TableColumn<>("Dish");
        column.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getName()));
        table.getColumns().add(column);
        return flowPane;
    }


    private ComboBoxTableCell<DatedDayPlan, Dish> getDishCombo(BiConsumer<LocalDate, Meal> consumer) {
        ComboBoxTableCell<DatedDayPlan, Dish> combo = new ComboBoxTableCell<>(dishList) {
            @Override
            public void startEdit() {
                super.startEdit();
                if (isEditing() && getGraphic() instanceof ComboBox) {
                    // needs focus for proper working of esc/enter
                    getGraphic().requestFocus();
                    ((ComboBox<?>) getGraphic()).show();
                }
            }

            @Override
            public void commitEdit(Dish newValue) {
                super.commitEdit(newValue);
                TableRow<DatedDayPlan> tableRow = getTableRow();
                TableView<DatedDayPlan> tableView = getTableView();
                int index = tableRow.getIndex();
                DatedDayPlan plan = tableView.getItems().get(index);
                LocalDate date = plan.getDate();
                consumer.accept(date, new Meal(newValue, null, date, ""));
            }
        };

//        combo.setComboBoxEditable(true);
//        combo.setEditable(true);
        return combo;
    }

    private static <T> TableColumn<DatedDayPlan, T> makeColumn(String name, Function<DatedDayPlan, T> function) {
        TableColumn<DatedDayPlan, T> column = new TableColumn<>(name);
        column.setCellValueFactory(x -> Bindings.createObjectBinding(() -> {
            DatedDayPlan value = x.getValue();
            return function.apply(value);
        }));
        column.setPrefWidth(PREFERRED_COL_WIDTH);
        return column;
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
                    //TODO ideally, when left or right pressed, we want to also commit the edit, AND make sure
                    //the next cell has the focus. Not sure how to do that.
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
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
    }
}
