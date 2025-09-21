package fjdb.mealplanner.fx;

import fjdb.mealplanner.Dish;
import fjdb.mealplanner.MealPlanManager;
import fjdb.mealplanner.MealPlanTemplates;
import fjdb.mealplanner.fx.planpanel.MealPlanPanel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.time.LocalDate;
import java.util.function.Consumer;

public class MealPlanConfigurator extends FlowPane {

    public MealPlanConfigurator(Consumer<MealPlanPanel> consumer, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {

        //TODO a selector to define start date
        //a selector to set num days


        DatePicker datePicker = Selectors.getDateSelector();
        Spinner<Integer> daySpinner = new Spinner<>(1, 28, 1);
        SpinnerValueFactory<Integer> valueFactory = daySpinner.getValueFactory();
        valueFactory.setValue(14);

        CheckBox useTemplate = new CheckBox("Use Template");

        getChildren().add(datePicker);
        getChildren().add(daySpinner);
        getChildren().add(useTemplate);
        Button ok = new Button("OK");
        ok.setOnAction(actionEvent -> {
            //TODO get date and daySpinner, and fire result to an object passed into the constructor to deal with
            //selections.
            //We could even define the selection set as a generic which the MealPlanConfigurator could be typed on,
            //but that isn't necessary here.
            LocalDate selectedDate = datePicker.getValue();
            Integer daysInPlan = daySpinner.getValue();
            Configuration configuration = new Configuration(selectedDate, daysInPlan);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle();
            alert.setContentText("Created MealPlan from " + selectedDate);
            alert.show();
            if (useTemplate.isSelected()) {
                consumer.accept(new MealPlanPanel(MealPlanTemplates.makePlanFromTemplate(mealPlanManager.loadTemplate(), selectedDate, daysInPlan), dishList, mealPlanManager));
            } else {
                consumer.accept(new MealPlanPanel(configuration, dishList, mealPlanManager));
            }
        });
        getChildren().add(ok);

    }

    public static MealPlanPanel makePanel(LocalDate startDate, ObservableList<Dish> dishList, MealPlanManager mealPlanManager) {
        Configuration configuration = new Configuration(startDate, 14);
        return new MealPlanPanel(configuration, dishList, mealPlanManager);
    }

    public static class Configuration {
        final LocalDate date;
        final int days;

        public static Configuration defaultConfig() {
            return new Configuration(LocalDate.now(), 14);
        }

        public static Configuration defaultConfig(LocalDate date) {
            return new Configuration(date, 14);
        }
        public Configuration(LocalDate date, int days) {
            this.date = date;
            this.days = days;
        }

        public LocalDate getDate() {
            return date;
        }

        public int getDays() {
            return days;
        }
    }
}
