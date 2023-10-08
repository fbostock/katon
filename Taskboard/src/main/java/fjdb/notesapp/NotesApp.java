package fjdb.notesapp;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
