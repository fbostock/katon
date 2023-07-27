package fjdb.fxutil;

import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public class ApplicationBoard extends Pane {

    public void setupBoard(Consumer<MouseEvent> onMousePressed, Consumer<ContextMenuEvent> onContextMenuRequested) {

        final Pane panelsPane = this;

        panelsPane.setOnMousePressed(event -> {
            if (event.getClickCount() == 2) {
                onMousePressed.accept(event);
            }
        });

        panelsPane.setOnContextMenuRequested(onContextMenuRequested::accept);
    }
}
