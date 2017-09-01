package fjdb.taskboard.tasks;

/**
 * Created by Frankie Bostock on 23/07/2017.
 */
public class TaskId {

    public static final TaskId NULL = new TaskId(-1);

    private final Integer id;

    public TaskId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskId taskId = (TaskId) o;

        return id != null ? id.equals(taskId.id) : taskId.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TaskId" + getId();
    }
}
