package fjdb.taskboard;

import fjdb.fxutil.ApplicationBoard;
import fjdb.fxutil.FxUtils;
import fjdb.taskboard.tasks.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Frankie Bostock on 11/06/2017.
 */
public class TaskBoard extends Application {

    private TaskPaneManager taskPaneManager;
    private TaskManager manager;

    /*
            TODO we may want to define our own abstract class which extends Node, so we can control/enforce the decoration of
            various node classes, such as wrapping them so they are draggable etc.


            TODO should we consider when making changes to tasks, to only send updates of the properties which were
            changed, rather than build a whole taskItem? It would result in only sending an UpdateBuilder for a given task
            to the task manager to update? But how will we know which fields of the update are set? Perhaps would just have to
            iterate over all of them - wouldn't be too bad I guess, unless several events might be fired for each property
            which was changed for instance. But I guess you could check whether a parameter has changed.

             */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("TaskBoard");


        manager = new TaskManager(new TaskLoader());
        List<TaskItem> tasks = manager.getTasks();


        ApplicationBoard applicationBoard = new ApplicationBoard();
        applicationBoard.setupBoard(new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    //TODO any double click actions? Maybe that to create new task
                    TaskItem newTask = manager.createNewTask();
                    taskPaneManager.add(newTask);
                }
            }
        }, new Consumer<ContextMenuEvent>() {
            @Override
            public void accept(ContextMenuEvent contextMenuEvent) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem item = new MenuItem("Create new task");
                item.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        TaskItem newTask = manager.createNewTask();
                        taskPaneManager.add(newTask);

                    }
                });
                contextMenu.getItems().add(item);
                contextMenu.show(applicationBoard.getScene().getWindow(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());

            }
        });

        taskPaneManager = new TaskPaneManager(applicationBoard, tasks);

        manager.addListener(taskPaneManager);


        final BorderPane sceneRoot = new BorderPane();

        BorderPane.setAlignment(applicationBoard, Pos.TOP_LEFT);
        sceneRoot.setCenter(applicationBoard);

        final CheckBox dragModeCheckbox = new CheckBox("Drag mode");
        BorderPane.setMargin(dragModeCheckbox, new Insets(6));
        sceneRoot.setBottom(dragModeCheckbox);

        final Scene scene = new Scene(sceneRoot, 600, 600);
        primaryStage.setScene(scene);
//        stage.setTitle("Draggable Panels Example");
        primaryStage.show();
    }

    public Node getPane(TaskItem taskItem) {
        return new TaskNode(manager, taskItem).getNode();
    }

    private class TaskNode {

        //TODO we might want to store a builder with the task node, and treat it as a mutable taskItem. So to commit changes
        //we can just set the value to the builder, rather than constantly update the task item.
        private final TextArea contentsField;
        private final Node node;
        private final Label titleField;
        private final TaskBuilder builder;
        //TODO this class could be put in a factory which is supplied with the taskManager. This object could be a non-static
        //inner class which references that manager reference directly.
        public TaskNode(TaskManager manager, TaskItem taskItem){
            VBox pane = new VBox();
            builder = new TaskBuilder(taskItem);


            HBox title = new HBox();
            titleField = new Label(builder.getTitle());
            titleField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    builder.setTitle(titleField.getText());
                }
            });
            title.getChildren().add(titleField);
            HBox contents = new HBox();
            contentsField = new TextArea(builder.getContents());
            contents.getChildren().add(contentsField);
            pane.getChildren().addAll(title, contents);
            contentsField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    builder.setContents(contentsField.getText());
                }
            });

            //TODO make the context menu add options including save (to save edits to textField), as well as an edit
            //which will then popup the editor.
            //Might also want to look at looking at events on the text field to save everytime a key is pressed, but with
            //some caching to reduce overhead.

            TaskBoardUtils.configureBorder(pane);
            //add mouse click actions. Need to use the same event type as that used on the panelsPane, in order to consume the event.
            pane.setOnContextMenuRequested(event -> {

                ContextMenu menu = new ContextMenu();
                MenuItem delete = new MenuItem("Delete");
                delete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        manager.deleteTask(taskItem);
                    }
                });
                MenuItem addChild = new MenuItem("Add child task");
                addChild.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        TaskItem child = manager.createNewTask(taskItem.getTaskId());
                        //TODO open in editor, using code from edit method (but for the child task)
                    }
                });
                MenuItem edit = new MenuItem("Edit");
                edit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ButtonType okButton = ButtonType.OK;
                        ButtonType cancelButton = ButtonType.CANCEL;
                        Dialog<ButtonType> dialog = new Dialog<>();
                        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
                        Editor editor = getEditor(builder);
                        dialog.getDialogPane().setContent(editor.getNode());

                        Optional<ButtonType> result = dialog.showAndWait();
                        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                            manager.updateTask(editor.getEditedTask());
                        }

                    }
                });
                MenuItem save = new MenuItem("Save");
                save.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        manager.updateTask(builder.makeTask());
                    }
                });
                menu.getItems().addAll(save, edit, delete);
                menu.show(pane, event.getScreenX(), event.getScreenY());
                event.consume();
            });
            node = FxUtils.makeDraggable(pane);

        }

        public Node getNode() {
            return node;
        }
    }


    private Editor getEditor(TaskItem task) {
        return new Editor(task);
    }
    private Editor getEditor(TaskBuilder builder) {
        return new Editor(builder);
    }

    private class TaskPaneManager implements TaskListener {
        private final Pane panelsPane;
        int nextXlocation = 0;
        int nextYLocation = 60;
        private Map<TaskId, Node> nodesById = new HashMap<>();

        public TaskPaneManager(Pane panelsPane, List<TaskItem> tasks) {
            this.panelsPane = panelsPane;
            for (TaskItem task : tasks) {
                add(task);
            }
        }

        public void add(TaskItem task) {
            Node pane = getPane(task);
            pane.relocate(nextXlocation, nextYLocation);
            nextYLocation += 20;
            nextXlocation += 20;
            panelsPane.getChildren().addAll(pane);
            nodesById.put(task.getTaskId(), pane);
        }

        @Override
        public void update(TaskEvent event) {
            TaskItem task = event.getTaskItem();
            if (event.isDeleted()) {
                Node node = nodesById.get(task.getTaskId());
                panelsPane.getChildren().remove(node);
                nodesById.remove(task.getTaskId());
            } else if (event.isModified() || event.isAdded()) {
                Node node = nodesById.get(task.getTaskId());
                if (node != null) {
                    double layoutX = node.getLayoutX();
                    double layoutY = node.getLayoutY();
                    Node updatedNode = getPane(task);
                    updatedNode.relocate(layoutX, layoutY);
                    panelsPane.getChildren().remove(node);
                    panelsPane.getChildren().add(updatedNode);
                    nodesById.put(task.getTaskId(), updatedNode);
                } else {
                    System.out.println("This should not have happened");
                }
            }
        }



    }



}
