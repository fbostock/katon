package com.example.deckbuilder.DeckBuilder.events;

/**
 * An event handler to translate generic Event objects to Event specific implementation handling those events, e.g. CardEvents
 * or ShipEvents.
 * Each event will have an EventType. This allows Handler implementations to furhter delegate actions on the EventType, based
 * on the particular Event it is handling.
 * @param <T>
 */
public class EventHandler<T> {

    public T handle(Event event) {
        return null;
    }

    public T handle(GameEvent event) {
        return null;
    }

    public T handle(ShipEvent shipEvent) {
        return null;
    }
}
