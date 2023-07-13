package fjdb.mealplanner.search;

import com.google.common.collect.Sets;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.MealPlanManager;
import fjdb.mealplanner.loaders.CompositeDishLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.TreeSet;

public class SearchSelectorPopupFx extends Stage implements SearchObserver {

    private TextField textField;
    private SearchModel<Dish> model;
private ObservableList<Dish> items;

    public static SearchSelectorPopupFx launchDialog(javafx.scene.control.TextField field) {
        return new SearchSelectorPopupFx(field);
    }

    public SearchSelectorPopupFx(javafx.scene.control.TextField field) {
        textField = field;
        TreeSet<Dish> dishesSet = Sets.newTreeSet(new CompositeDishLoader(DaoManager.PRODUCTION).getDishes());
//        List<Dish> dishes = new CompositeDishLoader(DaoManager.PRODUCTION).getDishes();
        dishesSet.addAll(MealPlanManager.DishManager.getInstance().getAll());
        List<Dish> dishes = dishesSet.stream().toList();

        initModality(Modality.NONE);

        model = new SearchModel<>(dishes, Dish::toString);
//        ObservableList<Dish> items = FXCollections.observableArrayList(dishes);
        items = FXCollections.observableArrayList(dishes);
        ListView<Dish> listView = new ListView<>(items);

        Scene scene = new Scene(listView, 300, 400);
        setScene(scene);

        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, dish, t1) -> {
            if (t1 != null) {
                textField.setText(t1.getName());
                textField.getOnAction().handle(new ActionEvent());
//                textField.commitValue();
            }
        });
        model.addObserver(this);

        updateTextField(textField);
    }

    @Override
    public void update() {
        List<Dish> matches = model.getMatches();
        Platform.runLater(() -> {
            items.clear();
            items.addAll(matches);
        });
    }

    public void updateTextField(TextField field) {
        textField.setOnKeyTyped(null);
        textField = field;
        textField.setOnKeyTyped(keyEvent -> {
            String searchText = textField.getText();
            model.searchInBackground(searchText);
        });
    }
}
