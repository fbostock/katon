package com.example.deckbuilder.DeckBuilder.events;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;

public class GameEvent extends Event {
    public GameEvent(Card card, EventType eventType) {
        super(eventType);
    }

    @Override
    public <T> T accept(EventHandler<T> eventHandler) {
        return eventHandler.handle(this);
    }
}
