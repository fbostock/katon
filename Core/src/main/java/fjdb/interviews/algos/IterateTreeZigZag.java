package fjdb.interviews.algos;

import java.util.*;

public class IterateTreeZigZag {

    public static void main(String[] args) {
        Node tree = build(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});

        printZigZag(tree);
connectNodesAtSameLevelInTree(tree);
    }

    private static Node build(int[] input) {

        int i = 0;
        Map<Integer, Node> nodes = new HashMap<>();
        Node rootNode = new Node(input[0]);
        nodes.put(i, rootNode);
        Node node = rootNode;
        while (i < input.length) {
            node = nodes.get(i);
            if (input.length > 2 * i + 1) {
                Node left = new Node(input[2 * i + 1]);
                nodes.put(2 * i + 1, left);
                node.left = left;
            }
            if (input.length > 2 * i + 2) {

                Node right = new Node(input[2 * i + 2]);
                nodes.put(2 * i + 2, right);
                node.right = right;
            }
            i++;
        }
        return rootNode;
    }


    public static void printZigZag(Node rootNode) {
        //first node 0, then 2,1, then 3,4,5,6, then back again, 14, 13...
        Stack<Node> leftRightStack = new Stack<>();
        Stack<Node> rightLeftStack = new Stack<>();
        leftRightStack.add(rootNode);
        while (!leftRightStack.isEmpty() || !rightLeftStack.isEmpty()) {
            Node node;
            if (rightLeftStack.isEmpty()) {
                while (!leftRightStack.isEmpty()) {
                    node = leftRightStack.pop();
                    if (node.left != null) rightLeftStack.add(node.left);
                    if (node.right != null) rightLeftStack.add(node.right);
                    System.out.println(node.value);
                }
            } else {
                while (!rightLeftStack.isEmpty()) {
                    node = rightLeftStack.pop();
                    if (node.right != null) leftRightStack.add(node.right);
                    if (node.left != null) leftRightStack.add(node.left);
                    System.out.println(node.value);
                }
            }
        }


    }

    private static void connectNodesAtSameLevelInTree(Node tree) {
        List<LinkedList<Node>> allLists = new ArrayList<>();
        Queue<Node> nodes1 = new ArrayDeque<>();
        Queue<Node> nodes2 = new ArrayDeque<>();
        nodes1.add(tree);
        while (!nodes1.isEmpty() || !nodes2.isEmpty()) {
            LinkedList<Node> list = new LinkedList<>();
            if (nodes1.isEmpty()) {
                while(!nodes2.isEmpty()) {
                Node node = nodes2.poll();
                    if (node.left != null) nodes1.add(node.left);
                    if (node.right != null) nodes1.add(node.right);
                    list.add(node);
                }
            } else {
                while(!nodes1.isEmpty()) {
                 Node node = nodes1.poll();
                    if (node.left != null) nodes2.add(node.left);
                    if (node.right != null) nodes2.add(node.right);
                    list.add(node);
                }
            }
            allLists.add(list);
        }
        //create queue, add tree, iterate through queue creating a linkedList

        for (LinkedList<Node> allList : allLists) {
            System.out.println(allList);
        }

    }

    private static class Node {
        Node left = null;
        Node right = null;
        int value;

        public Node(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Val: %d left=%s right=%s", value, left==null ? "NULL" : left.value, right==null ? "NULL" : right.value);
        }
    }
}
