package fjdb.taskboard.tasks;

/**
 * Created by Frankie Bostock on 26/07/2017.
 */
public class TaskBuilder {

    private String title = "Title";
    private String contents = "";
    private TaskId taskId;
    private TaskId parentTaskId = TaskId.NULL;
    private TaskType taskType = TaskType.EPIC;

    public TaskBuilder() {
    }

    public TaskBuilder(TaskItem task) {
        setTaskId(task.getTaskId());
        setTitle(task.getTitle());
        setContents(task.getContents());
        setParentTaskId(task.getParentTaskId());
    }

    public String getTitle() {
        return title;
    }

    public TaskBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public TaskBuilder setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public TaskId getTaskId() {
        return taskId;
    }

    public TaskBuilder setTaskId(TaskId taskId) {
        this.taskId = taskId;
        return this;
    }

    public TaskId getParentTaskId() {
        return parentTaskId;
    }

    public TaskBuilder setParentTaskId(TaskId parentTaskId) {
        this.parentTaskId = parentTaskId;
        return this;
    }

    public TaskItem makeTask() {
        return new TaskItem(this);
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}
