package com.example.deckbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Note: vm args -p /Users/francisbostock/Code/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.web  -XX:+ShowCodeDetailsInExceptionMessages
*  This includes several modules to get javafx to work. Tried to use module-info but there seemed to be no easy way to use that and reference
 *  dependencies which should be imported into the unnamed module, at least running from maven.
 */
public class DeckBuilderGame extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DeckBuilderGame.class.getResource("deckbuilder-game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);


//        stage.setOpacity(0.1);
//        scene.setFill(Color.TRANSPARENT);
//        stage.initStyle(StageStyle.TRANSPARENT);
//        stage.initStyle(StageStyle.UNDECORATED);
//        stage.setResizable(false);
//        stage.centerOnScreen();

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}