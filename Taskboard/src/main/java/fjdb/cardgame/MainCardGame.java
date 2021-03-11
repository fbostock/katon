package fjdb.cardgame;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCardGame extends Application {

    /*


    public static void main(String[] args) {

        GameMaker gameMaker = new GameMaker();
        Player player1 = gameMaker.makePlayer();
        Player player2 = gameMaker.makePlayer();

        //

        JPanel gameArea = new JPanel();


        JButton draw = new JButton("Draw");
        JButton playAll = new JButton("Play All");


        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);


    }

*/
    @Override
    public void start(Stage primaryStage) throws Exception {

        GameMaker gameMaker = new GameMaker();
        Player player1 = gameMaker.makePlayer();
        Player player2 = gameMaker.makePlayer();

        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        //A canvas which will be a static picture in the background. Then nodes exist on top
        Canvas canvas = new Canvas(700, 700);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        CardNode cardNode = new CardNode(new Card(1, 1, 1));
        cardNode.setTranslateX(50);
        cardNode.setTranslateY(100);

        root.getChildren().add(canvas);
        root.getChildren().add(cardNode);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();



    }

    private static class CardSet extends Group {
        private List<Card> _cards;
        Map<Card, CardNode> _cardNodes = new HashMap<>();

        public CardSet(List<Card> cards) {
            _cards = cards;
            for (Card card : cards) {
                _cardNodes.put(card, new CardNode(card));
                getChildren().add(_cardNodes.get(card));
            }
        }

        public Card get(int index) {
            return _cards.get(index);
        }

        public void set(int index, Card card) {
            Card oldCard = _cards.set(index, card);
            CardNode cardNode = _cardNodes.get(oldCard);
            _cardNodes.put(card, new CardNode(card));
            getChildren().remove(cardNode);
            int i = getChildren().indexOf(cardNode);
            getChildren().set(i, _cardNodes.get(card));
        }

        public void add(Card card) {
            _cards.add(card);
            _cardNodes.put(card, new CardNode(card));
            getChildren().add(_cardNodes.get(card));
        }

        public void remove(int index) {
            Card remove = _cards.remove(index);
            remove(remove);
        }

        public void remove(Card card) {
            _cards.remove(card);
            CardNode remove = _cardNodes.remove(card);
            getChildren().remove(remove);
        }

    }

    private static class CentralDeck {
        CardSet _cardSet;
        Deck deck = new Deck();
//        List<Card> cards = new ArrayList<>();

        public CentralDeck() {
//            cards.addAll();
            _cardSet = new CardSet(deck.drawCards(5));
        }

        public Card takeCard(int index) {
            Card card = _cardSet.get(index);
            Card newCard = deck.drawCard();
            newCard = newCard == null ? Card.NULL : newCard;
            _cardSet.set(index, newCard);
            return card;
        }
    }


    public static class CardNode extends Group {
        private Card card;

        public CardNode(Card card) {
            this.card = card;
            Rectangle box = new Rectangle();
            box.setHeight(100);
            box.setWidth(60);

            box.setFill(Color.RED);
            box.setCache(true);
            getChildren().add(box);
        }
    }
}
