package fjdb.investments.tickers;

public record Ticker(String name) {

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
