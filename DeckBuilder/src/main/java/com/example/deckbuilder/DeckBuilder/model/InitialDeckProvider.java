package com.example.deckbuilder.DeckBuilder.model;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.cards.Cards;

import java.util.ArrayList;

public class InitialDeckProvider implements DeckProvider {
    public Deck makeDeck() {

        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new Card(Cards.dummyCard));
        }
        return new Deck(cards);
    }
}
