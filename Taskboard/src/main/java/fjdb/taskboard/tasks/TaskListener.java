package fjdb.taskboard.tasks;

import fjdb.taskboard.tasks.TaskEvent;

/**
 * Created by Frankie Bostock on 19/08/2017.
 */
public interface TaskListener {
    public void update(TaskEvent event);
}
