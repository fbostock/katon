package com.example.deckbuilder.DeckBuilder.model.ships;

public class SimpleShip implements ShipBlueprint {
    @Override
    public int getAttack() {
        return 1;
    }

    @Override
    public int getDefence() {
        return 1;
    }
}
