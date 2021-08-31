package fjdb.mealplanner.fx;

import com.google.common.collect.Multimap;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.DishTag;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Set;

public class DemoOfComponents extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);
        mainTabs.getTabs().add(new Tab("SelectionPanel", getSelectionPanel()));
        mainTabs.getTabs().add(new Tab("DishTagSelectionPanel", getDishTagSelectionPanel()));

        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();
    }

    private Node getSelectionPanel() {
        Set<DishTag> tags = DaoManager.PRODUCTION.getDishTagDao().getTags(true);
        return new SelectionPanel<>(tags, DishTag::getLabel);
    }

    private Node getDishTagSelectionPanel() {
        Multimap<Dish, DishTag> dishesToTags = DaoManager.PRODUCTION.getDishTagDao().getDishesToTags();
        Set<DishTag> tags = DaoManager.PRODUCTION.getDishTagDao().getTags(true);
        CategorySelectionPanel<Dish, DishTag> panel = new CategorySelectionPanel<>(tags, dishesToTags, DishTag::getLabel);
        panel.includeDishSelector();
        return panel;
    }
}
