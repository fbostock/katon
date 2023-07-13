package fjdb.notesapp;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NotesApp extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);

        mainTabs.getTabs().add(new Tab());


        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();
    }

    private Node createNoteWidget() {
        TextArea widget = new TextArea("Some Note Text");
        return widget;
    }
}
