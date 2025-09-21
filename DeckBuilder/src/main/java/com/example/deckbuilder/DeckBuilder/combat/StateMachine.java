package com.example.deckbuilder.DeckBuilder.combat;

import com.example.deckbuilder.DeckBuilder.events.*;
import fjdb.util.TypedMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StateMachine implements EventListener {

    /*
    There will be a series of states. A state has certain valid transitions.

    A state object will subscribe to certain type of events, and have handlers for those events. Those will be registered
    with the stateMachine.

    The state machine will be responsible for
    a) receiving events, and passing them to the current state object.
    b) receiving the response back from the state object, to indicate any transition it is making as a result of the event.
    Returning the same state type would result in no transition.
    c) handling termination of the final state and closing the workflow (combat)

    State objects will each have a type, and the type will indicate the transitions the state can make by returning it.

    State objects could use a visitor pattern for handling the event, with an adapter defaulting the behaviour.
     */

    public enum States {
        FirstPlayerSelect,
        SecondPlayerBlocking,
        FirstPlayerActions,
        SecondPlayerActions,
        BattleEvaluation,
        PostBattleEffects,
        ShipGraveyard, //remove dead ships, send surviving ships back to player, assign damage to players
        PostBattleEvaluation //any remaining effects, and check to see if a player is dead.

        /*
        When an event occurs, the statemachine will pass it to the active state, and attempt to handle it.
        if we have a handleShipSelection() method, we need a way to map an event to that method.

         */
    }

    public static void startStateMachine() {
        //TODO s
        //We need to create the statemachine for the battle.
        //initialise it as an EventListener to process events.
        //provide a publishing mechanism (game update events) to indicate to UI to update view.
//        EventProcessor.getInstance().addListener(this);
        // we also need a model for the Battle to be used by the view and updated by the State machine and its states.

    }

    public static void testHandleEvent() {

        Event event = new ShipEvent(EventType.SHIP_SELECTED, null);

        BiConsumer<AbstractBattleState, Event> handleShipSelection = AbstractBattleState::handleShipSelection;

        Map<EventType, BiConsumer<AbstractBattleState, Event>> handlers = new HashMap<>();
        handlers.put(EventType.SHIP_SELECTED, handleShipSelection);

        TestBattleState testBattleState = new TestBattleState();

        handlers.get(EventType.SHIP_SELECTED).accept(testBattleState, event);

        TypedMap typedMap = new TypedMap();
//        typedMap.put();



    }

    @Override
    public void processEvent(Event event) {
        //TODO delegate to active state in the state machine.
        //Each State should be an EventHandler typed on the States enum, which map to each Battle state. We will use this
        //to inform whether to transition to the next state or not, as per the workflow services.
    }



    public static void main(String[] args) {
        testHandleEvent();
    }


}
