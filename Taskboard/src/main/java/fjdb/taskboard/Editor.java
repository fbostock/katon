package fjdb.taskboard;

import fjdb.taskboard.tasks.TaskBuilder;
import fjdb.taskboard.tasks.TaskItem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Created by Frankie Bostock on 19/08/2017.
 */
public class Editor {
    private TaskBuilder builder;
    private TextField title;
    private TextArea contents;
    private Label idLabel;
    private final Pane pane;

    /**
     * Convenience contructor to make an editor from a TaskItem, rather than a TaskBuilder.
     * @param task
     */
    public Editor(TaskItem task) {
        this(new TaskBuilder(task));
    }

    public Editor(TaskBuilder builder) {
        this.builder = builder;
        title = new TextField(builder.getTitle());
        //TODO replace the textArea with HTMLEditor to support rich text
        contents = new TextArea(builder.getContents());
        idLabel = new Label(builder.getTaskId().toString());
        pane = new VBox();
        HBox vBox = new HBox();
        vBox.getChildren().addAll(idLabel, title);
        pane.getChildren().addAll(vBox, contents);
        TaskBoardUtils.configureBorder(pane);
    }

    public Node getNode() {
        return pane;
    }

    public TaskItem getEditedTask() {
        //alternatively, we could generate each field to add a listener which updates the builder, but that is not
        //necessarily ideal for text fields where someone is typing. For dropdown menus etc, it would make sense.
        builder.setTitle(title.getText());
        builder.setContents(contents.getText());
        return builder.makeTask();
    }

}
