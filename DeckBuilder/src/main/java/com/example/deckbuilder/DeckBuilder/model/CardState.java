package com.example.deckbuilder.DeckBuilder.model;

public enum CardState {
    IN_DECK,
    MARKET,
    IN_HAND,
    PLAYED,
    SHIP;

    public static CardState defaultState() {
        return IN_DECK;
    }
}
