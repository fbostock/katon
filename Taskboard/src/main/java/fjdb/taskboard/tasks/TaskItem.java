package fjdb.taskboard.tasks;

/**
 * Created by Frankie Bostock on 11/06/2017.
 */
public class TaskItem {

    private final String title;
    private final String contents;
    private final TaskId taskId;
    private final TaskId parentTaskId;
    private final TaskType taskType;

    /*
    task type: epic, story,
    id
    parentId
    state: none, planning, in progress, testing, done, blocked
    estimate
    linked ids: other tasks
    categories: selection from a list of topics such as "Database", "Dao", "JavaFx", "GUI", "multi-threading"
     */

    //TODO contruct the task from a builder, so a task can be built from any set of input variables.
    TaskItem(String title, String contents, TaskId taskId, TaskId parentTaskId, TaskType type) {
        this.title = title;
        this.contents = contents;
        this.taskId = taskId;
        this.parentTaskId = parentTaskId;
        this.taskType = type;
    }

    TaskItem(TaskBuilder builder) {
        title = builder.getTitle();
        contents = builder.getContents();
        taskId = builder.getTaskId();
        parentTaskId = builder.getParentTaskId();
        taskType = builder.getTaskType();
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public TaskId getTaskId() {
        return taskId;
    }

    public TaskId getParentTaskId() {
        return parentTaskId;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
