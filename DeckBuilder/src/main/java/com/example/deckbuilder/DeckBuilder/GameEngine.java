package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.events.*;
import com.example.deckbuilder.DeckBuilder.model.Deck;
import com.example.deckbuilder.DeckBuilder.model.GameModel;
import com.example.deckbuilder.DeckBuilder.model.InitialDeckProvider;
import com.example.deckbuilder.DeckBuilder.model.PlayerModel;
import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.ships.Ship;
import com.example.deckbuilder.DeckBuilder.model.ships.ShipBlueprint;

public class GameEngine implements EventListener {

    private static final GameEngine instance = new GameEngine();
    private GameModel gameModel;
    private boolean isFightMode = false;

    public static GameEngine getInstance() {
        return instance;
    }

    private CardContextProvider cardContextProvider = new CardContextProvider();

    private GameEngine() {
        EventProcessor.getInstance().addListener(this);
    }

    public void initialise() {
        InitialDeckProvider deckProvider = new InitialDeckProvider();
        gameModel = new GameModel(new PlayerModel(deckProvider.getDeck()), new PlayerModel(deckProvider.getDeck()));
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public CardContextProvider getCardContextProvider() {
        return cardContextProvider;
    }

    public Deck getMainMarketDeck() {
        //TODO remove if we don't need this.
        return gameModel.getMainMarketDeck();
    }

    public void register(EventListener eventListener) {
        EventProcessor.getInstance().addListener(eventListener);
    }

    /**
     * Method called when player selects card in their hand to play.
     */
    public void playCard(Card card, PlayerModel playerModel) {
        //remove card from hand, and add it to in-play
        boolean cardWasMoved = playerModel.moveFromHandToPlay(card);
        if (!cardWasMoved) {
            throw new RuntimeException("Card " + card + " could not be moved - not found in hand");
        }

        //update any money
        playerModel.updateMoney(card.getMoneyEarned());
        playerModel.updateHealth(card.getHealthGained());
        playerModel.updateDamage(card.getDamageDealt());

        //update other properties
        //TODO

        //fire event
        fireModelUpdate(card);
    }

    private void fireModelUpdate(Card card) {
        EventProcessor.getInstance().processEvent(new GameEvent(card, EventType.MODEL_UPDATE));
    }

    private void fireInvalidChoice(Card card) {
        EventProcessor.getInstance().processEvent(new GameEvent(card, EventType.INVALID_SELECTION));
    }

    public boolean buyCard(Card card, PlayerModel playerModel) {
        if (playerModel.getMoney() >= card.getCardCost()) {
            playerModel.updateMoney(-card.getCardCost());
            playerModel.addCardToDiscardPile(card);
            Card.Effect effect = card.effectOnBuying();
            //TODO process effect on buying.

            //TODO update market row
            gameModel.removeAndRefresh(card);
            fireModelUpdate(card);
            return true;
        } else {
            fireInvalidChoice(card);
        }
        return false;
    }

    public void activateCard(Card card, PlayerModel playerModel) {
        //TODO
    }

    public void transformShip(Card card, PlayerModel playerModel) {
        if (!playerModel.hasShipBeenConverted()) {
            ShipBlueprint blueprint = card.getShip();
            Ship ship = new Ship(blueprint, blueprint.getAttack(), blueprint.getDefence());
            boolean removedSuccessfully = playerModel.removeCard(card);

            playerModel.addShip(ship);
            playerModel.setHasShipBeenConverted(true);
            fireModelUpdate(card);
        } else {
            fireInvalidChoice(card);
        }
    }

    public void activateShip(Ship shipCard, PlayerModel playerModel) {
        //TODO
    }

    @Override
    public void processEvent(Event event) {
        if (event instanceof CardEvent cardEvent) {
            Card card = cardEvent.getCard();
            EventType type = cardEvent.getType();
            switch (type) {
                case CARD_PLAYED -> {
                    playCard(card, gameModel.getCurrentPlayer());
                }
                case SHIP_PLAYED -> {
                    transformShip(card, gameModel.getCurrentPlayer());
                }
                case CARD_BOUGHT -> {
                    buyCard(card, gameModel.getCurrentPlayer());
                }
                case CARD_SELECTED -> {
                    //TODO execute some card ability
                    //show context menu

                }
                case MODEL_UPDATE -> {
                    //nothing to do here.
                }
                default -> System.out.println("CardEvent" + event.getType() + " NOT HANDLED");

            }
        } else if (event instanceof ShipEvent shipEvent) {
            switch (event.getType()) {
                case SHIP_SELECTED -> {
                    System.out.println("Ship Selected");
                    if (isFightMode) {
                        //TODO activate some ship ability,
                        getGameModel().getCurrentPlayer().moveShipToFight(shipEvent.getShip());
                        System.out.printf("TODO update the GUI to add a fight area for the ships");
                    }
                }
                default -> System.out.println("ShipEvent" + event.getType() + " NOT HANDLED");

            }
        }
    }//end of process event


    /*
    Turn mode:
    General mode: Play cards, Buy cards, Convert ship
    Fight mode: can't play cards or buy, or convert ship. Can only select ships to fight, or activate cards in play, or activate
    ships in play if they have abilities
     */

    public boolean isFightMode() {
        return isFightMode;
    }

    public void setFightMode() {
        isFightMode = true;
        System.out.println("Entering Fight Mode");
        //TODO make panel appear,
//        Dialog dialog = new Dialog();
//
//        HBox content = new HBox();
//        ScrollPane scrollPane = new ScrollPane(content);
////        dialog.getDialogPane().getChildren().addAll(scrollPane);
//        dialog.getDialogPane().getChildren().addAll(content);
//        content.getChildren().add(new Label("This is the fight panel"));
//        dialog.show();

        //TODO start fight mode when button clicked, must disable playing cards, buying cards, enable selecting ships
        //for fighting, activating cards can still happen, activating ships can happen.
    }

    /* returns true if fight is over, false otherwise */
    public boolean nextFightStage() {

        boolean concludeFight = true;
        if (concludeFight) {
                        isFightMode = false;
                        /* TODO
                        resolve ships and their blockers. Should be handled in another object.
                        resolve damage from unblocked ships to other player.
                        move surviving ships from shipsInFight to shipsInPlay
                        return true, to allow the GUI to update controls.
                         */

        }

        return true;
    }



    public void endTurn() {
        //TODO complete playerModel end turn
        //TODO switch player
        gameModel.getCurrentPlayer().endTurn();
        fireModelUpdate(null);
    }

}
