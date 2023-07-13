package fjdb.mealplanner.fx;

import fjdb.mealplanner.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Consumer;

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
            date = date.plusDays(1);
        }

        Button makePlan = new Button("Make MealPlan");
        makePlan.setOnAction(actionEvent -> {
            MealPlan mealPlan = mealPlanBuilder.makePlan();
            mealPlanManager.addMealPlan(mealPlan);
        });

        Button csvPlan = createButton("Create CSV", mealPlanManager::toCSV, mealPlanManager.getCSVDirectory());
        Button pdfPlan = createButton("Create PDF", mealPlanManager::toPdf, mealPlanManager.getCSVDirectory());
        Button xlsPlan = createButton("Create Excel", mealPlanManager::toExcel, mealPlanManager.getCSVDirectory());

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        getChildren().add(flowPane);
        getChildren().add(makePlan);
        getChildren().add(pdfPlan);
        getChildren().add(csvPlan);
        getChildren().add(xlsPlan);
    }

    private Button createButton(String label, Consumer<MealPlan> operation, File directory) {
        Button button = new Button(label);
        button.setOnAction(actionEvent -> {
            MealPlan mealPlan = mealPlanBuilder.makePlan();
            operation.accept(mealPlan);
            try {
                Runtime.getRuntime().exec("open " + directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return button;
    }
}
