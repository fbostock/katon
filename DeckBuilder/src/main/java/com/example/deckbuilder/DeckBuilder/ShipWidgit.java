package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.events.EventProcessor;
import com.example.deckbuilder.DeckBuilder.events.EventType;
import com.example.deckbuilder.DeckBuilder.events.ShipEvent;
import com.example.deckbuilder.DeckBuilder.model.CardState;
import com.example.deckbuilder.DeckBuilder.model.ships.Ship;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ShipWidgit extends VBox {

    private static final int _noteHeight = 180;
    private static final int _noteSpacing = 10;

    private Ship ship;
    private CardState cardState = CardState.SHIP;

    public ShipWidgit(Ship card, CardState cardState) {
        this.ship = card;
        this.cardState = cardState;
        Label content = new Label(String.format("This is a ShipWidgit\n A/D %s/%s", ship.getAttack(), ship.getDefence()));
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
                onClick();
            }
        });
//            this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
//                @Override
//                public void handle(ContextMenuEvent contextMenuEvent) {
//                    ContextMenu contextMenu = new ContextMenu();
//                    MenuItem edit = new MenuItem("Edit");
//
//                    contextMenu.getItems().add(edit);
//                    contextMenu.show(applicationBoard.getScene().getWindow(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
//
//                }
//            });

    }

    public void onClick() {
        //This either should generate a context menu based on the cardState, or it could fire an event with the card state
        //that then triggers the context menu. If there is only one option e.g. playing a card, we can skip the context
        //menu and go straight to processing. So perhaps it should simply be an event.
        EventProcessor.getInstance().processEvent(new ShipEvent(EventType.SHIP_SELECTED, ship));
    }
}
