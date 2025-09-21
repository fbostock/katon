package com.example.deckbuilder.DeckBuilder.model.cards;

import com.example.deckbuilder.DeckBuilder.model.ships.ShipBlueprint;

public class Card {

    private final CardBlueprint cardBlueprint;
    private final ShipBlueprint ship;

    public Card(CardBlueprint cardBlueprint) {
        this.cardBlueprint = cardBlueprint;
        this.ship = cardBlueprint.getShip();
    }

    public String getName() {
        return cardBlueprint.getName();
    }

    public String getDetailedDescription() {
        return cardBlueprint.getDetailedDescription();
    }

    public int getCardCost() {
        return cardBlueprint.getCardCost();
    }

    public int getMoneyEarned() {
        return cardBlueprint.getMoneyEarned();
    }

    public int getHealthGained() {
        return cardBlueprint.getHealthGained();
    }

    public int getDamageDealt() {
        return cardBlueprint.getDamageDealt();
    }

    public ShipBlueprint getShip() {
        return ship;
    }

    /**
     * Return some object or lambda which encapsulates what should happen when the card is bought.
     */
    public Effect effectOnBuying() {
        //TODO
        return new Effect();
    }
    public Effect effectOnZZZYYYBuying() {
        //TODO
        return new Effect();
    }

    public static class Effect {

    }
}
