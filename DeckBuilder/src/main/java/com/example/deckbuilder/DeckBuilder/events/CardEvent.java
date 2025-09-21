package com.example.deckbuilder.DeckBuilder.events;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;

public class CardEvent extends Event {

    private final Card card;

    public CardEvent(Card card, EventType eventType) {
        super(eventType);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public <T> T accept(EventHandler<T> eventHandler) {
        return eventHandler.handle(this);
    }
}

