package com.example.deckbuilder.DeckBuilder.model;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import fjdb.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Randomises the order of cards in the deck
     */
    public void shuffle() {
        ListUtil.randomiseOrder(cards);
    }

    public void add(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }


    public List<Card> draw(int n) {
        List<Card> drawn = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            drawn.add(cards.remove(0));
        }
        return drawn;
    }

    public int size() {
        return cards.size();
    }
}
