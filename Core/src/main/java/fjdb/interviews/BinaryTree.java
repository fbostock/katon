package fjdb.interviews;

import java.util.Objects;

public class BinaryTree {
    int x; //rom 1 to 50000
    BinaryTree l;
    BinaryTree r;

    public BinaryTree(int value) {
        x = value;
    }

    public BinaryTree(int value, BinaryTree left, BinaryTree right) {
        x = value;
        l = left;
        r = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTree tree = (BinaryTree) o;
        return x == tree.x && Objects.equals(l, tree.l) && Objects.equals(r, tree.r);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, l, r);
    }
}
