package fjdb.maths.algos.primes;

import java.util.List;

public class PrimePlay {

    public static void main(String[] args) {
//        oddsTwoFromPrime(1000);
        evensSumofPrimes(100000);
    }

    /**
     * Find odd numbers that are more than two from a prime number, up to the value max.
     */
    private static void oddsTwoFromPrime(int max) {
        //From 1 up to max, check each odd number to see if it is two away from a prime.
        List<Integer> primes = Prime.getPrimes(max);
        int primeIndex = 0;
        for (int i = 3; i <= max; i += 2) {
            while (i > primes.get(primeIndex)) {
                primeIndex++;
            }
            if (primeIndex == primes.size() - 1) break;
            int primeBefore = primes.get(primeIndex - 1);
            int primeAfter = primes.get(primeIndex);
            if (i - 2 > primeBefore && i + 2 < primeAfter) {
                System.out.println(String.format("%s: previous prime %s next prime %s", i, primeBefore, primeAfter));
            }
            if (primeBefore > i || primeAfter < i) {
                throw new RuntimeException(String.format("%s: previous prime %s next prime %s", i, primeBefore, primeAfter));
            }
        }
    }

    /**
     * This method was to sanity check whether there are any obvious cases where an even number is not a sum of two primes
     */
    private static void evensSumofPrimes(int max) {
        List<Integer> primes = Prime.getPrimes(max);
        System.out.println(String.format("%s primes calculated", primes.size()));
//        Set<Integer> primeSet = Sets.newHashSet(primes);
        int candidates = 0;
        for (int testValue = 4; testValue < max; testValue += 2) {
            boolean found = false;
            for (int j = 0; j < primes.size() && !found; j++) {
                Integer firstPrime = primes.get(j);
                if (firstPrime > testValue) break;
                for (int k = 0; k < primes.size() && !found; k++) {
                    Integer secondPrime = primes.get(k);
                    if (secondPrime > testValue) break;
                    if (firstPrime + secondPrime == testValue) {
                        found = true;
                    }
                }

            }
            if (!found) {
                candidates++;
                System.out.println(String.format("%s not the sum of two primes", testValue));
            }
        }

        if (candidates==0) {
            System.out.println(String.format("None found for numbers up to %s", max));
        }
    }
}
