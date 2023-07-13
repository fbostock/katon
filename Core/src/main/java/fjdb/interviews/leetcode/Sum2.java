package fjdb.interviews.leetcode;

import java.util.*;

public class Sum2 {

    public static void main1(String[] args) {


        long start = System.nanoTime();
        int[] result = twoSum(new int[]{99, 98, 97, 96, 95, 94, 93, 92, 91, 0, 1, 2, 3, 6}, 5);
        double time = (System.nanoTime() - start) / 1000000.0;
        System.out.println(time);
        System.out.println(Arrays.toString(result));

        start = System.nanoTime();
        int[] result2 = twoSum2(new int[]{99, 98, 97, 96, 95, 94, 93, 92, 91, 0, 1, 2, 3, 6}, 5);
        time = (System.nanoTime() - start) / 1000000.0;
        System.out.println(time);
        System.out.println(Arrays.toString(result2));

    }

    public static int[] twoSum(int[] nums, int target) {
        int next = 1;
        while (next <= nums.length - 1) {
            for (int i = 0; next + i < nums.length; i++) {
                if (nums[i] + nums[i + next] == target)
                    return new int[]{i, i + next};
            }

            next++;
        }
        return new int[]{};
    }

    public static int[] twoSum2(int[] nums, int target) {
        int next = 0;
        while (next <= nums.length - 1) {
            for (int i = next + 1; i < nums.length; i++) {
                if (nums[i] + nums[next] == target)
                    return new int[]{next, i};
            }

            next++;
        }
        return new int[]{};
    }

    public static int[] twoSum3(int[] nums, int target) {
        Map<Integer, Integer> compliment = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (compliment.get(nums[i]) != null) {
                return new int[]{i, compliment.get(nums[i])};
            }
            compliment.put(target - nums[i], i);
        }
        return new int[]{};
    }

    // pabcadefg

    public int mac(String s) {
        int max = 0;
        char[] chars = s.toCharArray();
        Map<Character, Integer> unique = new HashMap<>();
        int start = 0;
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (unique.containsKey(aChar)) {
                max = Math.max(max, unique.size());
                Integer index = unique.get(aChar);
                for (int j = start; j <= index; j++) {
                    unique.remove(chars[j]);
                }
                start = index + 1;
            }
            unique.put(aChar, i);
        }
        max = Math.max(max, unique.size());
        return max;
    }

    public int max(String s) {
        int max = 0;
        int n = s.length();
        int i = 0;
        int j = 0;
        Set<Character> set = new HashSet<>();
        while (i < n) {
            char charAt = s.charAt(i);
            while (set.contains(charAt)) {
                set.remove(s.charAt(j));
                j++;
            }
            set.add(charAt);
            max = Math.max(max, i + 1);
            i++;
        }
        return max;
    }

    public static void main0(String[] args) {
        System.out.println("Expect 4.0: " + findMedianSortedArrays(new int[]{1, 2, 3, 4, 4, 5}, new int[]{1, 2, 6, 7, 8, 9, 10}));
        System.out.println("Expect 5.0: " + findMedianSortedArrays(new int[]{1, 5}, new int[]{4, 4, 4, 4, 6, 7, 8, 9, 10}));
    }


    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int length1 = nums1.length;
        int length2 = nums2.length;

        int targetIndex = (length1 + length2) / 2;

        double median1 = median(nums1);
        double median2 = median(nums2);
        int[] left, right;
        if (median1 < median2) {
            left = nums1;
            right = nums2;
        } else {
            left = nums2;
            right = nums1;
        }
        int index = 0;
        int value = right[index];
        while (value < left[left.length - 1]) {
            index++;
            value = right[index];
        }

        int medianIndex = left.length - 1;
        double median = left[medianIndex];
        for (int i = 0; i < index; i++) {
            if (right[i] < median) {
                medianIndex--;
                median = left[medianIndex];
            }
            if (right[i] > median) {
//                median = right[i];
                int i1 = targetIndex - medianIndex;
                if (i1 >= index) {
                    median = left[medianIndex + i1 - index];
                } else {
                    median = right[i1];

                }
                //I think the median will be right[targetIndex-median]
                break;
            }
        }

        /*
1,2,3,4,4,5    1,2,  6,7,8,9,10
         */


        return median;
    }

    private static double median(int[] array) {
        int index = array.length / 2;
        if (array.length % 2 == 0) {
            return (array[index] + array[index - 1]) / 2.0;
        } else {
            return array[index];
        }
    }


    public static void main(String[] args) {

        System.out.println(-13%12);
        System.out.println(isPalin2(10021));
        System.out.println(isPalin2(121));
        System.out.println(isPalin2(12321));
        System.out.println(isPalin2(10));
//        System.out.println(convert2("PAYPALISHIRING", 3));//PAHNAPLSIIGYIR PAHNAPLSIIGYIR
//        System.out.println(convert2("PAYPALISHIRING", 4));//PINALSIGYAHRPI PINALSIGYAHRPI
    }

    //zigzag conversion
    /*
    PAYPALISHIRING
     */
    public static String convert2(String s, int numRows) {

        List<StringBuilder> lines = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            lines.add(new StringBuilder());
        }

        int factor = 2 * numRows - 2;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            int index = i % factor;
            if (index < numRows) {
                lines.get(index).append(aChar);
            } else {
                index = factor - index;
                lines.get(index).append(aChar);
            }
        }


        StringBuilder output = new StringBuilder();
        for (StringBuilder line : lines) {
            output.append(line.toString());
        }
        return output.toString();
    }

    public static boolean isPalin(int x) {
        if (x < 0) return false;
        int dummy = x;
        x /= 10;
        int size = 1;
        while (x > 0) {
            size *= 10;
            x /= 10;
        }
        int remainder = dummy % 10;

        while (size > 1 && dummy >= 10) {
            if (dummy / size == remainder) {
                dummy = dummy - size * (dummy / size) - remainder;
                dummy /= 10;
                size /= 100;
                remainder = dummy % 10;
            } else {
                return false;
            }
        }
        return true;

    }

    public static boolean isPalin2(int x) {
        if (x < 0) return false;
        int copy = x;
//1   10  1001 10021  12321
        int reverse = 0;
        while (copy > 0) {
            reverse = 10 * reverse + copy%10;
            copy /= 10;
        }
        return reverse == x;
    }


    public static String convert(String s, int numRows) {
        /*

         */


        List<String> lines = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            lines.add("");
        }
        int col = 0;
        char[] chars = s.toCharArray();
        int rowCount = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            boolean completeColumn = col % (numRows - 1) == 0;
            if (completeColumn) {
                int index = i % (numRows + 1);
                index = index % numRows;
                lines.set(index, lines.get(index) + c);
            } else {
                int index = numRows - (col % (numRows - 1)) - 1;
                lines.set(index, lines.get(index) + c);
            }
            if (completeColumn) {//2, 6 ,10...3, 9, 15, so every
                rowCount++;
                if (rowCount % numRows == 0) {
                    col++;
                    rowCount = 0;
                }
            } else {
                col++;
            }
        }

        String output = "";
        for (String line : lines) {
            output += line;
        }
        return output;
    }
}
