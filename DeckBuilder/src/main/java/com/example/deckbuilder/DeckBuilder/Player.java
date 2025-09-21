package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.Deck;

import java.util.ArrayList;
import java.util.List;

class Player {
    Deck deck;
    List<Card> discardPile = new ArrayList<>();

    public Player(Deck deck) {
        this.deck = deck;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public void discard(Card card) {
        discardPile.add(card);
    }

    private void reshuffle() {
        deck.add(discardPile);
        discardPile.clear();
    }

    public Card draw() {
        Card draw = deck.draw();
        if (draw == null) {
            reshuffle();
            return draw();
        }
        return draw;
    }

}
