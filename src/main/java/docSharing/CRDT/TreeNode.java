package docSharing.CRDT;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class TreeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    PositionedChar c;

    @OneToMany
    List<TreeNode> children;

    TreeNode() {

    }

    private TreeNode(PositionedChar c, List<TreeNode> children) {
        this.c = c;
        this.children = children;
    }

    public static TreeNode createEmptyTreeNode() {
        return new TreeNode(null, null);
    }

    public static TreeNode createNewTreeNode(PositionedChar c, List<TreeNode> children) {
        return new TreeNode(c, children);
    }



    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChar(PositionedChar c) {
        this.c = c;
    }

    public PositionedChar getChar() {
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
