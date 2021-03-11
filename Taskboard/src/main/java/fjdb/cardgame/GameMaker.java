package fjdb.cardgame;

public class GameMaker {

    public Player makePlayer() {
        Player player = new Player(50);
        player.setDeck(makeDeck());
        return player;
    }

    public Deck makeDeck() {
        return new Deck();
    }


}
