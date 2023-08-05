package fjdb.notesapp;

import com.google.common.collect.Lists;
import fjdb.fxutil.ApplicationBoard;
import fjdb.fxutil.FxUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class NotesApp extends Application {


    public NotesApp() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        NotesRepository repository = new NotesRepository();


        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);

        Tab noteTab = new Tab("Notes");
        mainTabs.getTabs().add(noteTab);
        noteTab.setContent(new NotesPane(repository, false).getApplicationBoard());

        Tab archiveTab = new Tab("Archive");
        mainTabs.getTabs().add(archiveTab);
        archiveTab.setContent(new NotesPane(repository, true).getApplicationBoard());



        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();

//        MenuButton menuButton = FxDemos.addTrigramMenuButton();
//        applicationBoard.getChildren().add(menuButton);


    }


}
