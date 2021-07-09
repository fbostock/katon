package fjdb.mealplanner.fx;

import fjdb.mealplanner.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MealPlanPanel extends FlowPane {

    private final MealPlanBuilder mealPlanBuilder;
    private final ObservableList<Dish> dishList;
private static final double PREFERRED_COL_WIDTH = 200.0;

    public MealPlanPanel(MealPlanConfigurator.Configuration configuration, ObservableList<Dish> dishList) {
        this.dishList = dishList;
        LocalDate startDate = configuration.getDate();
        int days = configuration.getDays();
        mealPlanBuilder = new MealPlanBuilder();
        mealPlanBuilder.setBreakfast(startDate, new Meal(Dishes.PANCAKES, MealType.BREAKFAST, startDate, ""));
        mealPlanBuilder.setLunch(startDate, new Meal(Dishes.FRYUP, MealType.LUNCH, startDate, ""));
        mealPlanBuilder.setDinner(startDate, new Meal(Dishes.LASAGNE, MealType.DINNER, startDate, ""));

        //TODO make panel with given number of days entries.
        TableView<DatedDayPlan> dayPlansTable = new TableView<>();
//        dayPlans.selec
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

        TableColumn<DatedDayPlan, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDate()));

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
        TableColumn<DatedDayPlan, Dish> breakfast = makeColumn("Breakfast", datedDayPlan->datedDayPlan.getBreakfast().getDish());
        TableColumn<DatedDayPlan, Dish> lunchColumn = makeColumn("Lunch", datedDayPlan -> datedDayPlan.getLunch().getDish());
        TableColumn<DatedDayPlan, Dish> dinner = makeColumn("Dinner", datedDayPlan->datedDayPlan.getDinner().getDish());


        breakfast.setEditable(true);
        lunchColumn.setEditable(true);
        dinner.setEditable(true);
//        lunchColumn.setCellFactory(cb -> new ComboBoxTableCell<>(dishList));

        breakfast.setCellFactory(cb -> getDishCombo(mealPlanBuilder::setBreakfast));
        lunchColumn.setCellFactory(cb -> getDishCombo(mealPlanBuilder::setLunch));
        dinner.setCellFactory(cb -> getDishCombo(mealPlanBuilder::setDinner));
        dayPlansTable.getColumns().addAll(dateColumn, unfreeze, cook, breakfast, lunchColumn, dinner);

        getChildren().add(dayPlansTable);
        Button makePlan = new Button("Make MealPlan");
        makePlan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MealPlan mealPlan = mealPlanBuilder.makePlan();
                mealPlan.print();
            }
        });
        getChildren().add(makePlan);

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
