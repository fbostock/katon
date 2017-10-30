/**
 * Created by Frankie Bostock on 11/06/2017.
 */
public class FrankiePlay {

    public static void main(String[] args) {


    }
    /*

    TODO
    add the maven runnable jar plugin


     */


    //TODO add a task or epic which lists tasks which are for design planning, such as a framework for persisting data
    //to xml, such that a given interface enforces the components to both write the data to file as well as read it back in again.
    //such tasks could be done on the train.

    /*private String getTradeString(Trade trade) {
        ArrayList<String> list = Lists.newArrayList();
        list.add("'" + trade.getInstrument() +"'");
        //TODO add a DateFormatters class to print these as required.
//        list.add(trade.getTradeDate().toString());
        LocalDate tradeDate = trade.getTradeDate();
        String date = DateTimeUtil.print(tradeDate);
        list.add(date == null ? "NULL" : date);
        list.add(String.valueOf(trade.getQuantity()));
        //TODO should have a price object which holds a value and currency
        list.add(String.valueOf(trade.getPrice()));
        //TODO column needs to handle conversion between currencies and back
        list.add("'"+trade.getCurrency().toString()+"'");
        list.add(String.valueOf(trade.getFixing()));
        return Joiner.on(",").join(list);
    }*/

}
