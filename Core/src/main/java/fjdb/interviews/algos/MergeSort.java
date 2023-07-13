package fjdb.interviews.algos;

import java.util.Arrays;
import java.util.TreeMap;

public class MergeSort {

    public static void main(String[] args) {

//        int[] array1 = new int[]{2, 1};
        int[] array1 = new int[]{5,4,3,2,1};
        int[] array2 = new int[]{1,2,3,4,5};
        int[] array3 = new int[]{5,1,4,2,3};
        int[] array4 = new int[]{5,5,5,3,3,3,2,2,2};
        int[] array5 = new int[]{5,4,5,4,5,4,2,1,2,1};

        mergeSort(array1);
        mergeSort(array2);
        mergeSort(array3);
        mergeSort(array4);
        mergeSort(array5);

        System.out.println(Arrays.toString(array1));
        System.out.println(Arrays.toString(array2));
        System.out.println(Arrays.toString(array3));
        System.out.println(Arrays.toString(array4));
        System.out.println(Arrays.toString(array5));

    }

    public static void mergeSort(int[] array) {
        mergeSort(array, 0, array.length-1);
    }

    public static void mergeSort(int[] array, int begin, int end) {

        if (begin >= end) return;
        int mid = begin + (end - begin) / 2;
        mergeSort(array, begin, mid);//0->0
        mergeSort(array, mid + 1, end); //1->1

        merge(array, begin, mid, end);
    }


    private static int merge(int[] array, int left, int mid, int right) {
        int[] leftArray = new int[mid - left + 1];
        int[] rightArray = new int[right - mid];

        for (int i = 0; i < leftArray.length; i++) {
            leftArray[i] = array[left + i];
        }
        for (int i = 0; i < rightArray.length; i++) {
            rightArray[i] = array[mid+1 + i];
        }


        //merge both into existing array, going from left to right
        int leftIndex = 0;
        int rightIndex = 0;
        int mergedIndex = left;
        while (leftIndex < leftArray.length && rightIndex < rightArray.length) {
            if (leftArray[leftIndex] < rightArray[rightIndex]) {
                array[mergedIndex] = leftArray[leftIndex];
                leftIndex++;
            } else {
                array[mergedIndex] = rightArray[rightIndex];
                rightIndex++;
            }
            mergedIndex++;
        }

        for (int i = leftIndex; i < leftArray.length; i++) {
            array[mergedIndex] = leftArray[i];
            mergedIndex++;
        }
        for (int i = rightIndex; i < rightArray.length; i++) {
            array[mergedIndex] = rightArray[i];
            mergedIndex++;
        }

        return -1;
    }
}
