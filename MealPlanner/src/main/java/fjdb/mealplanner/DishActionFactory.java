package fjdb.mealplanner;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fjdb.mealplanner.fx.MealPlanProxy;
import fjdb.util.DateTimeUtil;
import fjdb.util.ListUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class DishActionFactory {

    private final MealHistoryManager mealHistoryManager;

    private MealPlanProxy currentMealPlan = new MealPlanStub();

    public DishActionFactory(MealHistoryManager mealHistoryManager) {
        this.mealHistoryManager = mealHistoryManager;
    }

    public void getDishHistory(Dish dish) {
        List<LocalDate> dates = mealHistoryManager.getDates(dish);

        TableView<LocalDate> table = new TableView<>(FXCollections.observableList(dates));
        TableColumn<LocalDate, LocalDate> tableColumn = new TableColumn<>("Date");
        tableColumn.setCellValueFactory(x -> Bindings.createObjectBinding(x::getValue));
        table.getColumns().add(tableColumn);
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(table, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public Menu getDishMenu(Dish dish) {
        Menu menu = new Menu("Dish Menu");
        menu.getItems().add(getDishHistoryMenu(dish));
        menu.getItems().add(addDishToMealPlan(dish));
        return menu;
    }

    public void showDishHistory(List<Dish> dishes) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);

        TableView<HistoryRow> historyTable = getHistoryTable(dishes);
        Scene dialogScene = new Scene(historyTable, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public TableView<HistoryRow> getHistoryTable(List<Dish> dishes) {
        List<HistoryRow> rows = Lists.newArrayList();
        for (Dish dish : dishes) {
            List<LocalDate> dates = mealHistoryManager.getDates(dish);
            LocalDate last = ListUtil.tryLast(dates);
            int count = dates.size();
            rows.add(new HistoryRow(dish, last, count));
        }
        TableView<HistoryRow> table = new TableView<>(FXCollections.observableList(rows));
        TableColumn<HistoryRow, Dish> dishColumn = new TableColumn<>("Dish");
        TableColumn<HistoryRow, LocalDate> lastDateColumn = new TableColumn<>("Last Date");
        TableColumn<HistoryRow, Integer> dishInstances = new TableColumn<>("Dish Count");
        dishColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDish()));
        lastDateColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getLastDate()));
        dishInstances.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getInstances()));
        table.getColumns().addAll(dishColumn, lastDateColumn, dishInstances);
        dishColumn.setSortable(true);
        lastDateColumn.setSortable(true);
        dishInstances.setSortable(true);

        table.setRowFactory(tv -> {
            TableRow<HistoryRow> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (MouseButton.SECONDARY.equals(mouseEvent.getButton())) {
                    ContextMenu contextMenu = new ContextMenu();
                    HistoryRow selectedItem = row.getItem();
                    contextMenu.getItems().add(showDates(selectedItem.getDish()));
                    contextMenu.getItems().add(addDishToMealPlan(selectedItem.getDish()));
                    contextMenu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            });
            return row;
        });
        return table;
    }

    public void setCurrentMealPlan(MealPlanProxy currentMealPlan) {
        this.currentMealPlan = currentMealPlan;
    }

    public MealPlanProxy getCurrentMealPlan() {
        return currentMealPlan;
    }

    public static class HistoryRow {
        private final Dish dish;
        private final LocalDate lastDate;
        private final int instances;

        public HistoryRow(Dish dish, LocalDate lastDate, int instances) {
            this.dish = dish;
            this.lastDate = lastDate;
            this.instances = instances;
        }

        public Dish getDish() {
            return dish;
        }

        public LocalDate getLastDate() {
            return lastDate;
        }

        public int getInstances() {
            return instances;
        }
    }

    private MenuItem showDates(Dish dish) {
        MenuItem menuItem = new MenuItem("Show dates");
        menuItem.setOnAction(actionEvent -> {
            List<LocalDate> dates = mealHistoryManager.getDates(dish);
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            Scene dialogScene = new Scene(new Label(Joiner.on("\n").join(dates)), 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });
        return menuItem;
    }

    private MenuItem getDishHistoryMenu(Dish dish) {
        MenuItem dishHistory = new MenuItem("Dish History");
        dishHistory.setOnAction(actionEvent -> getDishHistory(dish));
        return dishHistory;
    }

    private MenuItem addDishToMealPlan(Dish dish) {
        MenuItem menuItem = new MenuItem("Add Dish to plan starting " + currentMealPlan.getStart());
        menuItem.setOnAction(actionEvent -> currentMealPlan.addDishToHolder(dish));
        return menuItem;
    }

    public MenuItem addDishToMealPlan(Dish dish, LocalDate date, MealType mealType) {
        MenuItem menuItem = new MenuItem("Add Dish to plan on " + date);
        menuItem.setOnAction(actionEvent -> currentMealPlan.addDish(dish, date, mealType));
        return menuItem;
    }

    private static class MealPlanStub implements MealPlanProxy {
        @Override
        public LocalDate getStart() {
            return DateTimeUtil.today().minusWeeks(2);
        }

        @Override
        public void addDishToHolder(Dish dish) {
            //no-op
        }

        @Override
        public void addDish(Dish dish, LocalDate date, MealType type) {
            //no-op
        }
    }
}
