package fjdb.interviews.algos;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ReverseLinkedList {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MyLinkedList<Integer> numbers = new MyLinkedList<>();
        numbers.add(5).add(3).add(99).add(1).add(2).add(3);

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest("Javarmi123".getBytes());
        BigInteger bigInteger = new BigInteger( 1, digest);

        System.out.println(bigInteger.toString(16));
        for (Integer number : numbers) {
            System.out.println(number);
        }

        System.out.println();
        reverse(numbers);

        for (Integer number : numbers) {
            System.out.println(number);
        }

    }

    public static <V> void reverse(MyLinkedList<V> list) {
//N  ->  N2   -> N3    -> N4
        MyLinkedList.Node<V> oldHead = list.head;
        MyLinkedList.Node<V> current = oldHead;
        MyLinkedList.Node<V> previous = null;
        while(current.next != null) {

            MyLinkedList.Node next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        current.next = previous;
        list.head = current;
        list.tail = oldHead;
    }


}
