package docSharing.service;

import docSharing.CRDT.PositionedChar;
import docSharing.CRDT.Identifier;
import docSharing.CRDT.CRDT;
import docSharing.CRDT.TreeNode;
import docSharing.entities.Document;
import docSharing.exceptions.INodeNotFoundException;
import docSharing.repository.DocRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class DocService {

    private static final int boundary = 10;
    @Autowired
    private DocRepository docRepository;

    static Map<Long, String> docContentByDocId = new HashMap<>();
    static Map<Long, List<String>> connectedUsersByDocId = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(DocService.class.getName());

    public DocService() {
        logger.info("init Doc Service instance");

        Runnable saveContentToDBRunnable = new Runnable() {
            public void run() {
                //saveAllChangesToDB(docContentByDocId);
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(saveContentToDBRunnable, 0, 5, TimeUnit.SECONDS);

    }

    public Document fetchDocumentById(Long id) {
        return docRepository.findById(id).orElseThrow(() -> new INodeNotFoundException("Document not found with id " + id));
    }

    /**
     * @param map documents content by docId hashMap.
     */
    public void saveAllChangesToDB(Map<Long, String> map) {
        logger.info("start saveChangesToDB function");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            saveOneDocContentToDB(entry.getKey(), entry.getValue());
        }
    }


    /**
     * @param docId           document id
     * @param documentContent document new content
     */
    private void saveOneDocContentToDB(Long docId, String documentContent) {

        logger.info("start saveOneDocContentToDB function");
        boolean docIsPresent = docRepository.findById(docId).isPresent();
        if (!docIsPresent) {
            logger.error("there is no document with this id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
            //this should be changed.
        }
        Document doc = docRepository.findById(docId).get();
        //doc.setContent(documentContent);

        docRepository.save(doc);
        logger.info("document is saved");

    }

    public List<PositionedChar> getDocumentWithRawText(CRDT crdt) {
        //go over crdt tree and extract the values with their position array, add to the result list.
        return preorderTraversal(crdt);
    }


    public List<String> addUserToDocConnectedUsers(Long docId, String userEmail) {
        logger.info("{} is ADDED to connected users of doc {}", userEmail, docId);
        if (connectedUsersByDocId.containsKey(docId)) {
            if (!connectedUsersByDocId.get(docId).contains(userEmail)) {
                connectedUsersByDocId.get(docId).add(userEmail);
            }
        } else {
            List<String> list = new ArrayList<>();
            list.add(userEmail);
            connectedUsersByDocId.put(docId, list);
        }
        logger.info("Someone joined - Online users: {}", connectedUsersByDocId.get(docId));

        return connectedUsersByDocId.get(docId);
    }

    public List<String> removeUserFromDocConnectedUsers(Long docId, String userEmail) {
        logger.info("{} is REMOVED from connected users of doc {}", userEmail, docId);
        if (connectedUsersByDocId.containsKey(docId)) {
            connectedUsersByDocId.get(docId).remove(userEmail);
            if (connectedUsersByDocId.get(docId).isEmpty()) {
                connectedUsersByDocId.remove(docId);
            }
        }
        logger.info("Someone left - Online users: {}", connectedUsersByDocId.get(docId));

        return connectedUsersByDocId.get(docId);
    }

    /**
     * @param docId document id
     * @return id of the owner of that document
     */
    public Long getOwner(Long docId) {
        logger.info("start getOwner function");
        boolean isDocument = docRepository.findById(docId).isPresent();
        if (!isDocument) {
            logger.error("there is no document with this id");
            throw new IllegalArgumentException("there is no document with this id");
        }
        Document doc = docRepository.findById(docId).get();
        return doc.getOwner().getId();

    }

    //convert the crdt doc tree to a list of PositionedChar object, using pre-order traversal algorithm.
    public List<PositionedChar> preorderTraversal(CRDT crdt) {
        List<PositionedChar> positionedChars = new ArrayList<>();
        rec(crdt.getRoot(), positionedChars);

        return positionedChars.subList(2, positionedChars.size() - 1);    //remove root, begin and end characters
    }

    public void rec(TreeNode root, List<PositionedChar> positionedChars) {
        if (null == root) {
            return;
        }

        positionedChars.add(root.getChar());
        if (null != root.getChildren()) {
            for (int i = 0; i < root.getChildren().size(); i++) {
                if (null != root.getChildren().get(i)) {
                    rec(root.getChildren().get(i), positionedChars);
                }
            }
        }
    }

    //this function adds a new character to the document tree

    public void addCharBetween(List<Identifier> p, List<Identifier> q, Document document, char ch) {
        CRDT crdt = document.getCrdt();
        List<Identifier> newPos = null;
        if (null == p && null == q) {
            //todo: throw exception "insertion problem of char {}".}
        }
        if (isBOF(p) && isEOF(q)) {
            newPos = alloc(p, q, crdt.getStrategy());
        } else if (isBOF(p)) {
            //insert to the beginning of the document
            //todo: handle adding a char in the beginning of file.
            //List<Identifier> p = CRDT.PositionCalculatorUtil.decrementByOne(q);
        } else if (isEOF(q)) {
            //insert to the end of the document
            newPos = CRDT.PositionCalculatorUtil.incrementByOne(p);
        } else {
            //insert between two characters
            newPos = alloc(p, q, crdt.getStrategy());
        }

        addCharToDocTree(crdt, newPos, ch);
        document.incSize();
        docRepository.save(document);
    }

    private boolean isBOF(List<Identifier> p) {
        return p.size() == 1 && p.get(0).getDigit() == CRDT.BOF;
    }

    private boolean isEOF(List<Identifier> q) {
        return q.size() == 1 && q.get(0).getDigit() == CRDT.EOF;
    }

    //This function traverses the doc tree (starts at root) in the newPos values path and inserts a new
    //tree node with the new given char.
    //the function allocates a new depth to the tree if needed.
    //TODO: check that nodes are allocated and if needed allocate new arrays in the relevant size.

    private void addCharToDocTree(CRDT crdt, List<Identifier> newPos, char ch) {
        int depth = 0;   //in case a new depth is allocated.
        TreeNode curNode = crdt.getRoot();
        for (int i = 0; i < newPos.size(); i++) {
            int curDigit = newPos.get(i).getDigit();
            //allocate a new depth (new array of size: 2^(base + depth)
            if (null == curNode.getChildren()) {
                curNode.initializeChildrenList(depth); //set all children null
            }
            //allocate a treenode instead of null
            if (null == curNode.getChildren().get(curDigit)) {
                curNode.getChildren().set(curDigit, TreeNode.createEmptyTreeNode());
            }
            curNode = curNode.getChildren().get(curDigit);
            depth++;
        }

        //set the node's character
        curNode.setChar(PositionedChar.createNewChar(ch, newPos));
    }

    /**
     * Given two characters p and q with consecutive positions in a document, this function allocates a new position between them.
     *
     * @param p        - position
     * @param q        - position
     * @param strategy - a map that keeps the chosen strategies
     * @return A new position (Identifiers list) between p and q
     */

    public List<Identifier> alloc(List<Identifier> p, List<Identifier> q, Map<Integer, Boolean> strategy) {
        int depth = 0, interval = 0, iteration = 0;
        int maxIterations = Math.max(p.size(), q.size());
        boolean isNewDepth = false;

        /**
         (1). Find the number of available spots in the crdt tree to insert a new character.
         calculate interval - the number of available spots between p and q (to insert a new character).
         depth - the depth into which the new character is going to be inserted.
         **/
        while (interval < 1 && iteration <= maxIterations) {
            depth++;
            iteration++;
            interval = calculateInterval(p, q, depth, CRDT.BASE);
        }

        //edge case: allocate identifier in a new depth (in case that interval stays 0)
        if (interval == 0) {
            interval = (int) Math.pow(2, CRDT.BASE + depth - 1) - 1;
            isNewDepth = true;
        }
        //limits the interval
        int step = Math.min(CRDT.BOUNDARY, interval);

        /**
         (2). Allocation strategy: boundary+ or boundary-.
         **/
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (!strategy.containsKey(depth)) {
            boolean rand = random.nextBoolean();
            strategy.put(depth, rand);
        }

        /**
         (3). New identifier construction.
         Get a random value using the step variable calculated earlier, and depends on the chosen strategy,
         adds/subtracts this value from p/q (respectively) at the specific depth.
         **/
        List<Identifier> id;
        if (strategy.get(depth)) {      //boundary+
            int addVal = random.nextInt(0, step) + 1;
            id = addVal(prefix(p, depth), addVal, isNewDepth);
        } else {                        //boundary-
            int subVal = random.nextInt(0, step) + 1;
            id = subVal(prefix(p, depth), prefix(q, depth), subVal, depth, CRDT.BASE);
        }

        return id;
    }

    //given a list of identifiers id and a depth in tree, this function returns the prefix of the id, calculated
    //from tree root up to depth.
    public List<Identifier> prefix(List<Identifier> id, int depth) {
        List<Identifier> idCopy = new ArrayList<>();
        for (int cpt = 0; cpt < depth; cpt++) {
            if (cpt < id.size()) {
                idCopy.add(id.get(cpt));
            } else {
                idCopy.add(new Identifier(0));
            }
        }

        return idCopy;
    }

    //this function calculates the number of possible available places between two identifiers
    //(the delta between two identifiers)
    //performs the calculation: prefix(q, depth) - prefix(p, depth) - 1;
    //in the following way: subtract the last identifier value of q from the last identifier value of p and return an integer result.
    //in case that q has 0 as its last identifier value, q will be transformed to its equivalent value in terms of p in the specific depth.
    //e.g. depth = 2 (base = 2^6),  q = [10,0], p = [9,60]. In this case, q will be transformed to [9, 65] and the subtraction
    //will be performed as described above.
    public int calculateInterval(List<Identifier> p, List<Identifier> q, int depth, int base) {

        //prefix function returns p_prefix and q_prefix in the same length (depth).
        List<Identifier> pPrefix = prefix(p, depth);
        List<Identifier> qPrefix = prefix(q, depth);

//        if (p >= q) {
//            throw exception. illegal identifiers
//        }

        int qLastDig = qPrefix.get(depth - 1).getDigit();
        int pLastDig = pPrefix.get(depth - 1).getDigit();

        if (qLastDig > pLastDig) {
            return qLastDig - pLastDig - 1;
        }

        if (qLastDig < pLastDig) { //e.g [10,0] - [9,60], depth = 2
            List<Identifier> qEquiv = getEquivalentPosition(pPrefix, qPrefix, depth, base);
            return calculateInterval(pPrefix, qEquiv, depth, base);
        } else {
            return 0; //TODO: is it legal qLastDig = pLastDig ?
        }
    }

    //this function calculates the equivalent of position q in terms of position p.
    //p and q in same length.
    //e.g. given q=[10, 0], p=[9,61], depth=2, base=5
    //the function returns [9,64], because in depth 2 the counting goes as following: [9,0],...[9,62],[9,63],[10],[10,0],[10,1]...
    private List<Identifier> getEquivalentPosition(List<Identifier> p, List<Identifier> q, int depth, int base) {
        //transform q to be in terms of p.
//      //the number of children of a treenode in depth i with base x is: 2^(x + i - 1)
        List<Identifier> qEquiv = new ArrayList<>(p);
        qEquiv.set(depth - 1, new Identifier((int) Math.pow(2.0, base + depth - 1)));

        return qEquiv;
    }

    //this function performs: prefix(p, depth) + addVal;
    //todo: move the addVal function to the CalculatorUtility
    List<Identifier> addVal(List<Identifier> pPrefix, int val, boolean isNewDepth) {
        if (isNewDepth) {
            pPrefix.add(new Identifier(0));
        }
        List<Identifier> id = new ArrayList<>(pPrefix);
        id.set(id.size() - 1, new Identifier(id.get(id.size() - 1).getDigit() + val));

        return id;
    }

    //this function performs: prefix(q, depth) - subVal;
    //todo: move the subVal function to the CalculatorUtility
    List<Identifier> subVal(List<Identifier> p, List<Identifier> q, int val, int depth, int base) {
        List<Identifier> id;
        List<Identifier> qEquive;
        //in case that the last value is 0
        if (q.get(q.size() - 1).getDigit() == 0) {
            qEquive = getEquivalentPosition(p, q, depth, base);
            id = new ArrayList<>(qEquive);
        } else {
            id = new ArrayList<>(q);
        }

        id.set(id.size() - 1, new Identifier(id.get(id.size() - 1).getDigit() - val));

        return id;
    }

}