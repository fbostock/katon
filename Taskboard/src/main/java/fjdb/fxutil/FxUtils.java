package fjdb.fxutil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FxUtils {

    public static Stage makeDialog(EventHandler<ActionEvent> okAction, Node content) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
//                dialog.initOwner(primaryStage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(content);

//        dialogVbox.getChildren().add(new Text("Insert New Dish"));
//        HBox dishName = new HBox();
//        dishName.getChildren().add(new Text("Dish Name"));
//        TextField dishNameField = new TextField();
//        TextField dishDetailsField = new TextField();
//        dishName.getChildren().add(dishNameField);
//        HBox dishDetails = new HBox();
//        dishDetails.getChildren().add(new Text("Dish Details"));
//        dishDetails.getChildren().add(dishDetailsField);
//        dialogVbox.getChildren().add(dishName);
//        dialogVbox.getChildren().add(dishDetails);
//        DishTagDao dishTagDao = daoManager.getDishTagDao();
//        Multimap<Dish, DishTag> dishesToTags = dishTagDao.getDishesToTags();
//        Set<DishTag> tags = dishTagDao.getTags(false);
//        DishTagSelectionPanel filterPanel = new DishTagSelectionPanel(tags, dishesToTags);
//        dialogVbox.getChildren().add(filterPanel);


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
}
