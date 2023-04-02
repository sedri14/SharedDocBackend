package docSharing.CRDT;

import docSharing.CRDT.TreeNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CRDT {

    //public static final double BASE = Math.pow(2,5);
    public static final double BASE = 5;       //tree root has 2^5 children
    public static final int DOC_BEGIN = 0;

    public static final int DOC_END = (int)Math.pow(2,BASE) - 1;

    public static final int BOUNDARY = 10;

    private Map<Integer, Boolean> strategy;

    private TreeNode head;

    public CRDT() {
        head = TreeNode.createNewTreeNode(null, Arrays.asList(new TreeNode[(int)Math.pow(2,BASE)]));
        iniBeginAndEndOfDocument(head);
        strategy = new HashMap<>();
    }

    //this function initialize the beginning and end nodes
    //these two special nodes has no character values
    //they are both in depth 1 of the crdt tree
    //and their positions are: [0] and [2^BASE - 1] respectively.
    private void iniBeginAndEndOfDocument(TreeNode head) {
        head.children.add(DOC_BEGIN, TreeNode.createDocBeginNode());
        head.children.add(DOC_END, TreeNode.createDocEndNode());
    }

    public Map<Integer, Boolean> getStrategy() {
        return strategy;
    }
}


