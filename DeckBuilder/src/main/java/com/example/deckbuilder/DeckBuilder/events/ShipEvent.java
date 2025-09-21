package com.example.deckbuilder.DeckBuilder.events;

import com.example.deckbuilder.DeckBuilder.model.ships.Ship;

public class ShipEvent extends Event {

    private final Ship ship;

    public ShipEvent(EventType eventType, Ship ship) {
        super(eventType);
        this.ship = ship;
    }

    public Ship getShip() {
        return ship;
    }

    public <T> T accept(EventHandler<T> eventHandler) {
        return eventHandler.handle(this);
    }
}
