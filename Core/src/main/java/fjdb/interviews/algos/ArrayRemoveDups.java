package fjdb.interviews.algos;

import java.util.Arrays;

public class ArrayRemoveDups {

    public static void main(String[] args) {

        System.out.println(Arrays.toString(removeDups(new int[]{1,1,1,2,2,3})));
        System.out.println(Arrays.toString(removeDups(new int[]{1,2,3})));
        System.out.println(Arrays.toString(removeDups(new int[]{1,2,3,3,3,3})));


    }


    private static int[] removeDups(int[] input) {
        int i=0;
        for(int j=1; j<input.length; j++) {
            if (input[i] == input[j]) continue;
            i++;
            swap(input, i, j);
        }
        return Arrays.copyOf(input, i+1);
    }

    private static void swap(int[] input, int i, int j) {
        int temp = input[i];
        input[i] = input[j];
        input[j] = temp;//not necessary really
    }
}
