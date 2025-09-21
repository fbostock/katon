package com.example.deckbuilder.DeckBuilder.model;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.cards.Cards;

import java.util.ArrayList;

public class Deck1Provider implements DeckProvider {

    public Deck makeDeck() {
        //TODO CARD this needs an input of card to form the main trade deck.

        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new Card(Cards.dummyCard));
        }
        return new Deck(cards);
    }
}
