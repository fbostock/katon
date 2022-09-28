package fjdb.mealplanner.search;

import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.loaders.CompositeDishLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class SearchSelectorPopupFx extends Stage {

    private TextField textField;
    private SearchModel<Dish> model;

    public static SearchSelectorPopupFx launchDialog(javafx.scene.control.TextField field) {
        return new SearchSelectorPopupFx(field);
    }

    public SearchSelectorPopupFx(javafx.scene.control.TextField field) {
        textField = field;
        List<Dish> dishes = new CompositeDishLoader(DaoManager.PRODUCTION).getDishes();

        initModality(Modality.NONE);

        model = new SearchModel<>(dishes, Dish::toString);
        ObservableList<Dish> items = FXCollections.observableArrayList(dishes);
        ListView<Dish> listView = new ListView<>(items);

        Scene scene = new Scene(listView, 300, 400);
        setScene(scene);

        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, dish, t1) -> {
            if (t1 != null) {
                textField.setText(t1.getName());
            }
        });
        model.addObserver(() -> {

            List<Dish> matches = model.getMatches();
            Platform.runLater(() -> {
                items.clear();
                items.addAll(matches);
            });
        });

        updateTextField(textField);
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
