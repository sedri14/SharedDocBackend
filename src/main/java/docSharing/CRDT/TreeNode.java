package docSharing.CRDT;

import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeNode {

    Char c;

    List<TreeNode> children;

    private TreeNode(Char c, List<TreeNode> children) {
        this.c = c;
        this.children = children;
    }

    public static TreeNode createNewTreeNode(Char c) {
        return new TreeNode(c, null);
    }

    public static TreeNode createNewTreeNode(Char c, List<TreeNode> children) {
        return new TreeNode(c, children);
    }

    //TODO: combine 2 functions - reuse code
    public static TreeNode createDocBeginNode() {
        List<Identifier> position = Arrays.asList(new Identifier(CRDT.DOC_BEGIN));
        Char dummyChar = Char.createNewChar(null, position);

        return TreeNode.createNewTreeNode(dummyChar, null);
    }

    public static TreeNode createDocEndNode() {
        List<Identifier> position = Arrays.asList(new Identifier(CRDT.DOC_END));
        Char dummyChar = Char.createNewChar(null, position);

        return TreeNode.createNewTreeNode(dummyChar, null);

    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChar(Char c) {
        this.c = c;
    }
}
