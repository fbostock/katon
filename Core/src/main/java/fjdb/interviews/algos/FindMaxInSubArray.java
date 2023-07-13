package fjdb.interviews.algos;

import java.util.*;

public class FindMaxInSubArray {

            /*
        [5,4,3,2,1,3,6,3,4,3,6]
        put 5 at front of queue, 4 next, then 3,2,1
        q[1,2,3,4,5]

        set 5 as max of first subarray
        remove value. q[1,2,3,4]
        get next value, 3
        remove all values from back of queue which are less than or equal to this
        q[4]
        then add it to back: q[3,4]
        set 4 as max for next subarray.
        4 from queue as position is out of context now. q[3]. 3 is now the highest in the subarray.
        get next value, 6. remove all values from back of queue <=6. q[6]
        next step:, element is 3. remove from q elements <=3. q stays as q[6]. then add 3: q[3,6]
        next step, q-> q[4,6] as 4 usurps 3.
        next step, q-> q[3,4,6]



         */


    public static void main(String[] args) {

        System.out.println(productsAboveK(new int[]{1, 6, 2, 3, 2, 1}, 12));
        System.out.println(Arrays.toString(findMaxes(new int[]{1, 2, 3, 2, 1, 5, 3, 4, 6}, 3)));//3,3,3,5,5,5,6
        System.out.println(Arrays.toString(findMaxes(new int[]{1, 2, 3, 1, 4, 5, 2, 3, 6}, 3)));//3,3,3,5,5,5,6
        System.out.println(Arrays.toString(findMaxes(new int[]{5, 4, 3, 2, 1, 2, 3, 4, 5}, 5)));//3,3,3,5,5,5,6

        System.out.println(Arrays.toString(maxProductSubsequence(new int[]{1, 2, 3, 12, 9, 10, 11})));
        System.out.println(Arrays.toString(maxProductSubsequence(new int[]{6, 7, 10, 1, 2, 3, 11, 12})));
        System.out.println(Arrays.toString(maxProductSubsequence(new int[]{1,2,12,8,9,10})));
    }

    private static int[] findMaxes(int[] input, int k) {
        int[] maxes = new int[input.length - k + 1];
        Deque<Integer> queue = new ArrayDeque<>();

        queue.add(0);
        int i = 1;
        while (i < k) {
            while (!queue.isEmpty() && input[queue.peekLast()] < input[i]) {
                queue.removeLast();
            }
            queue.addLast(i++);
        }

        int j = 0;
        while (i < input.length) {
            maxes[j] = input[queue.getFirst()];
            j++;
            if (queue.getFirst() < j) {
                queue.removeFirst();
            }
            while (!queue.isEmpty() && input[queue.peekLast()] <= input[i]) {
                queue.removeLast();
            }
            queue.addLast(i++);
        }
        maxes[j] = input[queue.getFirst()];

        return maxes;
    }


    private static void mergeTrees() {
        /*
        A binary tree has a parent node, with up to two children. They are sorted - so for node n, left is < n, right is > n.

        iterate through tree2, inOrder traversal to return nodes in ascending order
        for each, add to tree 1.

        time: tree1 n, tree2 m: m * log n
        (so this is better than

        OR given tow sorted lists of ints
        int[] first, int[] second
        int i, j = 0;
        int[] output = new int[first.length + second.length];
        while(i<first.length && j < second.length) {
            if (first[i] < second[j] {
                output[i+j] = first[i++];
            } else {
                output[i+j] = first[j++];
            }
        }
        while(i<first.length) output[i+j] = first[i++];
        while(j<second.length) output[i+j] = second[j++];



         */


    }

    private static int productsAboveK(int[] input, int k) {
        int count = 0;

        //1,2,3,4,5..
        //1 then 1,2 then 1,2,3 until product above k. then 2, then 2,3
        for (int i = 0; i < input.length; i++) {
            int prod = 1;
            for (int j = i; j < input.length; j++) {
                prod = prod * input[j];
                if (prod < k) {
                    count++;
                } else {
                    break;
                }
            }
        }
        //OR - the below algorithm is more efficient, as there's only one main loop over the input array.
        // 1,2,6,1,2,3,4,2
        // l, r, (r-l+1) = 1 :1, r-l+1=2: 3, r-l+1=3:6 4:10 (r-l+1): 1, 3, 6, 10. adding new element adds (r-l+1) new sub arrays.
        //
        int left = 0;
        int prod = 1;
        int ans = 0;
        for (int right = 0; right < input.length; right++) {
            prod = prod*input[right];
            while(prod >= k) {
                prod /= input[left];
                left++;
            }
            ans += (right-left+1);
        }


        System.out.println(ans);
        return count;
    }

    private static int[] maxProductSubsequence(int[] input) {
        //find a subsequence of input, of length 3, which gives largest product.
        //i.e. three numbers in order which gives largest product. For 1,2,3,12,9, 10,11, product is from 9,10,11 as 12 out of order.

        /*
        for 12, 2,3,12. For 9, 2,3,9 for 10, 3,9,10, for 11: 9,10,11.

        for each value, find two smaller values to the left.
        When we have a candidate, we have a list of values "to the left", starting with 1,2,3
        consider 12. Use 2,3 and 12 to make new candidate.
        a new candidate will either use first two values, and new higher value,


         */

        int n = input.length;
        int[] best = new int[]{0,0,0};//can we make this 0,0,0

        int[] largestLeft = new int[n];

        TreeSet<Integer> uniqueValues = new TreeSet<>();
        for (int i = 0; i < n; i++) {
            //TODO for each i, need to find largest element to the left which is largest but smaller than input[i]
            int value = input[i];
            Integer largest = uniqueValues.floor(value);
            if (largest != null) {
                largestLeft[i] = largest;
            } else {
                largestLeft[i] = -1;
            }
            uniqueValues.add(value);
        }

        //1,2,3,12,9, 10,11

        int m = 0;
        int prod = 1;

        for (int i = n-1; i >=0 ; i--) {
            if (input[i] > m) {
                m = input[i];
            } else {
                if (largestLeft[i] != -1) {
                    int newProd = m * input[i] * largestLeft[i];
                    if (newProd > prod) {
                        prod = newProd;
                        best[0] = largestLeft[i];
                        best[1] = input[i];
                        best[2] = m;
                    }
                }
            }
        }

        return best;
    }

}
