package fjdb.notesapp;

import com.google.common.collect.Lists;
import fjdb.fxutil.ApplicationBoard;
import fjdb.fxutil.FxUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class NotesPane {

    private static final int _noteHeight = 180;
    private static final int _noteSpacing = 10;

    NotesRepository repository;
    ApplicationBoard applicationBoard;
    List<Node> widgetList = Lists.newArrayList();

    public NotesPane(NotesRepository repository, boolean isArchive) {
        this.repository = repository;

        applicationBoard = new ApplicationBoard();

        applicationBoard.setupBoard(mouseEvent -> {

        }, contextMenuEvent -> {
            ContextMenu contextMenu = new ContextMenu();
            //TODO
            MenuItem item = new MenuItem("Create new note");
            item.setOnAction(actionEvent -> {
                NoteWidget noteWidget = addNewNote();
                noteWidget.editAction();
            });
            contextMenu.getItems().add(item);
            contextMenu.show(applicationBoard.getScene().getWindow(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });

        List<NotesRepository.Note> notes = repository.getNotes(isArchive);
        widgetList.addAll(notes.stream().map(NoteWidget::new).toList());
        refreshBoard();
    }

    public ApplicationBoard getApplicationBoard() {
        return applicationBoard;
    }

    private void refreshBoard() {
        addAllToBoard(widgetList);
    }

    private static final int columns = 6;
    static int totalNodes = 0;

    private static void addNodeToBoard(ApplicationBoard board, Node node) {
        //go for 4 cols.
        int rows = totalNodes / columns;
        int col = totalNodes % columns;
        board.getChildren().add(node);
        node.setLayoutX(10 + col * 120);
        node.setLayoutY(10 + rows * _noteHeight + _noteSpacing);
        totalNodes++;
    }

    private NoteWidget addNewNote() {
        NoteWidget noteWidget = createNoteWidget();
        widgetList.add(0, noteWidget);
        refreshBoard();
        return noteWidget;
    }

    private void addAllToBoard(List<? extends Node> nodes) {
        applicationBoard.getChildren().clear();
        totalNodes = 0;
        nodes.forEach(widget -> addNodeToBoard(applicationBoard, widget));
    }

    private NoteWidget createNoteWidget() {
        return new NoteWidget(repository.generate("New Note"));
    }


    private class NoteWidget extends VBox {
        private final NotesRepository.Note note;

        public NoteWidget(NotesRepository.Note note) {

            Label content = new Label(snippet("", note.getContent()));
            this.note = note;
            content.setMinWidth(100);
            content.setMinHeight(_noteHeight-40);
            content.setMaxWidth(100);
            content.setMaxHeight(_noteHeight-40);
            content.setWrapText(true);
            content.setPadding(new Insets(0,2,2,2));
            content.setStyle("-fx-font-family: 'Arial'; -fx-border-color: black;  -fx-text-fill: blue; -fx-font-size:9; -fx-background-color:pink");
            Label title = new Label(note.getTitle());
            title.setMinWidth(100);
            title.setMinHeight(40);
            title.setMaxWidth(100);
            title.setMaxHeight(40);
            title.setWrapText(true);
            title.setStyle("-fx-font-family: 'Arial'; -fx-border-color: none; -fx-font-weight: bold; -fx-text-fill: red");
            DropShadow shadow = new DropShadow();
            content.setEffect(shadow);
            getChildren().addAll(content, title);
//            getChildren().addAll(content);
            this.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    editAction();
                }
            });
            this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent contextMenuEvent) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem edit = new MenuItem("Edit");
                    edit.setOnAction(e->editAction());
                    MenuItem delete = new MenuItem("Delete");
                    delete.setOnAction(e->deleteAction());

                    contextMenu.getItems().add(edit);
                    contextMenu.show(applicationBoard.getScene().getWindow(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());

                }
            });
        }

        private void deleteAction() {
            widgetList.remove(this);
            refreshBoard();
            System.out.println("Widget just removed. Item still in database");
            //TODO delete from database, or archive.
        }

        private void editAction() {
            Editor editor = new Editor(NoteWidget.this);
            editor.prefHeightProperty().bind(applicationBoard.heightProperty());
            editor.prefWidthProperty().bind(applicationBoard.widthProperty());
            applicationBoard.getChildren().add(editor);
        }

        private static String snippet(String title, String content) {
            int length = Math.min(200 - title.length(), content.length());
            String ellipsis =  (length < content.length()) ? "..." : "";
//            return title + System.lineSeparator() + content.substring(0, length) + ellipsis;
            return title + content.substring(0, length) + ellipsis;
        }

        public NotesRepository.Note getNote() {
            return note;
        }
    }

    private class Editor extends VBox {

        private final TextArea textArea;
        private final TextField label;
        private final NotesRepository.Note note;
        private final NoteWidget noteWidget;
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
