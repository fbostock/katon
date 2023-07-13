package fjdb.interviews.play.dataprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessor {

    private ExecutorService executorService = Executors.newCachedThreadPool();


    private BlockingQueue<Update> queue;

    private Map<DataKey, DataStore> dataStores = new HashMap<>();

    public MessageProcessor(BlockingQueue<Update> queue) {
        this.queue = queue;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Update poll = queue.take();
                        executorService.submit(() -> dataStores.get(poll.getDataKey()).add(poll.getPrice(), poll.getTime()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                }
            }
        });
    }

    public void addDataStore(DataKey dataKey, DataStore dataStore) {
        dataStores.put(dataKey, dataStore);
    }
}
