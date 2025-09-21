package com.example.deckbuilder.DeckBuilder.model.ships;

/**
 * Represents details how to create a ship instance. A Ship represents a card in play that the player is using. The
 * blue print is the template of how to create that instance.
 */
public interface ShipBlueprint {

    int getAttack();
    int getDefence();

    //TODO other abilities. First up, we simply want to be able to play cards, turn them into ships, and attack the opponent.
}
