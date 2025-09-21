package com.example.deckbuilder.DeckBuilder.events;

public abstract class Event {

    private final EventType eventType;

    public EventType getType() {
        return eventType;
    }

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    //TODO may need to separate out the events a bit, such that the view responds to one set of events, and the engine another.
    //e.g. selecting a card to fire an event is very different to the engine carrying something out and telling the view it needs to update,
    //or that further information is required, e.g. selecting ships to fight.

    public abstract <T> T accept(EventHandler<T> eventHandler);
}
