package com.example.deckbuilder;

import com.example.deckbuilder.DeckBuilder.events.Event;
import com.example.deckbuilder.DeckBuilder.events.EventListener;
import com.example.deckbuilder.DeckBuilder.events.EventProcessor;
import com.example.deckbuilder.DeckBuilder.events.GameEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static com.example.deckbuilder.DeckBuilder.events.EventType.COMBAT_COMPLETE;

public class CombatController implements EventListener {

    @FXML
    Button endCombat;

    @Override
    public void processEvent(Event event) {
        //TODO events will come from the gui. This needs to dispatch them to the state machine. The state machine will
        //publish results, which the controller should then listen to and update the UI as appropriate
    }

    @FXML
    public void endCombat(ActionEvent actionEvent) {
        EventProcessor.getInstance().processEvent(new GameEvent(null, COMBAT_COMPLETE));
    }
}
