package fjdb.taskboard.tasks;

/**
 * Created by Frankie Bostock on 17/08/2017.
 */
public class TaskEvent {

    public enum TaskEventType {ADD, MODIFY, DELETE, OTHER}

    private TaskItem taskItem;
    private TaskItem oldTaskItem;
    private TaskEventType type;

    public TaskEvent(TaskItem taskItem, TaskEventType type) {
        this.taskItem = taskItem;
        this.type = type;
    }

    public TaskEvent(TaskItem taskItem, TaskItem oldTask, TaskEventType type) {
        this.taskItem = taskItem;
        this.type = type;
        this.oldTaskItem = oldTask;
    }

    public TaskItem getTaskItem() {
        return taskItem;
    }

    public TaskItem getOldTaskItem() {
        return oldTaskItem;
    }

    public TaskEventType getType() {
        return type;
    }

    public boolean isDeleted() {
        return type.equals(TaskEventType.DELETE);
    }

    public boolean isModified() {
        return type.equals(TaskEventType.MODIFY);
    }

    public boolean isAdded() {
        return type.equals(TaskEventType.ADD);
    }

}
