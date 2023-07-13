package fjdb.shopping;

import java.util.Arrays;

public class Item {
    private String name;
    private Iterable<Department> departments;

    public static Item of(String name, Department... departments) {
        return new Item(name, departments);
    }


    public Item(String name, Iterable<Department> departments) {
        this.name = name;
        this.departments = departments;
    }

    public Item(String name, Department... departments) {
        this(name, Arrays.asList(departments));
    }

    public String getName() {
        return name;
    }

    public Iterable<Department> getDepartments() {
        return departments;
    }

    public Item copy() {
        return new Item(name, departments);
    }

    @Override
    public String toString() {
        return "Item " + name;
    }

}