public class RedBlackTree {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private class Node {
        int key;
        Node left, right, parent;
        boolean color;
        
        Node(int key) {
            this.key = key;
            this.color = RED;
        }
    }
    
    private Node root;
    
    public void insert(int key) {
        Node node = new Node(key);
        if (root == null) {
            root = node;
            root.color = BLACK;
            return;
        }
        insertNode(root, node);
        fixInsertion(node);
    }
}
