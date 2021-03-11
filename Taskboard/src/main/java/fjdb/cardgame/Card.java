package fjdb.cardgame;

public class Card {

    public static Card NULL = new Card(0 ,0, 0);

    private int health;
    private int trade;
    private int damage;

    public Card(int health, int trade, int damage) {
        this.health = health;
        this.trade = trade;
        this.damage = damage;
    }

    public int getHealth() {
        return health;
    }

    public int getTrade() {
        return trade;
    }

    public int getDamage() {
        return damage;
    }

//    public void setHealth(int health) {
//        this.health = health;
//    }
//
//    public void setTrade(int trade) {
//        this.trade = trade;
//    }
//
//    public void setDamage(int damage) {
//        this.damage = damage;
//    }

    public boolean scrapable() {
        return false;
    }

    public Scrapable getScrapable() {
        return null;
    }

}
