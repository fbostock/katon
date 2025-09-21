package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.events.CardEvent;
import com.example.deckbuilder.DeckBuilder.events.Event;
import com.example.deckbuilder.DeckBuilder.events.EventProcessor;
import com.example.deckbuilder.DeckBuilder.events.EventType;
import com.example.deckbuilder.DeckBuilder.model.CardState;
import com.example.deckbuilder.DeckBuilder.model.cards.Card;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;

public class CardWidgit extends VBox {

    private static final int _noteHeight = 180;
    private static final int _noteSpacing = 10;

    private Card card;
    private CardState cardState = CardState.defaultState();
    private CardContextProvider cardContextProvider;

    public CardWidgit(Card card, CardState cardState) {
        this(card, cardState, GameEngine.getInstance().getCardContextProvider());
    }
    public CardWidgit(Card card, CardState cardState, CardContextProvider cardContextProvider) {
        this.card = card;
        this.cardState = cardState;
        this.cardContextProvider = cardContextProvider;
        Label content = new Label(card.getName() + " - £" + card.getCardCost() + "\n£" + card.getMoneyEarned()
                + " H" + card.getHealthGained() + " D" + card.getDamageDealt());
        content.setMinWidth(100);
        content.setMinHeight(_noteHeight - 40);
        content.setMaxWidth(100);
        content.setMaxHeight(_noteHeight - 40);
        content.setWrapText(true);
        content.setPadding(new Insets(0, 2, 2, 2));
        content.setStyle("-fx-font-family: 'Arial'; -fx-border-color: black;  -fx-text-fill: blue; -fx-font-size:9; -fx-background-color:pink");
//            Label title = new Label("Title");
//            title.setMinWidth(100);
//            title.setMinHeight(40);
//            title.setMaxWidth(100);
//            title.setMaxHeight(40);
//            title.setWrapText(true);
//            title.setStyle("-fx-font-family: 'Arial'; -fx-border-color: none; -fx-font-weight: bold; -fx-text-fill: red");
        DropShadow shadow = new DropShadow();
        content.setEffect(shadow);
//            getChildren().addAll(content, title);
        getChildren().addAll(content);
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    System.out.println("Popup Trigger");
                } else {
                    onClick(mouseEvent);
                }
            }
        });

        this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                //TODO create menu with option to view card, and option of actions to take for this card. This should
                //be able to check state of turn to know what actions are available for the card.
                showCardMenu(event.getScreenX(), event.getScreenY(), true);
            }
        });

    }

    private void showCardMenu(double screenX, double screenY, boolean showCard) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem display;
        display = new MenuItem("Display", showCard ? new LargeCard(card) : null);
        contextMenu.getItems().add(display);

        List<CardAction> actions = cardContextProvider.getActions(card);
        for (CardAction action : actions) {
            MenuItem menuItem = new MenuItem(action.getDisplayName());
            menuItem.setOnAction(action.getEventHandler());
            contextMenu.getItems().add(menuItem);
        }
        contextMenu.show(CardWidgit.this, screenX, screenY);
    }

    public void onClick(MouseEvent mouseEvent) {
        //This either should generate a context menu based on the cardState, or it could fire an event with the card state
        //that then triggers the context menu. If there is only one option 9e.g. playing a card), we can skip the context
        //menu and go straight to processing. So perhaps it should simply be an event.
        Event event = null;
        switch (cardState) {
            case IN_DECK -> {
            }
            case IN_HAND -> {
                event = new CardEvent(card, EventType.CARD_PLAYED);
            }
            case MARKET -> {
                event = new CardEvent(card, EventType.CARD_BOUGHT);
            }
            case PLAYED -> {
                if (cardContextProvider.isSelecting()) {
                    event = new CardEvent(card, EventType.CARD_SELECTED);
                } else {
                    showCardMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY(), false);
                }

            }
        }
        if (event != null) {
            EventProcessor.getInstance().processEvent(event);
        }
    }

    private static class LargeCard extends VBox {

        public LargeCard(Card card) {
            Label header = new Label(card.getName() + " - £" + card.getCardCost() + "\n£" + card.getMoneyEarned()
                    + " H" + card.getHealthGained() + " D" + card.getDamageDealt());
            Label content = new Label(card.getDetailedDescription());
            content.setMinWidth(100*2);
            content.setMinHeight(_noteHeight*2 - 40);
            content.setMaxWidth(100*2);
            content.setMaxHeight(_noteHeight*2 - 40);
            content.setWrapText(true);
            content.setPadding(new Insets(0, 2, 2, 2));
            content.setStyle("-fx-font-family: 'Arial'; -fx-border-color: black;  -fx-text-fill: blue; -fx-font-size:9; -fx-background-color:pink");
            DropShadow shadow = new DropShadow();
            content.setEffect(shadow);
            getChildren().addAll(header, content);
        }
    }
}
