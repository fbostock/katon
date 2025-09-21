package frankiemedeslabs.fxutil;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.List;

public class FxDemos {

    /**
     * Add some simple prep to a stage, return a TabPane. Used to quickly demo or test something out.
     */
    public static TabPane prepareStage(Stage stage) {
        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);
        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);
        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();
        return mainTabs;
    }

    public Border borderExample() {
        return new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    }

    public static MenuButton addTrigramMenuButton() {
        return addTrigramMenuButton(List.of(new MenuItem("Settings"), new MenuItem("About")));
    }


    public static MenuButton addTrigramMenuButton(List<MenuItem> menuItems) {
        Line l1 = new Line(0.5, 0, 15.5, 0);
        l1.setStrokeWidth(2);
        Line l2 = new Line(0.5, 5, 15.5, 5);
        l2.setStrokeWidth(2);
        Line l3 = new Line(0.5, 10, 15.5, 10);
        l3.setStrokeWidth(2);

        MenuButton m = new MenuButton();
        m.setGraphic(new Group(l1, l2, l3));
        m.getItems().addAll(menuItems);

        Platform.runLater(() ->
        {
            // hide the arrow of menuButton
            m.lookup(".arrow").setStyle("-fx-background-insets: 0; -fx-padding: 0; -fx-shape: null;");
            // hide the arraw-button pane, to remove unnecessary padding
            m.lookup(".arrow-button").setStyle("-fx-padding: 0");
        });
        return m;
    }
}
