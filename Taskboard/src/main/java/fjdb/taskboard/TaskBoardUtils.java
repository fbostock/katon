package fjdb.taskboard;

import fjdb.taskboard.tasks.TaskType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Created by Frankie Bostock on 22/07/2017.
 */
public class TaskBoardUtils {

    /*TODO this makes a node draggable, but does this enforce the node is limited to where it can be dragged i.e. only within the parent
    node that it is in? If not, then we might need to pass in the parent node and apply a contraint somehow.
      */
//    public static Node makeDraggable(Node node) {
//        DragContext dragContext = new DragContext();
//        Group group = new Group(node);
//        group.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
////                event.consume();
//            }
//        });
//
//        group.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                group.toFront();
//                dragContext.mouseAnchorX = event.getX();
//                dragContext.mouseAnchorY = event.getY();
//                dragContext.initialTranslateX = node.getTranslateX();
//                dragContext.initialTranslateY = node.getTranslateY();
//            }
//        });
//
//        group.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                node.setTranslateX(event.getX() - dragContext.mouseAnchorX + dragContext.initialTranslateX);
//                node.setTranslateY(event.getY() - dragContext.mouseAnchorY + dragContext.initialTranslateY);
//            }
//        });
//        return group;
//    }

//    private static class DragContext {
//        double mouseAnchorX;
//        double mouseAnchorY;
//        double initialTranslateX;
//        double initialTranslateY;
//    }

    public static void configureBorder(final Region region) {
        region.setStyle("-fx-background-color: white;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 6;"
                + "-fx-padding: 6;");
    }

    public ChoiceBox<TaskType> getTaskType() {
        ChoiceBox<TaskType> choiceBox = new ChoiceBox<>();
        choiceBox.setItems(FXCollections.observableArrayList(TaskType.values()));
        return choiceBox;
    }

}
