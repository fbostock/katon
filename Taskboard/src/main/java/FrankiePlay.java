/**
 * Created by Frankie Bostock on 11/06/2017.
 */
public class FrankiePlay {



    public static void main(String[] args) {

//        System.out.println(String.format("%s", test(1.12)));
//    if (true) return;

    Double target = 152000.0;

//    double initial = 1.05;
    double initial = 2.0;
    double increment = 0.01;
    double r = initial;
    boolean decreasing = true;
    while(true) {
        //if value positive, need to decrease r
        double value = test(r) - target;
        if (value>0) {
            if (!decreasing) {
                decreasing = !decreasing;
                increment /= 10.0;
            }
            r -=increment;
        } else {
            if (decreasing) {
                increment /= 10.0;
                decreasing = !decreasing;
            }
            r +=increment;
        }
        if (Math.abs(value) < 1.0) break;
    }
        System.out.println("Rate: " + r);

    //initial * (1+r)^t
/*
Start Sep 2017, but assume at April 2018.
2018: 71000
2019: x + 20000
2020: z + 20000
2021:

(((71000 * R) + 20000) * R + 20000) * R = 148000


 */

    }

    private static double test(Double r) {
        return (((71000 * r) + 20000) * r + 20000) * r;
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
