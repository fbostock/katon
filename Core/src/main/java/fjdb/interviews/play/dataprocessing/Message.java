package fjdb.interviews.play.dataprocessing;

import java.time.LocalDateTime;

public class Message {
    private final SubscriptionId subscriptionId;
    private final Double price;
    private final LocalDateTime time;

    public Message(SubscriptionId subscriptionId, Double price, LocalDateTime time) {
        this.subscriptionId = subscriptionId;
        this.price = price;
        this.time = time;
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public Double getPrice() {
        return price;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
