package fjdb.tradinggame;

import fjdb.tradinggame.transactions.TransactionEvent;
import fjdb.tradinggame.transactions.TransactionEventManager;

import java.util.List;

public class TestGame {

    public static void main(String[] args) {

        /*
        make apples commodity, and attempt to buy them, adding to inventory.



        Create a market object, which has an inventory consisting of a supply of apples


        To buy:
        select x apples at price p

        check buyer has x*p available to pay.
        check buyer has x capacity available to store.
        on either fail, provide feedback on failures and return.


        remove x from seller.
        add x to buyer.
        subtract xp from buyer.
        add xp to seller.

        update UI.


         */

        TraderGenerator traderGenerator = new TraderGenerator();
        Market market = traderGenerator.makeMarket();
        Person mainPlayer = traderGenerator.makePerson();

        //load the inventory content of the market

        //fire a TransactionEvent



        //this would be displayed somehow

        //assume we have selected x apples at price p


        int amount = 5;
        double price = 0.5;
        TransactionEvent event = new TransactionEvent(mainPlayer, market, Commodity.APPLE, amount, price);
        String result = TransactionEventManager.fireEvent(event);



        TraderDebugger.print(mainPlayer);
        TraderDebugger.print(market);

    }


    /*An interface for things which can trade - they have inventories, they have money,
     * they can buyer and sell things */
    public interface Trader {
        String getName();
        double getMoney();

        double addMoney(double money);

        Inventory getInventory();

        boolean tryAddMoney(double money);

    }

    public static class Market implements Trader {
        private Inventory inventory = new Inventory();
        private double money;

        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public String getName() {
            return "Market name";
        }

        public double getMoney() {
            return money;
        }

        public double addMoney(double money) {
            this.money += money;
            return this.money;
        }

        public boolean tryAddMoney(double money) {
            synchronized (this) {
                if (this.money + money > 0) {
                    this.money += money;
                    return true;
                }
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("%s £%s", getName(), getMoney());
        }
    }

    public static class Person implements Trader {
        private Inventory inventory = new Inventory();
        private double money;

        public Person(double money) {
            this.money = money;
        }

        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public String getName() {
            return "Player name";
        }

        public double getMoney() {
            return money;
        }

        public double addMoney(double money) {
            this.money += money;
            return this.money;
        }

        public boolean tryAddMoney(double money) {
            synchronized (this) {
                if (this.money + money > 0) {
                    this.money += money;
                    return true;
                }
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("%s £%s", getName(), getMoney());
        }


    }

    /*
    A factory for making Trader objects. Will be configurable to change how markets for instance
    are created.
     */
    public static class TraderGenerator {
        public Market makeMarket() {
            return new Market();
        }

        public Person makePerson() {
            return new Person(100);
        }
    }

    /**
     * Debugging class, prints out properties of trader objects for information purposes.
     */
    private static class TraderDebugger {

        public static String print(Trader trader) {

            String content = String.format("Type: %s Name: %s Money: %s\n", trader.getClass(), trader.getName(), trader.getMoney());
            content += printInventory(trader);

            System.out.println(content);
            return content;
        }

        public static String printInventory(Trader trader) {
            StringBuilder content = new StringBuilder();
            Inventory inventory = trader.getInventory();
            List<Commodity> commodities = inventory.getCommodities();
            for (Commodity commodity : commodities) {
                content.append(String.format("%s %d", commodity, inventory.getQuantity(commodity)));
            }
            return content.toString();
        }
    }

}
