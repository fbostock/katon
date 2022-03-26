package fjdb.mealplanner.fx;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import javafx.application.Application;
import javafx.collections.FXCollections;
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
        Set<String> tags = Sets.newHashSet("Tag1", "Tag2", "Tag3", "OtherTag", "NullTag");
        return new SelectionPanel<>(tags, s -> s);
    }

    private Node getDishTagSelectionPanel() {
        Multimap<Integer, String> dishesToTags = ArrayListMultimap.create();
        dishesToTags.putAll(1, Sets.newHashSet("Tag1", "Tag2", "Tag3", "OtherTag", "NullTag"));
        dishesToTags.putAll(2, Sets.newHashSet("Tag1", "OtherTag", "NullTag"));
        dishesToTags.putAll(3, Sets.newHashSet("OtherTag", "NullTag"));
        //Note: doing the following would mean 9 would not appear in the map, as mappings require elements
//        dishesToTags.putAll(9, Sets.newHashSet());

        Set<String> tags = Sets.newHashSet("Tag1", "Tag2", "Tag3", "OtherTag", "NullTag");
        CategorySelectionPanel<Integer, String> panel = new CategorySelectionPanel<>(tags, dishesToTags, s -> s);
        panel.includeDishSelector(FXCollections.observableList(Lists.newArrayList(1, 2, 3, 4)));
        return panel;
    }
}
