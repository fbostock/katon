package com.example.deckbuilder.DeckBuilder.model;

import com.example.deckbuilder.DeckBuilder.GameEngine;
import com.example.deckbuilder.DeckBuilder.model.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class GameModel {

    PlayerModel player1;
    PlayerModel player2;
    boolean isPlayer1Current = true;


    private final Deck mainMarketDeck;
    private List<Card> marketCards = new ArrayList<>();

    public GameModel(PlayerModel player1, PlayerModel player2) {
        this.player1 = player1;
        this.player2 = player2;
        mainMarketDeck = new Deck1Provider().getDeck();
        for (int i = 0; i < 5; i++) {
            marketCards.add(mainMarketDeck.draw());
        }
    }

    public PlayerModel getPlayer1() {
        return player1;
    }

    public PlayerModel getPlayer2() {
        return player2;
    }

    public PlayerModel getCurrentPlayer() {
        return isPlayer1Current ? player1 : player2;
    }



    public List<Card> getMarketCards() {
        return marketCards;
    }

    public Deck getMainMarketDeck() {
        return mainMarketDeck;
    }

    //    public void removeMarketCard(Card card) {
//        marketCards.remove(card);
//    }

    public void removeAndRefresh(Card card) {
        int i = marketCards.indexOf(card);
        Card draw = mainMarketDeck.draw();
        if (draw != null) {
            marketCards.set(i, draw);
        } else {
            marketCards.remove(i);
        }
    }

    public void switchPlayer() {
        isPlayer1Current = !isPlayer1Current;
    }

    public boolean isFightMode() {
        return GameEngine.getInstance().isFightMode();
    }
}
