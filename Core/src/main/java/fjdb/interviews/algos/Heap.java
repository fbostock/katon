package fjdb.interviews.algos;

import com.google.common.collect.Lists;

import java.util.List;

public class Heap {

    public static void main(String[] args) {
        //TODO first start with a min heap
        Heap heap = new Heap();

        heap.insertKey(5);
        heap.insertKey(4);
        heap.insertKey(3);
        heap.insertKey(1);
        heap.insertKey(2);

        while(heap.size > 1) {
            System.out.println(heap.extractMin());
        }

        heap.insertKey(2);
        heap.insertKey(40);
        heap.insertKey(30);
        heap.insertKey(10);
        heap.insertKey(20);

        while(heap.size > 0) {
            System.out.println(heap.extractMin());
        }

    }

    private List<Integer> contents = Lists.newArrayList();
    private int size = 0;

    public int getMin() {
        return size() > 0 ? contents.get(0) : Integer.MIN_VALUE;
    }

    public int extractMin() {
        if (size <= 0) return Integer.MIN_VALUE;
        if (size == 1) {
            size--;
            return contents.get(0);
        }
        Integer root = contents.get(0);
        swap(0, size-1);
        size--;
        heapify(0);
        return root;
    }

    public void insertKey(int value) {
        if (contents.size() > size) {
            contents.set(size, value);
        } else {
            contents.add(value);
        }
        size++;

        int i = size - 1;
        while (i != 0 && contents.get(parent(i)) > value) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public void deleteKey(int index) {
        decreaseKey(index, Integer.MIN_VALUE);
        extractMin();
    }

    public void decreaseKey(int index, int newValue) {
        //change key at index i to newValue, then ensure heap is valid
        contents.set(index, newValue);
        int i = index;
        while (i != 0 && contents.get(parent(i)) > newValue) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public int size() {
        return size;
    }


    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private void heapify(int i) {
        //TODO apply the heap property. recursively iterate through heap, ensuring each tree at node i is a valid min-heap
        //perform on left, and on right.
        //
        int smallest = i;
        Integer nodeValue = contents.get(i);
        int left = left(i);
        int right = right(i);
        if (left < size && contents.get(left) < nodeValue) {
            smallest = left;
        }
        if (right < size && contents.get(right) < contents.get(smallest)) {
            smallest = right;
        }
        if (smallest != i) {
            swap(i, smallest);
            heapify(smallest);
        }
    }

    private void swap(int from, int to) {
        Integer temp = contents.get(to);
        contents.set(to, contents.get(from));
        contents.set(from, temp);
    }
}
