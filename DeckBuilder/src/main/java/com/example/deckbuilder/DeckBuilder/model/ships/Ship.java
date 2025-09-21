package com.example.deckbuilder.DeckBuilder.model.ships;

public class Ship {

    private final ShipBlueprint blueprint;
    private final int attack;
    private final int defence;

    public Ship(ShipBlueprint blueprint, int attack, int defence) {
        this.blueprint = blueprint;
        this.attack = attack;
        this.defence = defence;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }
}
