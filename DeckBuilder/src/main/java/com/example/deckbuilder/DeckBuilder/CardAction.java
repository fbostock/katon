package com.example.deckbuilder.DeckBuilder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CardAction {

    private String displayName;
    private EventHandler<ActionEvent> eventHandler;

    public CardAction(String displayName, EventHandler<ActionEvent> eventHandler) {
        this.displayName = displayName;
        this.eventHandler = eventHandler;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EventHandler<ActionEvent> getEventHandler() {
        return eventHandler;
    }
}
