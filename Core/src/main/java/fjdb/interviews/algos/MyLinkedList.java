package fjdb.interviews.algos;

import java.util.Iterator;

public class MyLinkedList<V> implements Iterable<V> {

    public Node<V> head = null;
    public Node<V> tail = null;

    public MyLinkedList<V> add(V value) {
        Node<V> vNode = new Node<>(value, null);
        if (tail != null) {
            tail.next = vNode;
            tail = vNode;
        } else {
            head = vNode;
            tail = head;
        }
        return this;
    }


    @Override
    public Iterator<V> iterator() {

        return new Iterator<V>() {
            Node<V> start = head;

            @Override
            public boolean hasNext() {
                return start != null;
            }

            @Override
            public V next() {
                V value = start.value;
                start = start.next;
                return value;
            }
        };
    }

    public static class Node<V> {
        V value;
        Node<V> next;

        public Node(V value, Node<V> next) {
            this.value = value;
            this.next = next;
        }

    }
}
