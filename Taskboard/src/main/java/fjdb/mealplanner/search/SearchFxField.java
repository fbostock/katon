package fjdb.mealplanner.search;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;


public class SearchFxField extends Application {

    public static void main(String[] args) {
launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        final BorderPane sceneRoot = new BorderPane();

        TextField textField = new TextField("Write...");
        ObservableList<String> inputList = FXCollections.observableArrayList();
        inputList.add("First");
        inputList.add("Second");
        inputList.add("Third");


        ListView<String> listView = new ListView<>(inputList);
        FlowPane pane = new FlowPane(Orientation.VERTICAL);

        listView.selectionModelProperty().addListener(new ChangeListener<MultipleSelectionModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends MultipleSelectionModel<String>> observableValue, MultipleSelectionModel<String> stringMultipleSelectionModel, MultipleSelectionModel<String> t1) {
                //TODO apply selected option
            }
        });
        pane.getChildren().add(textField);
        pane.getChildren().add(listView);
//        TableView<String> tableView = new TableView<>(inputList);
//        pane.getChildren().add(tableView);
//        TableColumn<String, String> column = new TableColumn<>("Name");
//        column.setCellValueFactory(x -> Bindings.createObjectBinding(x::getValue));
//        column.setPrefWidth(tableView.getPrefWidth()-2);
//        column.prefWidthProperty().bind(tableView.widthProperty());
//        column.setStyle("-fx-background-color: white");
//        tableView.getColumns().add(column);
        //Table should have no header
        //Table should have plain white background
        //Table should have one single column


        sceneRoot.setCenter(pane);



        final Scene scene = new Scene(sceneRoot, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
