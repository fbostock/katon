package fjdb.fxutil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class FxUtils {

    public static Stage makeDialog(EventHandler<ActionEvent> okAction, Node content) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
//                dialog.initOwner(primaryStage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(content);

        HBox okCancel = new HBox();
        Button ok = new Button("OK");
        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> dialog.close());
        EventHandler<ActionEvent> eventHandler = actionEvent -> {
            dialog.close();
            okAction.handle(actionEvent);
        };
        ok.setOnAction(eventHandler);
        okCancel.getChildren().add(ok);
        okCancel.getChildren().add(cancel);
        dialogVbox.getChildren().add(okCancel);

//        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        Scene dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
//        dialog.show();
        return dialog;
    }

    public static <T> ComboBox<T> makeCombo(List<T> inputs) {
        ObservableList<T> types = FXCollections.observableArrayList(inputs);
        return makeCombo(types);
    }

    public static <T> ComboBox<T> makeCombo(ObservableList<T> inputs) {
        return new ComboBox<>(inputs);
    }

    public static TextField getTextField() {
        return new TextField();
    }

    public static CheckBox getCheckBox() {
        return new CheckBox();
    }

    public static Button getButton(String label) {
        return new Button(label);
    }

    public static TabPane prepareStage(Stage stage) {
        return FxDemos.prepareStage(stage);
    }

}
