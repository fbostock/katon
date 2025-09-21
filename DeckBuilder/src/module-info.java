module com.example.deckbuilder {
    requires javafx.controls;

//    requires javafx.*;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires frankiemedeslabs.fxutil;
//    requires Core;

    requires eu.hansolo.tilesfx;

    opens com.example.deckbuilder to javafx.fxml;
    exports com.example.deckbuilder;
}