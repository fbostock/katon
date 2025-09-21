package frankiemedeslabs.fxutil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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

    public static Node makeDraggable(Node node) {
        DragContext dragContext = new DragContext();
        Group group = new Group(node);
        group.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
//                event.consume();
            }
        });

        group.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                group.toFront();
                dragContext.mouseAnchorX = event.getX();
                dragContext.mouseAnchorY = event.getY();
                dragContext.initialTranslateX = node.getTranslateX();
                dragContext.initialTranslateY = node.getTranslateY();
            }
        });

        group.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                node.setTranslateX(event.getX() - dragContext.mouseAnchorX + dragContext.initialTranslateX);
                node.setTranslateY(event.getY() - dragContext.mouseAnchorY + dragContext.initialTranslateY);
            }
        });
        return group;
    }

    private static class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double initialTranslateX;
        double initialTranslateY;
    }

    public static void configureBorder(final Region region) {
        region.setStyle("-fx-background-color: white;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 6;"
                + "-fx-padding: 6;");
    }


}
