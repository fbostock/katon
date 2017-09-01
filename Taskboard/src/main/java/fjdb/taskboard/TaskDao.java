package fjdb.taskboard;

import fjdb.taskboard.tasks.TaskItem;

import java.util.List;

/**
 * Created by Frankie Bostock on 23/07/2017.
 */
public interface TaskDao {

    public List<TaskItem> loadTasks();

    public TaskItem addTask(TaskItem task);

    public boolean deleteTask(TaskItem task);

    public boolean update(TaskItem task);

    //TODO add create, Read, Update and Delete methods, based around a TaskId

}
