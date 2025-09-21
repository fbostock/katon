package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.model.CardState;
import com.example.deckbuilder.DeckBuilder.model.Deck;
import com.example.deckbuilder.DeckBuilder.model.Deck1Provider;
import com.example.deckbuilder.DeckBuilder.model.DeckProvider;
import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import com.example.deckbuilder.DeckBuilder.model.cards.Cards;
import frankiemedeslabs.fxutil.ApplicationBoard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainGame extends Application {

    private ApplicationBoard applicationBoard;

    public static void main(String[] args) {

        //start with a script which sets up a game, then makes player1 play their cards, and then switch to player2.

        //get deck of cards for the game, shuffle and create the market place
        DeckProvider deckProvider = new Deck1Provider();
        Deck deck = deckProvider.getDeck();
        deck.shuffle();

        List<Card> hand1 = defaultCards();
        List<Card> hand2 = defaultCards();

        Deck player1Deck = new Deck(hand1);
        Deck player2Deck = new Deck(hand2);
        player1Deck.shuffle();
        player2Deck.shuffle();

        Player player1 = new Player(player1Deck);
        Player player2 = new Player(player2Deck);

        List<Card> market = deck.draw(5);

        launch(args);

    }

    private static List<Card> defaultCards() {
        return new ArrayList<>();
    }

    @Override
    public void start(Stage stage) throws Exception {

        applicationBoard = new ApplicationBoard();
        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(applicationBoard);

        VBox vBox = new VBox();
        CardRow marketRow = new CardRow(CardState.MARKET);
        for (int i = 0; i < 5; i++) {
            CardWidgit cardWidgit = new CardWidgit(new Card(Cards.dummyCard), CardState.MARKET, null);
            marketRow.addCard(cardWidgit);
        }
        CardRow shipRow = new CardRow(CardState.SHIP);
        CardRow cardsPlayedRow = new CardRow(CardState.PLAYED);
        CardRow playersHand = new CardRow(CardState.IN_HAND);

        vBox.getChildren().addAll(marketRow, shipRow, cardsPlayedRow, playersHand);

        applicationBoard.getChildren().add(vBox);
        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();

    }

    private class CardRow extends HBox {

        private final CardState cardState;
        private List<CardWidgit> widgits = new ArrayList<>();

        public CardRow(CardState cardState) {
            this.cardState = cardState;
        }

        public void addCard(CardWidgit cardWidgit) {
            widgits.add(cardWidgit);
            getChildren().add(cardWidgit);
        }

    }

}
