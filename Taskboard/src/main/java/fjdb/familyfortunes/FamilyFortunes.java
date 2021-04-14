package fjdb.familyfortunes;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
//import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FamilyFortunes extends Application {

    private Round currentRound;

    public static void main(String[] args) {
        launch(args);
    }

    private static Polygon drawX(boolean small) {
        Polygon triangle = new Polygon();
        double height = small ? 130.0 : 180;
        triangle.getPoints().addAll(0.0, 0.0, 100.0, height, 50.0, height / 2, 100.0, 0.0, 0.0, height, 50.0, height / 2);
        triangle.setFill(Color.WHITE);
        triangle.setFill(Color.WHITE);
        triangle.setStroke(Color.YELLOW);
        triangle.setStrokeWidth(10.0);
        return triangle;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("TaskBoard");

        final Pane panelsPane = new Pane();
//        panelsPane.setStyle("-fx-background-color: lightblue;");
        panelsPane.setStyle("-fx-background-color: black;");

        final BorderPane sceneRoot = new BorderPane();
//        sceneRoot.

//        BorderPane.setAlignment(panelsPane, Pos.TOP_LEFT);
//        BorderPane.setAlignment(sceneRoot, Pos.TOP_CENTER);
        sceneRoot.setCenter(panelsPane);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        primaryStage.setScene(scene);

        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));

        Matrix matrix = new Matrix(10, 30);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 30; j++) {
                TextField cell = Cell.makeCell();
                grid.add(cell, j, i);
                matrix.addCell(cell, i, j);
            }
        }

        Wrongs wrongs = new Wrongs(matrix);


        HBox controlBox = new HBox();
        TextField questionField = new TextField();
//        answerField.
//        questionField.setStyle("-fx-text-inner-color: yellow; -fx-control-inner-background:black");
//        questionField.setStyle("-fx-text-inner-color: yellow;-fx-padding: 5,0,0,0;-fx-border-insets: 0px;-fx-background-insets: 5px;-fx-control-inner-background:grey");
        questionField.setStyle("-fx-text-inner-color: yellow;-fx-padding: 5,0,0,0;-fx-border-insets: 0px;-fx-background-insets: 5px;-fx-control-inner-background:black");
        questionField.setAlignment(Pos.CENTER);
//        questionField.setFont(new Font(questionField.getFont().getName(), 24));
        questionField.setFont(new Font(Font.getFontNames().get(61), 24));

        questionField.setLayoutY(500);
        questionField.setPrefColumnCount(52);
        panelsPane.getChildren().add(grid);
        panelsPane.getChildren().add(questionField);


        sceneRoot.setBottom(controlBox);
//        panelsPane.getChildren().add(controlBox);

        List<Round> rounds = loadRounds();
        ListIterator<Round> iterator = rounds.listIterator();

        Button clear = new Button("Clear");
        Button nextRound = new Button("Next");
        Button prevRound = new Button("Prev");
        Set<Integer> usedAnswers = new HashSet<>();


        final boolean[] started = {false};
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                matrix.clear();
                panelsPane.getChildren().removeAll(wrongs.getNodes());
                wrongs.reset();
                questionField.clear();
                usedAnswers.clear();
            }
        });
        Audio introMusic = new Audio("Intro.mpeg");

        final boolean[] addQuestion = {false};

        prevRound.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addQuestion[0] = false;
                matrix.clear();
                panelsPane.getChildren().removeAll(wrongs.getNodes());
                wrongs.reset();
                questionField.clear();
                usedAnswers.clear();
                if (iterator.hasPrevious()) {
                    currentRound = iterator.previous();
                    matrix.writeStart(currentRound.getNumberOfAnswers());
                    String question = currentRound.getQuestion();
                    questionField.setText(String.format("%s: %s", currentRound.getYear(), question));
                }
            }
        });

        nextRound.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (started[0]) {
                    if (introMusic.isPlaying()) introMusic.stop();
                    if (addQuestion[0]) {
                        String question = currentRound.getQuestion();
                        questionField.setText(String.format("%s: %s", currentRound.getYear(), question));
                        addQuestion[0] = false;
                    } else {
                        matrix.clear();
                        panelsPane.getChildren().removeAll(wrongs.getNodes());
                        wrongs.reset();
                        questionField.clear();
                        usedAnswers.clear();
                        if (iterator.hasNext()) {
                            currentRound = iterator.next();
                            matrix.writeStart(currentRound.getNumberOfAnswers());
                            addQuestion[0] = true;
                        } else {
                            //TODO finish quiz
                        }
                    }
                } else {
                    started[0] = true;
                    introMusic.play();
                }
            }
        });

        //index 14/15 to write score

//        Audio ticking = new Audio("Ticking.mpeg");
        Audio ticking = new Audio("Ticking.m4a");
        Pattern numPattern = Pattern.compile("(\\d)");
        final Polygon[] tempPolygon = {null};
        EventHandler<KeyEvent> eventHandlerTextField = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //Playing the animation
                if (event.getCharacter().equals("x")) {
                    panelsPane.getChildren().add(wrongs.getX());
                    if (ticking.isPlaying()) ticking.toggle();
                    playWrong();
                } else if (event.getCharacter().equals("p")){
                    if (tempPolygon[0] == null) {
                        tempPolygon[0] = wrongs.makeLargeX();
                        panelsPane.getChildren().add(tempPolygon[0]);
                        playWrong();
                    }   else {
                        panelsPane.getChildren().remove(tempPolygon[0]);
                        tempPolygon[0] = null;
                    }
                }
                String key = event.getCharacter();
                Matcher matcher = numPattern.matcher(key);
                if (matcher.matches()) {
                    int index = Integer.parseInt(matcher.group());
                    if (ticking.isPlaying()) ticking.toggle();
                    if (currentRound.getNumberOfAnswers() >= index) {
                        if (!usedAnswers.contains(index)) {
                            usedAnswers.add(index);
                            playRight();
                            matrix.addAnswer(index, currentRound);
                        }
                    }
                }
                if (event.getCharacter().equals("t") || event.getCode().equals(KeyCode.T)) {
                    ticking.toggle();
                }
                if (event.getCharacter().equals("i")) {
                    introMusic.toggle();
                }

            }
        };
        //Adding an event handler to the text feld
        scene.addEventHandler(KeyEvent.KEY_TYPED, eventHandlerTextField);

        controlBox.getChildren().add(clear);
        controlBox.getChildren().add(nextRound);
        controlBox.getChildren().add(prevRound);
//        controlBox.getChildren().add(answerField);


//        panelsPane.getChildren().add(matrix);
//        matrix.getChildren().add(grid);
//        matrix.setBackground(bg);

//        stage.setTitle("Draggable Panels Example");
        primaryStage.show();

    }


    private void playIntro() {
        playClip("Intro.mpeg");
    }

    private void playRight() {
        playClip("Right.mpeg");
    }

    private void playWrong() {
        String musicFile = "Wrong.mpeg";
        playClip(musicFile);
    }

    private static void playClip(String file) {
        URL clip = FamilyFortunes.class.getResource(file);
//        AudioClip mApplause = new AudioClip(clip.toExternalForm());
//        mApplause.play();
    }
    /*
    Grid needs to be 10 by 30
     */

    /*This will be an individual cell which contains a single character*/
    private static class Cell {

        private static List<String> names = Font.getFontNames();
        private static int num = 0;

        public static TextField makeCell() {

            TextField textField = new TextField();
//            textField.setMaxSize(20,10);
//            textField.setText(String.valueOf(num++));
//            textField.setText(num %2==0 ?"x" : "X");
//            num++;
            textField.setPrefColumnCount(1);
            textField.setFont(new Font(names.get(61), 24));
            textField.setStyle("-fx-text-inner-color: yellow;");
            Insets padding = textField.getPadding();
//            textField.setStyle("-fx-text-inner-color: yellow;-fx-padding: 8px;-fx-border-insets: 5px;-fx-background-insets: 8px;-fx-control-inner-background:grey");
            textField.setStyle("-fx-text-inner-color: yellow;-fx-padding: 5,0,0,0;-fx-border-insets: 0px;-fx-background-insets: 5px;-fx-control-inner-background:grey");
//            textField.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            return textField;
        }
    }

    /*This will represent the board of Cells. The graphic should have a black background*/
    private static class Matrix {

        private final int rows;
        private final int col;
        List<List<TextField>> cellRows;
        int totalScore = 0;

        public Matrix(int rows, int col) {
            this.rows = rows;
            this.col = col;
            cellRows = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                cellRows.add(new ArrayList<>(col));
            }
            for (List<TextField> cellRow : cellRows) {
                for (int j = 0; j < col; j++) {
                    cellRow.add(null);
                }
            }
        }

        private void clear() {
            totalScore = 0;
            for (List<TextField> cellRow : cellRows) {
                for (TextField textField : cellRow) {
                    textField.setText("");
                }
            }
        }

        public void addCell(TextField cell, int row, int col) {
            cellRows.get(row).set(col, cell);
        }

        public TextField getCell(int row, int col) {
            return cellRows.get(row).get(col);
        }

        public void writeStart(int answers) {
            answer();
            addNums(answers);
            //TODO map number rows to the actual answers for the round
        }

        private void answer() {
            String answer = "ANSWERS";
            List<TextField> row = cellRows.get(0);
            int index = 8;
            for (int i = 0; i < answer.length(); i++) {
                TextField cell = row.get(i + index);
                cell.setText(String.valueOf(answer.charAt(i)));
            }
            String total = "TOTAL";
            index = 8;
            row = cellRows.get(rows - 1);
            for (int i = 0; i < total.length(); i++) {
                TextField cell = row.get(i + index);
                cell.setText(String.valueOf(total.charAt(i)));
            }

        }

        /*Index is non-zero indexed, i.e. from 1...*/
        private void addAnswer(int index, Round round) {
            String answer = round.getAnswer(index - 1).toUpperCase();
            List<TextField> row = cellRows.get(index);
            int startIndex = 6;
            for (int i = 0; i < answer.length(); i++) {
                TextField cell = row.get(i + startIndex);
                cell.setText(String.valueOf(answer.charAt(i)));
            }
            int score = round.getScore(index - 1);
            write(String.valueOf(score), index, score > 9 ? 24 : 25);
            //TODO update score
            totalScore += score;
            write(String.valueOf(totalScore), 9, 24);
        }

        private void write(String text, int rowIndex, int startColumn) {
            List<TextField> row = cellRows.get(rowIndex);
            for (int i = 0; i < text.length(); i++) {
                TextField cell = row.get(i + startColumn);
                cell.setText(String.valueOf(text.charAt(i)));
            }
        }

        //TODO try to add a delay to updating cells
        private void addNums(int answers) {
            for (int i = 0; i < answers; i++) {
                List<TextField> row = cellRows.get(i + 1);
                row.get(4).setText(String.valueOf(i + 1));
                row.get(5).setText(".");
            }
        }

    }

    private static class Wrongs {
        private Matrix matrix;
        private int count = 0;
        Set<Node> nodes = new HashSet<>();

        public Wrongs(Matrix matrix) {
            this.matrix = matrix;
        }

        public Polygon getX() {
            Polygon node = addWrong();
            nodes.add(node);
            return node;
        }

        private Polygon addWrong() {
            if (count < 3) {
                Polygon triangle = drawX(true);
                TextField cell = matrix.getCell(1 + count++ * 3, 27);
                triangle.setLayoutX(cell.getLayoutX() + 5);
                triangle.setLayoutY(cell.getLayoutY() + 5);
                return triangle;
            } else {
                return makeLargeX();
            }
        }

        private Polygon makeLargeX() {
            Polygon triangle = drawX(false);
            TextField cell = matrix.getCell(3, 0);
            triangle.setLayoutX(cell.getLayoutX() + 5);
            triangle.setLayoutY(cell.getLayoutY() + 5);
            return triangle;
        }

        public void reset() {
            nodes.clear();
            count = 0;
        }

        public Set<Node> getNodes() {
            return nodes;
        }

    }

    private List<Round> loadRounds() {
        //TODO check through all the rounds for the answer length.
        return Rounds.loadRoundsMonday();
    }

    private static class Audio {

//        private final AudioClip mApplause;
        private boolean playing = false;

        public Audio(String file) {
            URL clip = FamilyFortunes.class.getResource(file);
//            mApplause = new AudioClip(clip.toExternalForm());
        }

        public boolean isPlaying() {
            return playing;
        }

        public void toggle() {
            if (playing) {
                stop();
            } else {
                play();
            }
        }

        public void play() {
            playing = true;
//            mApplause.play();
        }

        public void stop() {
            playing = false;
//            mApplause.stop();
        }
    }
}
