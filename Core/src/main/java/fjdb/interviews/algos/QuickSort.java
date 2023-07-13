package fjdb.interviews.algos;

import java.util.Arrays;

public class QuickSort<T> {


    public static void main(String[] args) {

        int[] array1 = new int[]{5,4,3,2,1};
        int[] array2 = new int[]{1,2,3,4,5};
        int[] array3 = new int[]{5,1,4,2,3};
        int[] array4 = new int[]{5,5,5,3,3,3,2,2,2};
        int[] array5 = new int[]{5,4,5,4,5,4,2,1,2,1};
Arrays.copyOf(new int[]{}, 4);
        quickSort(array1);
        quickSort(array2);
        quickSort(array3);
        quickSort(array4);
        quickSort(array5);

        System.out.println(Arrays.toString(array1));
        System.out.println(Arrays.toString(array2));
        System.out.println(Arrays.toString(array3));
        System.out.println(Arrays.toString(array4));
        System.out.println(Arrays.toString(array5));
    }



    public static void quickSort(int[] array) {
        quickSort(array, 0, array.length-1);
    }

    public static <T> void quickSort(int[] array, int l, int h) {

        if (l < h) {
            int partition = partition(array, l, h);

            quickSort(array, l, partition-1);
            quickSort(array, partition+1, h);


        }
    }

    private static int partition(int[] array, int low, int high) {

        int pivot = array[high];

        int i  = low-1;

        for (int j = low; j < high; j++) {

            if (array[j] < pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i+1, high);


        return i+1;
    }

    private static void swap(int[] array, int i, int j) {
        int a = array[i];
        int b = array[j];
        array[i] = b;
        array[j] = a;
    }

    //TODO quick sort for objects with a comparator
}
