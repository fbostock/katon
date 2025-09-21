package fjdb.typinggame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class TypingGame extends Application implements TypingGameEventListener {

    public static final String FANCYTEXT = "fancytext";
    /*
     * Mark incorrect letter as red/bold to indicate it's wrong.
     * A 'Reveal button' which shows ~3 letters for the next letter to help.
     *
     * Another game mode: shows a picture, and gives lots of word options for what it is.
     *                    shows a word, gives 3 pictures for what it is.
     *
     * Add counter to track number of words done.
     */
    private Button nextPuzzle;
    private PuzzleFrame puzzleFrame;
    private Counter counter;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO can we create a fun border.
        final BorderPane sceneRoot = new BorderPane();

        Pane mainPane = new Pane();

        //Create main puzzle frame, and load with first puzzle
        puzzleFrame = new PuzzleFrame(this, new PuzzleProvider());
        puzzleFrame.updatePuzzle();

        ImageView greenArrow = Images.getGreenArrow();

        mainPane.getChildren().add(puzzleFrame);
        puzzleFrame.setLayoutX(400);
        puzzleFrame.setLayoutY(200);


        nextPuzzle = new Button();
        nextPuzzle.setGraphic(greenArrow);
        VBox counterAndNextBut = new VBox(10.0);
        counter = new Counter(0);
        counterAndNextBut.getChildren().add(counter);
        counterAndNextBut.getChildren().add(nextPuzzle);
        mainPane.getChildren().add(counterAndNextBut);
//        mainPane.getChildren().add(nextPuzzle);
        counterAndNextBut.setLayoutX(800);
        counterAndNextBut.setLayoutY(300);
        nextPuzzle.setDisable(true);
        nextPuzzle.setOnAction(actionEvent -> loadNextPuzzle());

        sceneRoot.setCenter(mainPane);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("/fjdb/typinggame/styles/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> Platform.exit());

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                puzzleFrame.handle(event);
//                System.out.printf("Pressing key %s\n", event.getCode());
//                if (KeyCode.DELETE == event.getCode() || KeyCode.BACK_SPACE == event.getCode()) {
//                    puzzleFrame.typingPanel.delete();
//                } else if (event.getCode().isLetterKey()) {
//                    puzzleFrame.typingPanel.typingUpdate(event.getText().toUpperCase());
//                } else {
//                    System.out.printf("Not processing %s keycode\n", event.getCode());
//                }
            }
        });
    }

    private void correctAnswer() {
        //TODO fanfare effects.
        //Add button to go to next word

    }

    private void loadNextPuzzle() {
        nextPuzzle.setDisable(true);
        puzzleFrame.updatePuzzle();
        //TODO
        //create a PuzzleProvider which provides the image and word associated to it.
        //load picture into viewer. Load word into TypingPanel.
    }

    @Override
    public void onComplete() {
        counter.add(1);
        correctAnswer();
        nextPuzzle.setDisable(false);
        //TODO play some animation/audio
    }

    @Override
    public void onMistake() {

    }

    private static class PuzzleFrame extends FlowPane {
        private final TypingGameEventListener listener;
        private TypingPanel typingPanel;
        private PuzzleProvider puzzleProvider;

        public PuzzleFrame(TypingGameEventListener listener, PuzzleProvider puzzleProvider) {
            this.listener = listener;
            this.puzzleProvider = puzzleProvider;
        }

        public void updatePuzzle() {
            Puzzle puzzle = puzzleProvider.nextPuzzle();
            getChildren().clear();
            typingPanel = new TypingPanel(puzzle, listener);
            getChildren().add(typingPanel);
        }

        public void handle(KeyEvent event) {
            System.out.printf("Pressing key %s\n", event.getCode());
            if (KeyCode.DELETE == event.getCode() || KeyCode.BACK_SPACE == event.getCode()) {
                typingPanel.delete();
            } else if (event.getCode().isLetterKey()) {
                typingPanel.typingUpdate(event.getText().toUpperCase());
            } else {
                System.out.printf("Not processing %s keycode\n", event.getCode());
            }
        }

    }

    private static class TypingPanel extends FlowPane {
        private int wordLength;
        private TypingGameEventListener listener;
        private List<Text> letters = new ArrayList<>();
        private int speltIndex = -1;
        private Puzzle puzzle;
        int mistakeCount = 0;
        private HBox hintBox = new HBox(10);
        private Paint fill;

        public TypingPanel(Puzzle puzzle, TypingGameEventListener listener) {
            this.puzzle = puzzle;
            this.wordLength = puzzle.getName().length();
            this.listener = listener;
            for (int i = 0; i < wordLength; i++) {
                Text text = new Text("_");
                text.setId("fancytext");
                letters.add(text);
//                DropShadow shadow = new DropShadow();
//                text.setEffect(shadow);

            }
            fill = letters.get(0).getFill();
            VBox verticalPanel = new VBox(20);

            String url = puzzle.loadFile();
            ImageView imageView = new ImageView(url);
            imageView.setEffect(new DropShadow());
            verticalPanel.getChildren().add(imageView);

            HBox answerBox = new HBox(10);
            verticalPanel.getChildren().add(answerBox);
            verticalPanel.getChildren().add(hintBox);
            getChildren().add(verticalPanel);

            answerBox.getChildren().addAll(letters);
        }

        public void typingUpdate(String letter) {
            if (mistakeCount == 0 && speltIndex == wordLength - 1) return;
            if (mistakeCount == 0) {
                speltIndex++;
            }
//            letters.get(speltIndex).setStyle("fancytext");
            letters.get(speltIndex).setText(letter);
            int letterIncorrect = isLetterIncorrect();
            if (letterIncorrect >= 0) {
                letters.get(speltIndex).setStrikethrough(true);
                mistakeCount++;
                listener.onMistake();
                if (mistakeCount >= 3) {
                    showHint(puzzle.getName().toUpperCase().charAt(speltIndex));
                }
            } else {
                letters.get(speltIndex).setStyle("fancytext");
                letters.get(speltIndex).setStrikethrough(false);
                hintBox.getChildren().clear();
                mistakeCount = 0;
                isAnswerCorrect();
            }
        }

        public void showHint(Character character) {
            //assume error at speltIndex
            hintBox.getChildren().clear();
            Set<Character> hints = getHints(3, character);
            for (Character hint : hints) {
                Text text = new Text(hint.toString());
                text.setId("fancytext");
                hintBox.getChildren().add(text);
            }
        }

        public void delete() {
            if (speltIndex >= 0) {

                Text text = letters.get(speltIndex);
                letters.get(speltIndex).setStrikethrough(false);
                System.out.println("Fill: " + text.getFill());
//                text.setStyle("fancytext");
                System.out.println("Fill: " + text.getFill());
                text.setText("_");
                System.out.println("Fill: " + text.getFill());
                speltIndex--;
                mistakeCount = 0;
            }
        }

        private Set<Character> getHints(int totalSize, Character character) {
            Random r = new Random();
            HashSet<Character> characterSet = new HashSet<>();
            characterSet.add(character);
            while (characterSet.size() < totalSize) {
                char c = (char) (r.nextInt(26) + 'A');
                characterSet.add(c);
            }
            return characterSet;
        }

        private int isLetterIncorrect() {
            //check up to speltIndex
            String name = puzzle.getName().toUpperCase();
            for (int i = 0; i <= speltIndex; i++) {
                String text = letters.get(i).getText();
                char anObject = name.charAt(i);
                char c = text.charAt(0);
                if (c != anObject) {
                    return i;
                }
            }
            return -1;
        }

        private void isAnswerCorrect() {
            StringBuilder answer = new StringBuilder();
            for (Text letter : letters) {
                answer.append(letter.getText());
            }
            if (answer.toString().equalsIgnoreCase(puzzle.getName())) {
                animate();
                listener.onComplete();
            }
        }

        private void animate() {
            Font initialFont = letters.get(0).getFont();
            Timeline timeline = new Timeline();
            final IntegerProperty i = new SimpleIntegerProperty(0);
            final IntegerProperty changeAmount = new SimpleIntegerProperty(1);

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(25),
                    event -> {
                        Font value = new Font(initialFont.getSize() + i.get());
                        for (Text letter : letters) {
                            letter.setFont(value);
                        }
                        if (i.get() > 20) {
                            changeAmount.set(changeAmount.get() * -1);
                            i.set(19);
                        } else if (i.get() < 0) {
                            changeAmount.set(changeAmount.get() * -1);
                            i.set(0);
                        } else {
                            i.set(i.get() + changeAmount.get());
                        }

                    });
            timeline.getKeyFrames().add(keyFrame);
//            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.setCycleCount(200);
            timeline.play();
        }
    }


    private static class Counter extends HBox {
        private int counter;
        private final Text countField = new Text("0");

        public Counter(int initial) {
            super();
            countField.setId(FANCYTEXT);
            this.counter = initial;
            Text correct = new Text("Correct: ");
            correct.setId(FANCYTEXT);
            getChildren().add(correct);
            getChildren().add(countField);
        }

        public void clear() {
            counter = 0;
            add(0);
        }

        public void add(int value) {
            counter += value;
            countField.setText(String.valueOf(counter));
        }
    }
}
