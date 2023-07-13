package fjdb.interviews;

public class CodilityTest3 {

    public static void main(String[] args) {

        System.out.println(solution(test1()));
        System.out.println(solution(test2()));
        System.out.println(solution(test3()));
        System.out.println(solution(test4()));
    }

    public  static String test1() {
        return "011100";
    }

    public static String test2() {
        return "111";
    }

    public static  String test3() {
        return "1111010101111";
    }
    public static  String test4() {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < 400000; i++) {
            value.append("1");
        }
        return value.toString();

    }

    public static int solution(String S) {
        // Implement your solution here
        //111
        //010001
        //110011

        int zeroes = 0;
        int count = 0;
        char values[] = S.toCharArray();
        for (int i = values.length-1; i >-1 ; i--) {
            char c = values[i];
            if (c == '1') {
                if (zeroes > 0) {
                    count += zeroes;
                }
                count +=1;
                zeroes = 1;
            } else {//0
                zeroes ++;
            }
        }


        return count;
    }
}
