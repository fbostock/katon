package fjdb.shopping;

import java.util.ArrayList;
import java.util.List;

public class Department implements Comparable<Department> {

    private static final List<Department> departmentOrder = new ArrayList<>();

    private static int orderIndex = 0;
    public static final Department fruitVeg = of("Fruit+Veg");
    public static final Department meat = of("Meat");
    public static final Department baby = of("Baby");
    public static final Department toiletries = of("Toiletries");
    public static final Department lunches = of("Lunches");

    public static final Department dairy = of("dairy");
//    public static final Department yoghurtCheese = of("Yoghurt+Cheese");
    public static final Department milkJuiceSauces = of("Milk Juice Sauces");
    public static final Department pastaCansCondiments = of("Pasta + Cans + Condiments");
//    public static final Department herbsCondiments = of("Herbs+Condiments");
    public static final Department biscuits = of("Biscuits");
    public static final Department drinksCrisps = of("DrinksCrisps");
    public static final Department teaCofee = of("Tea Coffee");
    public static final Department cereals = of("Cereals");
//    public static final Department bakery = of("Bakery");
    public static final Department bakingBread = of("BakingBread");
    public static final Department frozen = of("Frozen");
    public static final Department booze = of("Booze");
    public static final Department cleaning = of("Cleaning products");
//    public static final Department  = of("");
//    public static final Department  = of("");
//    public static final Department  = of("");
//    public static final Department  = of("");



    public static final Department miscellaneous = of("Miscellaneous");//anything which doesn't fit to something else.


    public static List<Department> getDepartmentOrder() {
        return List.copyOf(departmentOrder);
    }

    private final String name;
    private final int index;

    public static Department of(String name) {
        Department department = new Department(name);
        departmentOrder.add(department);
        return department;
    }

    private Department(String name) {
        this.name = name;
        index = orderIndex++;
    }


    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Department o) {
        return Integer.compare(index, o.index);
    }
}
