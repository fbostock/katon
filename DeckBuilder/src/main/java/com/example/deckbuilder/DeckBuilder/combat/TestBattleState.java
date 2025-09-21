package com.example.deckbuilder.DeckBuilder.combat;

import com.example.deckbuilder.DeckBuilder.events.Event;

public class TestBattleState extends AbstractBattleState {

    @Override
    public void handleShipSelection(Event event) {
        System.out.println("Handling " + event + " in test battle state");
    }
}
