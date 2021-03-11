package fjdb.tradinggame.transactions;

import fjdb.tradinggame.Commodity;
import fjdb.tradinggame.TestGame;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    List<String> errors = new ArrayList<>();
    private final TestGame.Trader buyer;
    private final TestGame.Trader seller;

    public Transaction(TestGame.Trader buyer, TestGame.Trader seller) {
        this.buyer = buyer;
        this.seller = seller;
    }

    public boolean checkPurchase(double cost) {
        return buyer.getMoney() > cost;
    }

    //TODO this should return a type, for success or the type of failure, e.g. insufficent funds, insufficient
    //space, or other reasons as they come up (e.g. insufficient credit perhaps, or commodity ban in effect.)
    //this may replace the errors or work in combination, e.g. returns the worst, but errors include all tested.
    public boolean transaction(Commodity commodity, double cost, int amount) {
        if (checkPurchase(cost)) {
            if (buyer.tryAddMoney(-cost)) {
                buyer.getInventory().add(commodity, amount);
                seller.addMoney(cost);
                seller.getInventory().add(commodity, -amount);
                return true;
            }
            //TODO report errors or return false if transaction could not complete.
        }
        errors.add(String.format("Insufficient funds for %s", cost));
        return false;
    }

    public List<String> getErrors() {
        return errors;
    }
}
