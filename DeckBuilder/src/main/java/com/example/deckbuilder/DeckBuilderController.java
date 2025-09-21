package com.example.deckbuilder;

import com.example.deckbuilder.DeckBuilder.CardWidgit;
import com.example.deckbuilder.DeckBuilder.GameEngine;
import com.example.deckbuilder.DeckBuilder.ShipWidgit;
import com.example.deckbuilder.DeckBuilder.events.CardEvent;
import com.example.deckbuilder.DeckBuilder.events.Event;
import com.example.deckbuilder.DeckBuilder.events.EventListener;
import com.example.deckbuilder.DeckBuilder.events.EventType;
import com.example.deckbuilder.DeckBuilder.model.CardState;
import com.example.deckbuilder.DeckBuilder.model.GameModel;
import com.example.deckbuilder.DeckBuilder.model.PlayerModel;
import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.ships.Ship;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;

public class DeckBuilderController implements EventListener {
    @FXML
    public Pane root;

    ObservableList<Node> initialChildren;

    @FXML
    Pane combatView;

    @FXML
    public Button fightMode;

    @FXML
    public Button fightNext;

    @FXML
    public Label play2health;
    @FXML
    public Label play2dam;
    @FXML
    public Label play2money;
    @FXML
    public Label play1money;
    @FXML
    public Label play1dam;
    @FXML
    public Label play1health;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartGame() {
        initialChildren = root.getChildren();
        GameEngine.getInstance().register(this);
        GameEngine.getInstance().initialise();
        welcomeText.setText("Start The Game!");
        updateView(GameEngine.getInstance().getGameModel());
    }

    private void updateView(GameModel gameModel) {
        marketRow.getChildren().clear();
        playerHand.getChildren().clear();
        playerCardsPlayed.getChildren().clear();
        playerShips.getChildren().clear();
        opponentCardsPlayed.getChildren().clear();
        opponentShips.getChildren().clear();

        if (GameEngine.getInstance().isFightMode()) {
            List<Ship> shipsInFight = gameModel.getCurrentPlayer().getShipsInFight();
            for (Ship ship : shipsInFight) {
                marketRow.getChildren().add(new ShipWidgit(ship, CardState.SHIP));
            }
        } else {
            List<Card> marketCards = gameModel.getMarketCards();
            for (Card marketCard : marketCards) {
                marketRow.getChildren().add(new CardWidgit(marketCard, CardState.MARKET));
            }
        }

        PlayerModel player1 = gameModel.getPlayer1();
        for (Card card : player1.getCardsInHand()) {
            playerHand.getChildren().add(new CardWidgit(card, CardState.IN_HAND));
        }
        for (Card card : player1.getCardsInPlay()) {
            playerCardsPlayed.getChildren().add(new CardWidgit(card, CardState.PLAYED));
        }

        List<Ship> shipsInPlay = player1.getShipsInPlay();
        for (Ship ship : shipsInPlay) {
            playerShips.getChildren().add(new ShipWidgit(ship, CardState.SHIP));
        }
        PlayerModel player2 = gameModel.getPlayer2();
        for (Card card : player2.getCardsInPlay()) {
            opponentCardsPlayed.getChildren().add(new CardWidgit(card, CardState.PLAYED));
        }

        for (Ship ship : player2.getShipsInPlay()) {
            opponentShips.getChildren().add(new ShipWidgit(ship, CardState.SHIP));
        }

        play1money.setText("£ " + player1.getMoney());
        play1dam.setText("" + player1.getDamage());
        play1health.setText("" + player1.getHealth());
        play2money.setText("£ " + player2.getMoney());
        play2dam.setText("" + player2.getDamage());
        play2health.setText("" + player2.getHealth());
    }

    @FXML
    private Pane marketRow;

    @FXML
    private Pane playerShips;

    @FXML
    private Pane playerCardsPlayed;

    @FXML
    private Pane playerHand;

    @FXML
    private Pane opponentCardsPlayed;

    @FXML
    private Pane opponentShips;

    @Override
    public void processEvent(Event event) {
        if (EventType.COMBAT_COMPLETE.equals(event.getType())) {
            restorePane();
            return;
        }
        if (EventType.CARD_SELECTED.equals(event.getType())) {
            if (event instanceof CardEvent) {
                CardEvent cardEvent = (CardEvent) event;
                showContextMenu(cardEvent.getCard());
            }
        } else {
            updateView(GameEngine.getInstance().getGameModel());
        }
    }

    private void restorePane() {
        root.getChildren().setAll(initialChildren);
    }

    @FXML
    public void fightMode(ActionEvent actionEvent) {
        root.getChildren().setAll(combatView);

        fightMode.setVisible(false);
        fightNext.setVisible(true);
        GameEngine.getInstance().setFightMode();
    }

    @FXML
    public void endTurn(ActionEvent actionEvent) {
        GameEngine.getInstance().endTurn();
    }

    @FXML
    public void fightNextStage(ActionEvent actionEvent) {
        if (GameEngine.getInstance().nextFightStage()) {
            fightMode.setVisible(true);
            fightNext.setVisible(false);
        }
    }

    public void showContextMenu(Card card) {
//        playerCardsPlayed.setOnContextMenuRequested();
    }

}