package com.example.deckbuilder.DeckBuilder.model.cards;

import com.example.deckbuilder.DeckBuilder.model.ships.ShipBlueprint;

public class CardBlueprint {

    private final String name;
    private final String description;
    private final int cardCost;
    private final int moneyEarned;
    private final int healthGained;
    private final int damageDealt;
    private final ShipBlueprint ship;

    public CardBlueprint(String name, String description, int cost, int moneyEarned, int healthGained, int damageDealt, ShipBlueprint ship) {
        this.name = name;
        this.cardCost = cost;
        this.moneyEarned = moneyEarned;
        this.healthGained = healthGained;
        this.damageDealt = damageDealt;
        this.ship = ship;
        this.description = description;
    }

    public String getName() {
        return name;
    }



    public int getCardCost() {
        return cardCost;
    }

    public int getMoneyEarned() {
        return moneyEarned;
    }

    public int getHealthGained() {
        return healthGained;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public ShipBlueprint getShip() {
        return ship;
    }

    public String getDetailedDescription() {
        return description;
    }
}
