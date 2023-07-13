package fjdb.interviews.algos;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class QuickSortLinkedList {

    public static void main(String[] args) {


        List<Object> iterable = List.of(new Object());
        Validate.noNullElements(iterable);

        System.out.println(iterable.contains(null));
        Node start = new Node(4);
        Node end = start.add(Node.of(2)).add(Node.of(3)).add(Node.of(7)).add(Node.of(1)).add(Node.of(9)).add(Node.of(3)).add(Node.of(3)).add(Node.of(6)).add(Node.of(7));
        print(start);
        quickSort(start, end);
        print(start);
    }

    private static void print(Node node) {
        List<Integer> values = new ArrayList<>();
        while(node.next != null) {
            values.add(node.data);
            node = node.next;
        }

        System.out.println(Joiner.on(",").join(values));
    }


    public static Node partition(Node front, Node end) {
        //last node as the partition value
        //iterate through over nodes, swapping data based on partition value
        int pivot = end.data;
        Node i = front;
        while(i != end) {
            if (i.data < pivot) {
                swap(front, i);
                front = front.next;
            }
            if (i.next == null) {
                System.out.println();
            }
            i = i.next;
        }
        swap(front, end);
        return front;
    }

    private static void swap(Node one, Node two) {
        int temp = one.data;
        one.data = two.data;
        two.data = temp;
    }

    public static void quickSort(Node start, Node end) {
        if (start == null || end == null) return;
        if (start.previous== end) return;
        Node partition = partition(start, end);
        quickSort(start, partition.previous);
        quickSort(partition.next, end);
    }


    private static class Node {
        int data;
        Node previous;
        Node next;

        public static Node of(int data) {
            return new Node(data);
        }
        public Node(int data) {
            this.data = data;
        }

        public Node add(Node node) {
            next = node;
            node.previous = this;
            return node;
        }
    }
}
