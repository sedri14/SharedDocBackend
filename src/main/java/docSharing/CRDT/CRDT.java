package docSharing.CRDT;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

//This class represents the crdt doc tree data structure.
@Entity
public class CRDT {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public static final int BASE = 5;       //tree root has 2^5 children
    public static final int DOC_BEGIN = 0;

    public static final int DOC_END = (int) Math.pow(2, BASE) - 1;

    public static final int BOUNDARY = 10;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "strategy_by_depth")
    private Map<Integer, Boolean> strategy;

    @OneToOne
    private TreeNode root;

    public CRDT() {
        root = TreeNode.createNewTreeNode(PositionedChar.createNewChar('$', null), Arrays.asList(new TreeNode[(int) Math.pow(2, BASE)]));
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

    public static class PositionCalculatorUtil {

        //add val
        //sub val

        //>>>used instead of alloc, in case of adding to the EOF.<<<
        public static List<Identifier> incrementByOne(List<Identifier> p) {
            int depth = p.size();
            List<Identifier> newPos = new ArrayList<>(p);
            int lsb = p.get(p.size() - 1).getDigit();

            if (isHighestPossibleInDepth(lsb, depth)) {
                //case 1: last number is the highest possible, therefore add 1 to the array.
                newPos.add(new Identifier(1));
            } else {
                //case 2: last number can be incremented by one.
                newPos.set(p.size() - 1, new Identifier(lsb + 1));
            }

            return newPos;
        }

        private static boolean isHighestPossibleInDepth(int index, int depth) {

            int highestPossibleIndex = (int) Math.pow(2, CRDT.BASE + depth - 1) - 1;
            if (depth == 1) {
                //the check for depth 1 is different  because the last array spot is saved for the EOF
                return index >= highestPossibleIndex - 1;
            } else {
                return index >= highestPossibleIndex;
            }
        }

        //test cases: [10] - 1 = [9]
        //[1] - 1 = [0] //edge case, impossible!
        //[9,1] - 1 = [9]
        //[13,14,15] = [13,14,14]
        public static List<Identifier> decrementByOne(List<Identifier> q) {
            //ld <- get last digit of q.
            //if 1 == ld
            //return copy of q without the last digit (cut it out).
            //else
            //return copy of q but the last digit - 1.

            return null;
        }
    }
}


