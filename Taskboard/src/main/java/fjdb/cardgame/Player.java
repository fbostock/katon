package fjdb.cardgame;

public class Player {

    private int health;
    private Deck deck;
    private Hand hand;

    public Player(int health) {

        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public int adjustHealth(int change) {
        health += change;
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }
}
