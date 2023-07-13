package fjdb.interviews;

public class CodilityTesting {


    public static void main(String[] args) {


        System.out.println(strokes(array1()));
        System.out.println(strokes(array2()));
        System.out.println(strokes(array3()));
    }

    private static int[] array1() {
        int[] A = new int[11];
        A[0]  = 1;
        A[1]  = 3;
        A[2]  = 2;
        A[3]  = 1;
        A[4]  = 2;
        A[5]  = 1;
        A[6]  = 5;
        A[7]  = 3;
        A[8]  = 3;
        A[9]  = 4;
        A[10] = 2;
        return A;
    }

    private static int[] array2() {
        int[] A = new int[2];
        A[0] = 5;
        A[1] = 8;
        return A;

    }
    private static int[] array3() {
        int[] A = new int[4];
        A[0] = 1;
        A[1] = 1;
        A[2] = 1;
        A[3] = 1;
        return A;
    }

    private static int maxStrokes = 1000000000;

    public static int strokes(int A[]) {

        int numStrokes = 0;
        boolean painting = true;
        int maxHeight = 0;
        for (int i : A) {
            maxHeight = Math.max(i, maxHeight);
        }

        for (int currentHeight = 1; currentHeight <= maxHeight; currentHeight++) {

            for (int arrayValue : A) {
                if (arrayValue >= currentHeight) {
                    painting = true;
                } else {
                    if (painting) {
                        numStrokes += 1;
                        painting = false;
                        if (numStrokes > maxStrokes) {
                            return -1;
                        }
                    }
                }
            }
            if (painting) numStrokes++;
            painting = false;
        }

        return numStrokes > maxStrokes ? -1 : numStrokes;
    }

    private static class InnerClass {

        public String get() {
            return "Inner";
        }
    }

}
