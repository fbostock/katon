package fjdb.mealplanner.panels;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.MealPlan;
import fjdb.mealplanner.MealPlanManager;
import fjdb.mealplanner.MealPlanner;
import fjdb.mealplanner.fx.planpanel.MealPlanPanel;
import fjdb.util.DateTimeUtil;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class PlansPane extends TabPane {

    private MealPlanPanel latestPlan;
    private MealPlanManager manager;
    private ObservableList<Dish> dishList;

    public PlansPane(MealPlanManager manager, ObservableList<Dish> dishList, boolean isArchive) {
        this.manager = manager;
        this.dishList = dishList;
        if (!isArchive) {
            getTabs().add(new Tab("Archive", new PlansPane(manager, dishList, true)));
        }
        List<MealPlan> mealPlans = isArchive ? manager.getArchived() : manager.getMealPlans();
        for (MealPlan mealPlan : mealPlans) {
            MealPlanPanel mealPlanPanel = new MealPlanPanel(mealPlan, dishList, manager);
            latestPlan = mealPlanPanel;
            addMealPlanPanel(mealPlanPanel);
        }
    }

    public void loadTemplatePlan(MealPlan template) {
        //TODO when saving, this needs to update the template plan, not a normal plan.
        MealPlanPanel mealPlanPanel = new MealPlanPanel(template, dishList, manager, true);
        ScrollPane scrollPane = new ScrollPane(mealPlanPanel);
        getTabs().add(new Tab(String.format("Template Plan %s", mealPlanPanel.getStart()), scrollPane));
    }

    public void addMealPlanPanel(MealPlanPanel mealPlanPanel) {
        ScrollPane scrollPane = new ScrollPane(mealPlanPanel);
        getTabs().add(new Tab(String.format("Plan %s", mealPlanPanel.getStart()), scrollPane));
        if (latestPlan == null || mealPlanPanel.getStart().isAfter(latestPlan.getStart())) {
            latestPlan = mealPlanPanel;
        }
    }

    public MealPlanPanel getLatestPlan() {
        return latestPlan;
    }

    public LocalDate getNextDateForNewPlan() {
        if (latestPlan != null) {
            return latestPlan.getEnd().plusDays(1);
        } else {
            LocalDate date = DateTimeUtil.today();
            while (!date.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                date = date.plusDays(1);
            }
            return date;
        }
    }
}
