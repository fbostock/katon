package fjdb.mealplanner.events;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class EventProcessor {

    private static EventProcessor eventProcessor = new EventProcessor();
    private final List<MealEventListener> listeners = new ArrayList<>();

    public static EventProcessor getInstance() {
        return eventProcessor;
    }

    public void processEvent(MealEvent mealEvent) {
        Runnable runnable = () -> {
            for (MealEventListener listener : listeners) {
                listener.processEvent(mealEvent);
            }
        };
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public void addListener(MealEventListener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(MealEventListener listener) {
        if (listeners.contains(listener)) {
            return listeners.remove(listener);
        }
        return false;
    }
}
