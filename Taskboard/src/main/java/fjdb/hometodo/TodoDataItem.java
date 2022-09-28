package fjdb.hometodo;

import fjdb.databases.DataItemIF;
import org.apache.tools.ant.taskdefs.Local;

import java.time.LocalDate;
import java.util.Objects;

public class TodoDataItem implements DataItemIF {

    private final String name;
    private final Owner owner;
    private final Category category;
    private final Term term;
    private final Size size;
    private final Progress progress;
    private LocalDate dueDate;
    private Integer priority;

    /*
        name
        owner (Kate, Frankie, Both, Any)
        Category (House, Garden)
        Term (One-off, Ongoing)
         */
    public TodoDataItem(String name, Owner owner, Category category, Term term, Size size, Progress progress, LocalDate dueDate, Integer priority) {

        this.name = name;
        this.owner = owner;
        this.category = category;
        this.term = term;
        this.size = size;
        this.progress = progress;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Owner getOwner() {
        return owner;
    }

    public Category getCategory() {
        return category;
    }

    public Term getTerm() {
        return term;
    }

    public Size getSize() {
        return size;
    }

    public Progress getProgress() {
        return progress;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoDataItem that = (TodoDataItem) o;
        return Objects.equals(name, that.name) && owner == that.owner && category == that.category && term == that.term && size == that.size && progress == that.progress && Objects.equals(dueDate, that.dueDate) && Objects.equals(priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner, category, term, size, progress, dueDate, priority);
    }

    @Override
    public String toString() {
        return "TodoDataItem{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", category=" + category +
                ", term=" + term +
                ", size=" + size +
                ", progress=" + progress +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                '}';
    }
}
