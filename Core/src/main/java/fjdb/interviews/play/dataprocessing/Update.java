package fjdb.interviews.play.dataprocessing;

import java.time.LocalDateTime;

public class Update {
    private final DataKey dataKey;
    private final Double price;
    private final LocalDateTime time;

    public Update(DataKey dataKey, Double price, LocalDateTime time) {
        this.dataKey = dataKey;
        this.price = price;
        this.time = time;
    }

    public DataKey getDataKey() {
        return dataKey;
    }

    public Double getPrice() {
        return price;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
