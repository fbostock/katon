package com.example.deckbuilder.DeckBuilder.model.ships;

public class ComplexShip implements ShipBlueprint {
    @Override
    public int getAttack() {
        return 2;
    }

    @Override
    public int getDefence() {
        return 1;
    }
}
