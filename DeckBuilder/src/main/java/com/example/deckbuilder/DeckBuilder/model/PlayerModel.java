package com.example.deckbuilder.DeckBuilder.model;

import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.ships.Ship;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayerModel {

    Deck deck;
    List<Card> discardPile = new ArrayList<>();

    SimpleIntegerProperty money = new SimpleIntegerProperty();
    SimpleIntegerProperty damage = new SimpleIntegerProperty();
    SimpleIntegerProperty playerHealth = new SimpleIntegerProperty(20);
    boolean hasShipBeenConverted = false;
    boolean canFight = true;

    private final List<Card> cardsInHand = new ArrayList<>();
    private final List<Card> cardsInPlay = new ArrayList<>();
    private final List<Ship> shipsInPlay = new ArrayList<>();
    private final List<Ship> shipsInFight = new ArrayList<>();

    public PlayerModel(Deck deck) {
        this.deck = deck;
        draw(5);
    }

    public int getMoney() {
        return money.get();
    }

    public int getHealth() {
        return playerHealth.get();
    }

    public int getDamage() {
        return damage.get();
    }

    public List<Card> getCardsInHand() {
        return cardsInHand;
    }

    public List<Card> getCardsInPlay() {
        return cardsInPlay;
    }

    public List<Ship> getShipsInPlay() {
        return shipsInPlay;
    }

    public List<Ship> getShipsInFight() {
        return shipsInFight;
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
            if (deck.size() == 0) {
                return null;
            }
            return draw();
        }
        return draw;
    }

    public void draw(int numCards) {
        Card card = draw();
        int i = 0;
        while (card != null && i < numCards) {
            cardsInHand.add(card);
            i++;
            card = draw();
        }
    }

    public boolean removeCard(Card card) {
        if (discardPile.contains(card)) {
            return discardPile.remove(card);
        } else if (cardsInPlay.contains(card)) {
            return cardsInPlay.remove(card);
        } else if (cardsInHand.contains(card)) {
            return cardsInHand.remove(card);
        }
        return false;
    }

    public void updateMoney(int money) {
        this.money.set(money + this.money.get());
    }

    public void updateDamage(int damage) {
        this.damage.set(this.damage.get() + damage);
    }

    /**
     * Adjusts health. Returns true if the player is still alive, false otherwise.
     *
     * @param healthChange
     * @return
     */
    public boolean updateHealth(int healthChange) {
        this.playerHealth.set(this.playerHealth.get() + healthChange);
        return isAlive();
    }

    private boolean isAlive() {
        return playerHealth.get() > 0;
    }

    public boolean moveFromHandToPlay(Card card) {
        boolean remove = cardsInHand.remove(card);
        if (remove) {
            cardsInPlay.add(card);
        }
        return remove;
    }

    public void addCardToDiscardPile(Card card) {
        discardPile.add(card);
    }

    public void clearHandToDiscardPile() {
        discardPile.addAll(cardsInHand);
        cardsInHand.clear();
    }

    public void clearCardsInPlayToDiscardPile() {
        discardPile.addAll(cardsInPlay);
        cardsInPlay.clear();
    }

    public void clearHandAndCardsInPlay() {
        clearCardsInPlayToDiscardPile();
        clearHandToDiscardPile();
    }

    public void addShip(Ship ship) {
        shipsInPlay.add(ship);
    }

    public void moveShipToFight(Ship ship) {
        if (shipsInPlay.remove(ship)) {
            shipsInFight.add(ship);
        }
    }

    public boolean hasShipBeenConverted() {
        return hasShipBeenConverted;
    }

    public void setHasShipBeenConverted(boolean hasShipBeenConverted) {
        this.hasShipBeenConverted = hasShipBeenConverted;
    }

    public boolean isCanFight() {
        return canFight;
    }

    public void setCanFight(boolean canFight) {
        this.canFight = canFight;
    }

    public void endTurn() {
        hasShipBeenConverted = false;
        canFight = true;
        clearCardsInPlayToDiscardPile();
        draw(5);
        //TODO reset anything after turn end. Perhaps move this to a TurnModel object.
    }
}
