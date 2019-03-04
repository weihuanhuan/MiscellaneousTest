package search;


import java.util.Random;

/**
 * Created by JasonFitch on 9/20/2018.
 */
public class BinarySearchTree {

    public static void main(String[] args) {

        Random random = new Random(System.currentTimeMillis());

        Tree tree = new Tree();
        for (int i = 0; i < 5; ++i)
            tree.insert(random.nextInt(100));

        System.out.println("tree:");
        tree.travel();
        int dest = 23;
        System.out.println("search:" + dest);
        System.out.println("result:" + tree.searech(dest));
    }
}

class Node {

    public Integer data;
    public Node left;
    public Node right;

    public Node(Integer data, Node left, Node right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return data.toString();
    }
}


class Tree {

    private Node root;
    private Node dest;
    private boolean isFound = false;

    public Tree() {
        this.root = null;
    }

    public void insert(int v) {

        root = insert(root, v);
    }

    private Node insert(Node node, int v) {
        if (node == null) {
            node = new Node(v, null, null);

        } else if (v < node.data) {
            node.left = insert(node.left, v);
        } else if (v > node.data) {
            node.right = insert(node.right, v);
        } else {
            //exist
        }
        return node;
    }

    public Node searech(int dest) {

        search(root, dest);
        return this.dest;
    }

    private void search(Node node, int dest) {

        if (null != node.left)
            search(node.left, dest);
        if (node.data > dest && !isFound) {
            this.dest = node;
            isFound = true;
            return;
        }
        if (null != node.right)
            search(node.right, dest);
    }

    public void travel() {
        travel(root);
    }

    private void travel(Node node) {
        if (null != node.left)
            travel(node.left);
        System.out.println(node.data);
        if (null != node.right)
            travel(node.right);
    }

}
