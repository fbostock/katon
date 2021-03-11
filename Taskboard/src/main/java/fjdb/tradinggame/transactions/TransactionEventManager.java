package fjdb.tradinggame.transactions;

public class TransactionEventManager {

    public static String fireEvent(TransactionEvent event) {
        Transaction transaction = new Transaction(event.buyer, event.seller);
        double total = event.pricePerUnit * event.amount;
        boolean canPurchase = transaction.checkPurchase(total);
        boolean success = false;
        if (canPurchase) {
            success = transaction.transaction(event.commodity, total, event.amount);
        }

        if (success) {
            return "Success";
        }
        return "Failed";
    }

}
