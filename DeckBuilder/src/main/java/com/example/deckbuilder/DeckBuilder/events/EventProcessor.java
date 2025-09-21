package com.example.deckbuilder.DeckBuilder.events;

import fjdb.util.AbstractListenerCollection;

public class EventProcessor {

    private static final EventProcessor instance = new EventProcessor();

    public static EventProcessor getInstance() {
        return instance;
    }

    private final AbstractListenerCollection<EventListener> listeners = new AbstractListenerCollection<>();

    public void processEvent(Event event) {
        for (EventListener listener : listeners.getListeners()) {
            listener.processEvent(event);
        }
    }

    public void addListener(EventListener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(EventListener eventListener) {
        listeners.removeListener(eventListener);
    }
}
