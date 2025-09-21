package com.example.deckbuilder.DeckBuilder.model;

public interface DeckProvider {

    public Deck makeDeck();

    default public Deck getDeck() {
        Deck deck = makeDeck();
        deck.shuffle();
        return deck;
    }

    default
    public Deck getUnshuffledDeck() {
        return makeDeck();
    }
}
