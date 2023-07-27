package fjdb.notesapp;

import com.google.common.collect.Lists;
import fjdb.fxutil.ApplicationBoard;
import fjdb.fxutil.FxUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class NotesApp extends Application {

    NotesRepository repository;
    ApplicationBoard applicationBoard;
    List<NoteWidget> widgetList = Lists.newArrayList();

    public NotesApp() {
        repository = new NotesRepository();
    }

    @Override
    public void start(Stage stage) throws Exception {
        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);

        Tab noteTab = new Tab("Notes");
        mainTabs.getTabs().add(noteTab);

        applicationBoard = new ApplicationBoard();
        noteTab.setContent(applicationBoard);

        applicationBoard.setupBoard(new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {

            }
        }, new Consumer<ContextMenuEvent>() {
            @Override
            public void accept(ContextMenuEvent contextMenuEvent) {
                ContextMenu contextMenu = new ContextMenu();
                //TODO
                MenuItem item = new MenuItem("Create new note");
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        addNewNote();
                    }
                });
                contextMenu.getItems().add(item);
                contextMenu.show(applicationBoard.getScene().getWindow(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            }
        });


        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        stage.setScene(scene);
        stage.show();

//        MenuButton menuButton = FxDemos.addTrigramMenuButton();
//        applicationBoard.getChildren().add(menuButton);

        List<NotesRepository.Note> notes = repository.getNotes();
        widgetList.addAll(notes.stream().map(NoteWidget::new).toList());
        refreshBoard();
    }

    private void refreshBoard() {
        addAllToBoard(widgetList);
    }

    static int totalNodes = 0;

    private static void addNodeToBoard(ApplicationBoard board, Node node) {
        //go for 4 cols.
        int rows = totalNodes / 4;
        int col = totalNodes % 4;
        board.getChildren().add(node);
        node.setLayoutX(10 + col * 120);
        node.setLayoutY(10 + rows * 160);
        totalNodes++;
    }

    private void addNewNote() {
        NoteWidget noteWidget = createNoteWidget();
        widgetList.add(0, noteWidget);
        refreshBoard();
    }

    private void addAllToBoard(List<? extends Node> nodes) {
        applicationBoard.getChildren().clear();
        totalNodes = 0;
        nodes.forEach(widget -> addNodeToBoard(applicationBoard, widget));
    }

    private NoteWidget createNoteWidget() {
        return new NoteWidget(repository.generate("New Note"));
    }

    private class NoteWidget extends Label {
        private final NotesRepository.Note note;

        public NoteWidget(NotesRepository.Note note) {
            super(String.format("%s", snippet(note.getTitle(), note.getContent())));
            this.note = note;
            this.setMinWidth(100);
            this.setMinHeight(150);
            this.setWrapText(true);
            this.setStyle("-fx-border-color: black");
            this.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    //TODO setup an editor pane, which takes up whole view in app.
                    Editor editor = new Editor(NoteWidget.this);
                    editor.prefHeightProperty().bind(applicationBoard.heightProperty());
                    editor.prefWidthProperty().bind(applicationBoard.widthProperty());
                    applicationBoard.getChildren().add(editor);
                }
            });
            //        ScrollBar scrollBarv = (ScrollBar)widget.lookup(".scroll-bar:vertical");
//        scrollBarv.setDisable(true);
        }

        private static String snippet(String title, String content) {
            return title;
        }
        public NotesRepository.Note getNote() {
            return note;
        }
    }

    private class Editor extends VBox {

        private final TextArea textArea;
        private final TextField label;
        private NotesRepository.Note note;
        private NoteWidget noteWidget;
        private boolean changeMade = false;

        public Editor(NoteWidget noteWidget) {
            this.note = noteWidget.note;
            this.noteWidget = noteWidget;
            HBox topBox = new HBox();
            Button backButton = new Button("B");
            backButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (changeMade) {
                        save();
                    }
                    applicationBoard.getChildren().remove(Editor.this);
                }
            });
            label = new TextField(note.getTitle());

            topBox.getChildren().addAll(backButton, label);
            textArea = new TextArea(note.getContent());
            textArea.prefHeightProperty().bind(prefHeightProperty());
            getChildren().addAll(topBox, textArea);
            FxUtils.configureBorder(this);

            EventHandler<KeyEvent> eventHandler = keyEvent -> {
                changeMade = true;
                label.setOnKeyTyped(null);
                textArea.setOnKeyTyped(null);
            };
            label.setOnKeyTyped(eventHandler);
            textArea.setOnKeyTyped(eventHandler);
        }

        private void save() {
            widgetList.remove(noteWidget);
            NotesRepository.Note note = new NotesRepository.Note(label.getText(), textArea.getText(), this.note.getId());
            widgetList.add(0, new NoteWidget(note));
            repository.save(note);
            refreshBoard();
        }
    }
}
