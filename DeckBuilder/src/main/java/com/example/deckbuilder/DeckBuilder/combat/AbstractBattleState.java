package com.example.deckbuilder.DeckBuilder.combat;

import com.example.deckbuilder.DeckBuilder.events.Event;
import com.example.deckbuilder.DeckBuilder.events.ShipEvent;

public abstract class AbstractBattleState {

    public AbstractBattleState() {

    }

    public void handleShipSelection(Event event) {
        System.out.println("Handling " + event + " in BASE state");
    }

}
