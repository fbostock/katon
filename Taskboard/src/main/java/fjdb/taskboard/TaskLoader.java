package fjdb.taskboard;

import fjdb.taskboard.tasks.TaskBuilder;
import fjdb.taskboard.tasks.TaskId;
import fjdb.taskboard.tasks.TaskItem;
import fjdb.taskboard.tasks.TaskType;
import fjdb.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frankie Bostock on 23/07/2017.
 */
public class TaskLoader implements TaskDao {

    //TODO A dedicated class should handle the storage, and add the user to create the folder if required.
    private static final File home = new File(System.getenv("HOME"));
    private static final File defaultTaskDir = new File(home, "Desktop" + File.separator + "TaskBoard");
    private static final File xmlDirectory = new File(defaultTaskDir, "xmls");
    private static final File deletedDirectory = new File(defaultTaskDir, "deleted");

    private Integer nextId = -1;
    @Override
    public List<TaskItem> loadTasks() {
        ArrayList<TaskItem> tasks = new ArrayList<>();

        File[] xmlFiles = xmlDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(".xml");
            }
        });

        if (xmlFiles != null) {
            for (File xmlFile : xmlFiles) {
                List<TaskItem> readTasks = read(xmlFile);
                if (readTasks.isEmpty()) {
                    System.out.println("There were no tasks parsed from " + xmlFile);
                } else {
                    tasks.addAll(readTasks);
                }
            }
        }
        //TODO may want to perform checking on TaskId to ensure they are unique.
        Integer highestId = -1;
        for (TaskItem task : tasks) {
            if (task.getTaskId().getId() > highestId) {
                highestId = task.getTaskId().getId();
            }
        }
        nextId = ++highestId;
        return tasks;
    }

    /**
     * Generates the next unique TaskId
     */
    private TaskId nextId() {
        return new TaskId(nextId++);
    }

    @Override
    public TaskItem addTask(TaskItem task) {
        try {
            TaskBuilder builder = new TaskBuilder(task);
            builder.setTaskId(nextId());
            TaskItem newTask = builder.makeTask();
            write(newTask);
            return newTask;
        } catch (IOException e) {
            //TODO handle exception
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteTask(TaskItem task) {
        File file = new File(xmlDirectory, getFilename(task));
        return FileUtils.moveFile(file, deletedDirectory);
    }

    @Override
    public boolean update(TaskItem task) {
        try {
            //TODO we could attempt to save a history of files for a given task.
            write(task);
        } catch (IOException e) {
            //TODO handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private void write(TaskItem task) throws IOException {
        new XmlWriter(task, new File(xmlDirectory, getFilename(task)));
    }

    public static void main(String[] args) throws IOException {

        TaskLoader taskLoader = new TaskLoader();
        List<TaskItem> tasks = taskLoader.loadTasks();

        for (TaskItem taskItem : tasks) {
            System.out.println(taskItem.getTitle());
            taskLoader.write(taskItem);
        }

    }

    private List<TaskItem> read(File inputFile) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userhandler = new UserHandler();
            saxParser.parse(inputFile, userhandler);

            return userhandler.getTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private String getFilename(TaskItem task) {
        return task.getTaskId() + ".xml";
    }

    private static class UserHandler extends DefaultHandler {
        private TaskBuilder builder;

        //TODO check what the default behaviour is for elements
        private XmlTag currentTag = null;
        private final List<TaskItem> tasks;
        String content = "";

        public UserHandler() {
            tasks = new ArrayList<>();
            builder = new TaskBuilder();
        }

        public List<TaskItem> getTasks() {
            return tasks;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            //super.startElement(uri, localName, qName, attributes);
            switch (XmlTag.getValue(qName)) {
                case TASKITEM:
                    builder = new TaskBuilder();
                    break;
                case TITLE:
                    currentTag = XmlTag.TITLE;
                    break;
                case TASKID:
                    currentTag = XmlTag.TASKID;
                    builder.setTaskId(new TaskId(Integer.parseInt(attributes.getValue("value"))));
                    break;
                case PARENTID:
                    currentTag = XmlTag.PARENTID;
                    builder.setParentTaskId(new TaskId(Integer.parseInt(attributes.getValue("value"))));
                    break;
                case CONTENTS:
                    currentTag = XmlTag.CONTENTS;
                    content = "";
                    break;
                case TASKTYPE:
                    currentTag = XmlTag.TASKTYPE;
                    builder.setTaskType(TaskType.valueOf(attributes.getValue("value")));
                    break;
                default:
                    currentTag = null;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (currentTag != null) {
                switch (currentTag) {
                    case TASKITEM:
                        break;
                    case TITLE:
                        builder.setTitle(new String(ch, start, length));
                        break;
                    case CONTENTS:
                        content += new String(ch, start, length);
                        break;
                }
            }
        }


        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (XmlTag.getValue(qName)) {
                case TASKITEM:
                    tasks.add(builder.makeTask());
                    break;
                case CONTENTS:
                    //for any block content between xml tags, need to gradually store it as the characters method goes over line by line.
                    //As oppposed to storing it as an attribute.
                    builder.setContents(content);
                    break;
            }
            currentTag = null;
        }
    }

    private static class XmlWriter {
        private File file;

        public XmlWriter(TaskItem task, File file) throws IOException {
            this.file = file;

            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter writer = new FileWriter(file);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                TaskId taskId = task.getTaskId();

                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                bufferedWriter.newLine();
                bufferedWriter.write(XmlTag.TASKITEM.getOpen());
                bufferedWriter.newLine();
                bufferedWriter.write(XmlTag.TASKID.getAttribute(taskId.getId()));
                bufferedWriter.newLine();
                bufferedWriter.write(XmlTag.TITLE.getEnclose(task.getTitle()));
                bufferedWriter.newLine();
                bufferedWriter.write(XmlTag.CONTENTS.getEnclose(task.getContents()));
                bufferedWriter.newLine();
                if (!task.getParentTaskId().equals(TaskId.NULL)) {
                    bufferedWriter.write(XmlTag.PARENTID.getAttribute(task.getParentTaskId().getId()));
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(XmlTag.TASKTYPE.getAttribute(task.getTaskType()));
                bufferedWriter.write(XmlTag.TASKITEM.getClose());
                bufferedWriter.newLine();

            }
        }

    }


    private enum XmlTag {

        TASKITEM("taskitem"),
        TITLE("title"),
        TASKID("taskid"),
        PARENTID("parentid"),
        CONTENTS("contents"),
        TASKTYPE("tasktype");

        private final String name;

        public static XmlTag getValue(String name) {
            return valueOf(name.toUpperCase());
        }

        XmlTag(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getOpen() {
            return "<" + name + ">";
        }

        public String getClose() {
            return "</" + name + ">";
        }

        public String getOpenClose() {
            return "<" + name + "/>";
        }

        public String getEnclose(Object value) {
            return getOpen() + value + getClose();
        }

        public String getAttribute(Object value) {
            return "<" + name + " value=\"" + value + "\"/>";
        }

    }

    /*
    Have a parallel set of enums xml tags, and xmlHandlers tags. 1 to 1 mapping.
    Each handler would define how to generate tags from a taskItem, whether a tag is required (e.g. parentId), and how to
    read a tag to generate a task item via a builder.
    Aim: to define in one place everything needed when adding a new tag/property.
     */
}
