package fjdb.interviews.play.dataprocessing;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class DataProcessorPlay implements MessageReceiver {

    private Bpipe bpipe;
    private BlockingQueue<Update> messageQueue;

    public static void main(String[] args) {
        /*
        Have a process which subscribes to some external interface for messages. Each Message will contain a subscriptionId,
        a time, and a value (price).

        We identify that instrument from the subscriptionid.

        We send to a service that handles messages - or rather, to a queue, to which a executor service is reading to process jobs.

        Could have a queue per instrument, as long as worker threads can visit all queues.

        If we just have one queue, the worker threads take the message and append to the appropriate data store.

        TODO continue here Saturday.
        Create a dependent data store listening to updates from others. This will build our swap curve (a list of points).
        I want a thread which checks these dependent data stores to see if there are enough updates or to check time since
        last update, and schedule curve job accordingly.
         To determine next job, iterate and find which was one has the greatest T*U value, where T time since last update,
         and U number of updates.
        This job gets sent to black box, and forgotten about (we assume downstream processes are notified).

        10 in 100s, 10 in 50s, prefer 100s one, so use U*T to determine most necessary.
        10 in 100s, 20 in 100s, prefer 20 one



         */


    }

    private Map<SubscriptionId, DataKey> subscriptionMap = new HashMap<>();

    public DataProcessorPlay(Bpipe bpipe, BlockingQueue<Update> messageQueue) {
        this.bpipe = bpipe;
        this.messageQueue = messageQueue;
        bpipe.addMessageReceiver(this);
    }

    @Override
    public void process(Message message) {
        Update update = new Update(subscriptionMap.get(message.getSubscriptionId()), message.getPrice(), message.getTime());
        messageQueue.add(update);
    }

    public interface Bpipe {
        SubscriptionId subscribe(DataKey dataKey);

        void addMessageReceiver(MessageReceiver messageReceiver);
    }

    public void subscribe(DataKey dataKey) {
        subscriptionMap.put(bpipe.subscribe(dataKey), dataKey);
    }
}
