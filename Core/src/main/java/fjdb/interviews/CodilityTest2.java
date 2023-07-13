package fjdb.interviews;

import java.util.*;
import java.util.function.Consumer;

public class CodilityTest2 {

    public static void main(String[] args) {

        System.out.println(solution(testTree()));
        System.out.println(solution(testTree2()));
        System.out.println(solution(testTree3()));
        System.out.println("Height: " + findHeight(testTree()));
        System.out.println("Height: " + findHeight(testTree2()));
        System.out.println("Height: " + findHeight(testTree3()));

        printTrees(CodilityTest2::printInorder, testTree() );
        printTrees(CodilityTest2::printpreorder, testTree() );
        printTrees(CodilityTest2::printPostorder, testTree() );
//        printTrees(CodilityTest2::printInorder, testTree(), testTree2(), testTree3());
//        printTrees(CodilityTest2::printpreorder, testTree(), testTree2(), testTree3());
//        printTrees(CodilityTest2::printPostorder, testTree(), testTree2(), testTree3());
    }




    private static void printTrees(Consumer<BinaryTree> printer, BinaryTree... trees) {
        for (BinaryTree tree : trees) {
            printer.accept(tree);
            System.out.println();
        }

    }

    private static BinaryTree testTree() {
/*
        root(1)
       B(2)     C(3)
   D(3) E(6)   F(4)  G(1)
  H(2)             I(5)   J(6)
 */

        BinaryTree H = makeLeaf(2);
        BinaryTree E = makeLeaf(6);
        BinaryTree F = makeLeaf(4);
        BinaryTree I = makeLeaf(5);
        BinaryTree J = makeLeaf(6);

        BinaryTree D = new BinaryTree(3, H, null);
        BinaryTree B = new BinaryTree(2, D, E);
        BinaryTree G = new BinaryTree(1, I, J);
        BinaryTree C = new BinaryTree(3, F, G);


        BinaryTree root = new BinaryTree(1, B, C);
        return root;
    }

    private static BinaryTree testTree2() {
/*
     A
  B     C
D   E  F  G
 */

        BinaryTree tree = makeLeaf(1);//D
        BinaryTree tree2 = makeLeaf(2);//E
        BinaryTree tree4 = makeLeaf(4);//F
        BinaryTree tree1_ = makeLeaf(1);//G

        BinaryTree root = new BinaryTree(1);//A
        root.l = new BinaryTree(2, tree, tree2);//B
        root.r = new BinaryTree(2, tree4, tree1_);//C
        return root;
    }

    private static BinaryTree testTree3() {
/*
        A
          B
        C   D
              E
 */

        BinaryTree tree = makeLeaf(1);//C
        BinaryTree tree1R = makeLeaf(1);//D
        tree1R.l = makeLeaf(4);//E

        BinaryTree root = new BinaryTree(1);//A
        root.r = new BinaryTree(2, tree, tree1R);//B
        return root;
    }

    private static BinaryTree makeLeaf(int value) {
        return new BinaryTree(value);
    }




    private static int solution(BinaryTree tree) {

        return checkTree(tree, new HashSet<Integer>(), 0);
    }


    private static int checkTree(BinaryTree tree, Set<Integer> distinct, int currentValue) {
        if (tree == null || distinct.contains(tree.x)) {
            return currentValue;
        } else {
            Set<Integer> newDistinct = new HashSet<>(distinct);
            newDistinct.add(tree.x);
            currentValue++;
            int leftValue = checkTree(tree.l, newDistinct, currentValue);
            int rightValue = checkTree(tree.r, newDistinct, currentValue);
            return Math.max(leftValue, rightValue);
        }
    }

    public static int findHeight(BinaryTree tree) {
        if (tree==null) {
            return 0;
        }

        int leftHeight = findHeight(tree.l);
        int rightHeight = findHeight(tree.r);
        int height = Math.max(leftHeight, rightHeight);
        return height+1;
    }

    public static void printInorder(BinaryTree tree) {

        if (tree == null) return;
        printInorder(tree.l);
        System.out.print(tree.x + ",");
        printInorder(tree.r);

    }

    public static void printpreorder(BinaryTree tree) {

        if (tree == null) return;
        System.out.print(tree.x + ",");
        printpreorder(tree.l);
        printpreorder(tree.r);

    }

    public static void printPostorder(BinaryTree tree) {

        if (tree == null) return;
        printPostorder(tree.l);
        printPostorder(tree.r);
        System.out.print(tree.x + ",");

    }
}
