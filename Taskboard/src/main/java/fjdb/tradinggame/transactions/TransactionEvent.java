package fjdb.tradinggame.transactions;

import fjdb.tradinggame.Commodity;
import fjdb.tradinggame.TestGame;

public class TransactionEvent {
    public TestGame.Trader buyer;
    public TestGame.Trader seller;
    public Commodity commodity;
    public int amount;
    public double pricePerUnit;

    //TODO should this include a TransactionType, e.g. purchase, loan, future contract? Implement as needed
    public TransactionEvent(TestGame.Trader buyer, TestGame.Trader seller, Commodity commodity, int amount, double pricePerUnit) {
        this.buyer = buyer;
        this.seller = seller;
        this.commodity = commodity;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
    }
}
