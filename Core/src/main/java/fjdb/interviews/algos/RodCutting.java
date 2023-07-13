package fjdb.interviews.algos;

public class RodCutting {

    private static int[] prices = new int[]{1, 5, 8,9,10,17,17,20, 20, 20};

    public static void main(String[] args) {

        for (int i = 1; i < 10; i++) {
            int rodLength = i;
            int price = rodCut(rodLength, true);
            System.out.println("Length: " + rodLength + " price: " + price);

        }

    }


    private static int rodCut(int n, boolean check) {
        //iterate over 1 to n. price is the max of price(n) and price(i) + rodCut(n-i)
        int wholeRodPrice = price(n);
        int maxPrice = wholeRodPrice;
        for (int i = 1; i < n; i++) {
            int tryMax = price(i) + rodCut(n-i, false);
            if (tryMax > maxPrice) {
                maxPrice = tryMax;
                if (check) {
                    System.out.println("Max achieved with i: " + i + ", n-i: " + (n-i));
                }
            }
            maxPrice = Math.max(maxPrice, tryMax);
        }
        return maxPrice;
    }

    private static int price(int size) {
        return prices[size-1];
    }
}
