package fjdb.taskboard;

import fjdb.taskboard.tasks.TaskBuilder;
import fjdb.taskboard.tasks.TaskEvent;
import fjdb.taskboard.tasks.TaskItem;
import fjdb.taskboard.tasks.TaskListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frankie Bostock on 11/06/2017.
 */
public class TaskManager {

    private List<TaskItem> tasks = new ArrayList<>();
    private TaskDao taskDao;
    private List<TaskListener> taskListeners = new ArrayList<>();

    public TaskManager(TaskDao taskDao) {
        this.taskDao = taskDao;
        //create some dummy tasks
        tasks.addAll(taskDao.loadTasks());
        //TODO populate epic and any pre-filtered collections of tasks

    }

    public List<TaskItem> getTasks() {
        return tasks;
    }

    public TaskItem createNewTask() {
        TaskBuilder builder = new TaskBuilder();
        TaskItem task = builder.makeTask();
        return taskDao.addTask(task);
    }

    public void updateTask(TaskItem task) {
        taskDao.update(task);
        updateListener(new TaskEvent(task, TaskEvent.TaskEventType.MODIFY));
    }

    public void deleteTask(TaskItem task) {
        taskDao.deleteTask(task);
        updateListener(new TaskEvent(task, TaskEvent.TaskEventType.DELETE));
    }

    public void addListener(TaskListener taskListener) {
        taskListeners.add(taskListener);
    }

    public void removeListener(TaskListener taskListener) {
        taskListeners.remove(taskListener);
    }

    private void updateListener(TaskEvent event) {
        for (TaskListener taskListener : taskListeners) {
            taskListener.update(event);
        }
    }

    //todo add a taskloader dao interface, one being the xmls
}
