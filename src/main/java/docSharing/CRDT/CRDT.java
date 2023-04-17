package docSharing.CRDT;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This class represents the crdt doc tree data structure.
@Entity
public class CRDT {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public static final int BASE = 5;       //tree root has 2^5 children
    public static final int DOC_BEGIN = 0;

    public static final int DOC_END = (int)Math.pow(2,BASE) - 1;

    public static final int BOUNDARY = 10;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "strategy_by_depth")
    private Map<Integer, Boolean> strategy;

    @OneToOne
    private TreeNode root;

    public CRDT() {
        root = TreeNode.createNewTreeNode(PositionedChar.createNewChar('$',null), Arrays.asList(new TreeNode[(int)Math.pow(2,BASE)]));
        initDocumentBoundaries(root);
        strategy = new HashMap<>();
    }

    //this function initialize the beginning and end nodes
    //these two special nodes holds the characters '<' and '>' respectively.
    //they are both in depth 1 of the crdt tree
    //and their positions are: [0] and [2^BASE - 1] respectively.
    private void initDocumentBoundaries(TreeNode root) {
        //init start
        List<Identifier> startPos = Arrays.asList(new Identifier(CRDT.DOC_BEGIN));
        PositionedChar startChar = PositionedChar.createNewChar('<', startPos);
        root.children.set(DOC_BEGIN, TreeNode.createNewTreeNode(startChar, null));

        //init end
        List<Identifier> endPos = Arrays.asList(new Identifier(CRDT.DOC_END));
        PositionedChar endChar = PositionedChar.createNewChar('>', endPos);
        root.children.set(DOC_END, TreeNode.createNewTreeNode(endChar, null));
    }

    public Map<Integer, Boolean> getStrategy() {
        return strategy;
    }

    public TreeNode getRoot() {
        return root;
    }
}


