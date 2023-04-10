package docSharing.CRDT;

import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TreeNode {

    Char c;

    List<TreeNode> children;

    private TreeNode(Char c, List<TreeNode> children) {
        this.c = c;
        this.children = children;
    }

    public static TreeNode createEmptyTreeNode() {
        return new TreeNode(null, null);
    }

    public static TreeNode createNewTreeNode(Char c, List<TreeNode> children) {
        return new TreeNode(c, children);
    }



    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChar(Char c) {
        this.c = c;
    }

    public Char getChar() {
        return c;
    }

    public void initializeChildrenList(int depth) {
        this.children = new ArrayList<>(Collections.nCopies((int)Math.pow(2,CRDT.BASE + depth), null));
    }

    @Override
    public String toString() {
        return c.toString();
    }
}
