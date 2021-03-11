package fjdb.cardgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    List<Card> cards = new ArrayList<>();
    List<Card> discardPile = new ArrayList<>();
    private final Random random = new Random();

    public Hand drawHand() {
        List<Card> newHand = new ArrayList<>();
        if (cards.size() < 5) {
            newHand.addAll(cards);
            cards.clear();
            shuffle();
        }
        while (cards.size() > 0 && newHand.size() < 5) {
            newHand.add(cards.remove(0));
        }

        return new Hand(newHand);
    }

    public Card drawCard() {
        if (cards.size() > 0) {
            return cards.get(0);
        } else {
            shuffle();
            if (cards.size() > 0) {
                return cards.get(0);
            } else {
                return null;
            }
        }
    }

    public List<Card> drawCards(int n) {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;
        while(cards.size() < n && (card = drawCard()) != null) {
            cards.add(card);
        }
        return cards;
    }

    public void shuffle() {
        cards.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(cards, random);
    }

}


