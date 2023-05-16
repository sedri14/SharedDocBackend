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

    //todo: a possible change: TreeNode is just a position, inside this position there can be couple of letters,
    //todo: differentiated by site id. so each treenode should have a List of Tuples: siteid, char
    @OneToOne(cascade = CascadeType.PERSIST)
    PositionedChar c;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn
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
