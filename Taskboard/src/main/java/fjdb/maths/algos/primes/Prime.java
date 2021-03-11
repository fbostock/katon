package fjdb.maths.algos.primes;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

public class Prime {

    //TODO class for calculating prime numbers
    //methods to retrieve first n primes, primes between a,b etc.
    //store code for calculating primes with different approaches, just for fun.
    //e.g. one using regex - inefficient but fun! Others which store primes as they go.

    //TODO print out all primes below 1000. Then learn them for fun.
/*
2,3,5,7,11,13,17,19,23,29
31,37,41,43,47,53,59,61,67,71
73,79,83,89,97,101,103,107,109,113
127,131,137,139,149,151,157,163,167,173
179,181,191,193,197,199,211,223,227,229
233,239,241,251,257,263,269,271,277,281
283,293,307,311,313,317,331,337,347,349
353,359,367,373,379,383,389,397,401,409
419,421,431,433,439,443,449,457,461,463
467,479,487,491,499,503,509,521,523,541
547,557,563,569,571,577,587,593,599,601
607,613,617,619,631,641,643,647,653,659
661,673,677,683,691,701,709,719,727,733
739,743,751,757,761,769,773,787,797,809
811,821,823,827,829,839,853,857,859,863
877,881,883,887,907,911,919,929,937,941
947,953,967,971,977,983,991,997
 */
    public static void main(String[] args) {
        int max = 1000;
        List<Integer> primes = getPrimes(max);
//        for (Integer prime : primes) {
//            System.out.println(prime);
//        }

        List<List<Integer>> partition = Lists.partition(primes, 10);
        for (List<Integer> sub : partition) {
            System.out.println(Joiner.on(",").join(sub));
        }

        System.out.println(String.format("%s: %s", max, primes.size()));

    }
    public static List<Integer> getPrimes(int max) {
        List<Integer> primes = Lists.newArrayList(2);
        int val = 3;
        while(val <= max) {
            boolean isPrime = true;
            for (Integer prime : primes) {
                if (val %prime == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primes.add(val);
            }
            val+=2;
        }
        return primes;
    }

    public boolean isPrime(int num) {
        if (num == 1) return false;
        if (num % 2 == 0) return false;

        for (int i = 3; i < num; i++) {
            if (num%i==0) return false;
        }
        return true;
    }
}
