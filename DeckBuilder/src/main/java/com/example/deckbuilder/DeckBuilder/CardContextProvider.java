package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.events.CardEvent;
import com.example.deckbuilder.DeckBuilder.events.EventType;
import com.example.deckbuilder.DeckBuilder.events.ShipEvent;
import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.ships.Ship;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class CardContextProvider {

    private boolean setSelecting = false;
    public List<CardAction> getActions(Card card) {
        return List.of(transformToShip(card));
    }

    public List<CardAction> getActions(Ship ship) {
        return List.of();
    }

    public boolean isSelecting() {
        return setSelecting;
    }

    public void setSelecting(boolean setSelecting) {
        this.setSelecting = setSelecting;
    }

    private CardAction selectForFight(Ship ship) {
        return new CardAction("Add to Fight", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                GameEngine.getInstance().processEvent(new ShipEvent(EventType.SHIP_SELECTED, ship));
            }
        });
    }
    private CardAction transformToShip(Card card) {
        return new CardAction("Convert To Ship", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 GameEngine.getInstance().processEvent(new CardEvent(card, EventType.SHIP_PLAYED));
            }
        });
    }
}
