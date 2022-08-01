package fjdb.hometodo;

import fjdb.databases.DataItemIF;

import java.util.Objects;

public class TodoDataItem implements DataItemIF {

    private final String name;
    private final Owner owner;
    private final Category category;
    private final Term term;
    private final Size size;

    /*
        name
        owner (Kate, Frankie, Both, Any)
        Category (House, Garden)
        Term (One-off, Ongoing)
         */
    public TodoDataItem(String name, Owner owner, Category category, Term term, Size size) {

        this.name = name;
        this.owner = owner;
        this.category = category;
        this.term = term;
        this.size = size;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoDataItem that = (TodoDataItem) o;
        return Objects.equals(name, that.name) && owner == that.owner && category == that.category && term == that.term && size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner, category, term, size);
    }
}
