package fjdb.fxutil;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
}
