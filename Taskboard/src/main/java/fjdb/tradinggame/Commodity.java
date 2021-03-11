package fjdb.tradinggame;

public enum Commodity {

    APPLE("Apple"),
    ORANGES("Orange");

    private String name;

    private Commodity(String apple) {
        this.name = apple;
    }

    public String getName() {
        return name;
    }


}
