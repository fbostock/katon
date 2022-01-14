package fjdb.mealplanner.fx;

import fjdb.mealplanner.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.time.LocalDate;

public class MealPlanPanelGrid extends FlowPane {

    private final MealPlanBuilder mealPlanBuilder;
    private final ObservableList<Dish> dishList;
    private static final double PREFERRED_COL_WIDTH = 150.0;

    public MealPlanPanelGrid(MealPlanConfigurator.Configuration configuration, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this(new MealPlanBuilder(), configuration.getDate(), configuration.getDays(), dishList, mealPlanManager);
    }

    public MealPlanPanelGrid(MealPlan mealPlan, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        this(new MealPlanBuilder(mealPlan), mealPlan.getStart(), mealPlan.getDates().size(), dishList, mealPlanManager);
    }

    public MealPlanPanelGrid(MealPlanBuilder builder, LocalDate startDate, int days, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        mealPlanBuilder = builder;
        this.dishList = dishList;

        LocalDate endDate = startDate.plusDays(days - 1);
        LocalDate date = startDate;
        while (date.isBefore(endDate)) {
            DayPlanIF dayPlan = mealPlanBuilder.getDayPlan(date);
//            dayPlansTable.getItems().add(new MealPlanPanel.DatedDayPlan(date, dayPlan));
            date = date.plusDays(1);
        }

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
                    Runtime.getRuntime().exec("open " + mealPlanManager.getCSVDirectory());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
//        flowPane.getChildren().add(dayPlansTable);
//        flowPane.getChildren().add(getDishSidePane(dishListener));
        getChildren().add(flowPane);
        getChildren().add(makePlan);
        getChildren().add(csvPlan);
    }
}
