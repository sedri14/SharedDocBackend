package docSharing.service;

import docSharing.CRDT.Char;
import docSharing.CRDT.Identifier;
import docSharing.CRDT.CRDT;
import docSharing.CRDT.TreeNode;
import docSharing.DTO.Doc.UpdateDocContentRes;
import docSharing.entities.Document;
import docSharing.repository.DocRepository;
import docSharing.DTO.Doc.ManipulatedTextDTO;
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
    static Map<Long, List<String>> viewingUsersByDocId = new HashMap<>();
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

    /**
     * @param docId              document id
     * @param manipulatedTextDTO the updated text object
     * @return updated document object
     */
//    public UpdateDocContentRes UpdateDocContent(Long docId, ManipulatedTextDTO manipulatedTextDTO) {
//
//        logger.info("start sendUpdatedText function");
//
//        if (!docContentByDocId.containsKey(docId)) {
//            logger.info("you should get the document first");
//            throw new RuntimeException("you should get the document first");
//        }
//
//        logger.info("the client want to update" + manipulatedTextDTO);
//
//        switch (manipulatedTextDTO.getAction()) {
//            case APPEND:
//                addTextToDoc(docId, manipulatedTextDTO);
//                break;
//            case DELETE:
//                deleteTextFromDoc(docId, manipulatedTextDTO);
//                break;
//            case DELETE_RANGE:
//                deleteRangeTextFromDoc(docId, manipulatedTextDTO);
//                break;
//            case APPEND_RANGE:
//                addRangeTextToDoc(docId, manipulatedTextDTO);
//                break;
//        }
//        UpdateDocContentRes updateDocContentRes = new UpdateDocContentRes(
//                manipulatedTextDTO.getUserId()
//                , docContentByDocId.get(docId)
//                , manipulatedTextDTO.getStartPosition()
//                , manipulatedTextDTO.getEndPosition()
//                , manipulatedTextDTO.getAction());
//
//        logger.info("all subscribed users gets" + updateDocContentRes);
//
//        return updateDocContentRes;
//
//    }
//
//
//    /**
//     * @param docId document id
//     * @param text  the updated text object
//     */
//    private static void addTextToDoc(Long docId, ManipulatedTextDTO text) {
//
//        logger.info("start addTextToDoc function");
//        String docText = docContentByDocId.get(docId);
//        String updatedDocText = docText.substring(0, text.getStartPosition()) + text.getContent() + docText.substring(text.getStartPosition());
//
//        docContentByDocId.put(docId, updatedDocText);
//
//    }
//
//
//    /**
//     * @param docId document id
//     * @param text  the updated text object
//     */
//    private static void deleteTextFromDoc(Long docId, ManipulatedTextDTO text) {
//
//        logger.info("start deleteTextFromDoc");
//
//        String docText = docContentByDocId.get(docId);
//        String updatedDocText = docText.substring(0, text.getStartPosition()) + docText.substring(text.getStartPosition() + 1);
//        String deletedChar = docText.substring(text.getStartPosition(), text.getStartPosition() + 1);
//
//        text.setContent(deletedChar);
//
//        docContentByDocId.put(docId, updatedDocText);
//    }
//
//
//    /**
//     * @param docId document id
//     * @param text  the updated text object
//     */
//    private static void addRangeTextToDoc(Long docId, ManipulatedTextDTO text) {
//
//        logger.info("start addRangeTextToDoc");
//        String docText = docContentByDocId.get(docId);
//        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + text.getContent() + docText.substring(text.getEndPosition() + 1);
//
//
//        docContentByDocId.put(docId, updatedDocText);
//    }
//
//
//    /**
//     * @param docId document id
//     * @param text  the updated text object
//     */
//    private static void deleteRangeTextFromDoc(Long docId, ManipulatedTextDTO text) {
//
//        logger.info("start deleteRangeTextFromDoc function");
//
//        String docText = docContentByDocId.get(docId);
//        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + docText.substring(text.getEndPosition() + 1);
//        String deletedChars = docText.substring(text.getStartPosition() + 1, text.getEndPosition() + 1);
//
//        text.setContent(deletedChars);
//
//        docContentByDocId.put(docId, updatedDocText);
//    }


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
        doc.setContent(documentContent);

        docRepository.save(doc);
        logger.info("document is saved");

    }


    /**
     * @param documentId document id
     * @return the document content from the repository
     */
    public Document getDocument(Long documentId) {

        logger.info("start of getDocument function");
        boolean docIsPresent = docRepository.findById(documentId).isPresent();

        if (!docIsPresent) {
            logger.error("there is no document with this id");
            throw new IllegalArgumentException("there is no document with this id");
        }

        Document doc = docRepository.findById(documentId).get();
        String content = doc.getContent();

        if (!docContentByDocId.containsKey(documentId)) {
            docContentByDocId.put(documentId, content);
        }

        logger.info("the content in the hashmap is" + docContentByDocId.get(documentId));

        return doc;
    }


    /**
     * @param docId    document id
     * @param userName userName of the user who start viewing the document
     * @return all the current viewing users name
     */
    public List<String> addUserToViewingUsers(Long docId, String userName) {

        logger.info("start addUser To ViewingUsers function");

        if (viewingUsersByDocId.containsKey(docId)) {
            viewingUsersByDocId.get(docId).add(userName);
        } else {
            List<String> list = new ArrayList<>();
            list.add(userName);
            viewingUsersByDocId.put(docId, list);
        }

        logger.info("all viewing users are " + viewingUsersByDocId.get(docId));

        return viewingUsersByDocId.get(docId);

    }


    /**
     * @param docId    document id
     * @param userName userName of the user who stopped viewing the document
     * @return all the current viewing users name
     */
    public List<String> removeUserFromViewingUsers(Long docId, String userName) {

        logger.info("start removeUserFromViewingUsers function");

        if (viewingUsersByDocId.containsKey(docId)) {
            viewingUsersByDocId.get(docId).remove(userName);
        }

        logger.info("all current viewing users are");

        return viewingUsersByDocId.get(docId);

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


    /**
     * @param docId document id
     * @return list of usersName that are currently viewing the document
     */
    public List<String> getCurrentViewingUserList(Long docId) {
        return viewingUsersByDocId.get(docId);
    }

//    int comparePosition(List<Identifier> p1, List<Identifier> p2) {
//
//        for (int i = 0; i < Math.min(p1.size(), p2.size()); i++) {
//            int comp = Identifier.compare(p1.get(i), p2.get(i));
//            if (comp != 0) {
//                return comp;
//            }
//
//            if (p1.size() < p2.size()) {
//                return -1;
//            } else if (p1.size() > p2.size()) {
//                return 1;
//            } else {
//                return 0;   //program should never reach this line of code.
//            }
//        }
//
//        return 0; //TODO: delete this.
//    }

//    List<Identifier> generatePositionBetween(List<Identifier> beforePos, List<Identifier> afterPos, int site) {
//        Identifier head1 = beforePos.get(0); //TODO: set default value in case first element does not exist.
//        Identifier head2 = afterPos.get(0);
//
//        if (head1.getDigit() != head2.getDigit()) {
//            //Case 1: head digits are different
//            //convert positions to decimal
//            List<Integer> dec1 = Decimal.fromPosition(beforePos);
//            List<Integer> dec2 = Decimal.fromPosition(afterPos);
//            List<Integer> delta = Decimal.subtractGreaterThan(dec2, dec1);
//
//            //increment dec1 by some amount less than delta
//            int increase = Decimal.increment(dec1, delta);
//            return Decimal.toPosition(increase, beforePos, afterPos, site);
//
//        } else {
//            if (head1.getSite() < head2.getSite()) {
//                //Case 2: head digits are the same, sites are different
//                return CRDTUtils.concatLists(head1, generatePositionBetween(beforePos.subList(1, beforePos.size()), new ArrayList<>(), site));
//            } else if (head1.getSite() == head2.getSite()) {
//                //Case 3: head digits are the same, sites are the same
//                return CRDTUtils.concatLists(head1, generatePositionBetween(beforePos.subList(1, beforePos.size()), afterPos.subList(1, afterPos.size()), site));
//            } else {
//                throw new RuntimeException("invalid site ordering");
//            }
//        }
//    }

    //this function adds a new character to the document tree
    public void addCharBetween(List<Identifier> p, List<Identifier> q, CRDT crdt, char ch) {
        List<Identifier> newPos = alloc(p, q, crdt.getStrategy());
        addCharToDocTree(crdt, newPos, ch);
    }

    //this function traverse the doc tree (starts at root) in the newPos path and inserts a new
    //tree node with the new given char.
    //the function allocates a new depth to the thee if needed.
    //TODO: check that nodes are allocated and if needed allocate new arrays in the relevant size.

    private void addCharToDocTree(CRDT crdt, List<Identifier> newPos, char ch) {
        int depth = 0;   //need this in case a new depth is allocated.
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
        curNode.setChar(Char.createNewChar(ch, newPos));
    }

    //convert the crdt doc tree to a simple string, using pre-order traversal algorithm.
    public String preorderTraversal(CRDT crdt) {
        StringBuilder sb = new StringBuilder();
        rec(crdt.getRoot(), sb);

        return String.valueOf(sb);
    }

    public void rec(TreeNode root, StringBuilder sb) {
        if (null == root) {
            return;
        }

        sb.append(root.getChar().getValue());
        if (null != root.getChildren()) {
            for (int i = 0; i < root.getChildren().size(); i++) {
                if (null != root.getChildren().get(i)) {
                    rec(root.getChildren().get(i), sb);
                }
            }
        }
    }

    //given two characters p and q with consecutive positions in a document, this function allocates a new position between them.
    public List<Identifier> alloc(List<Identifier> p, List<Identifier> q, Map<Integer, Boolean> strategy) {
        int depth = 0;
        int interval = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        /**
         (1). Find the number of available spots in the crdt tree to insert a new character.
         calculating interval - the number of available spots to insert a new character.
         calculate depth - the depth into which the new character is going to be inserted.
         **/
        while (interval < 1) {
            depth++;
            interval = calculateInterval(p, q, depth, CRDT.BASE);
        }
        //in case that the interval is smaller than the set boundary, limit the available spots.
        int step = Math.min(CRDT.BOUNDARY, interval);

        if (!strategy.containsKey(depth)) {
            boolean rand = random.nextBoolean();
            strategy.put(depth, rand);
        }

        List<Identifier> id;
        if (strategy.get(depth)) {      //boundary+
            int addVal = random.nextInt(0, step) + 1;
            id = addVal(prefix(p, depth), addVal);
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
    List<Identifier> addVal(List<Identifier> pPrefix, int val) {
        List<Identifier> id = new ArrayList<>(pPrefix);
        id.set(id.size() - 1, new Identifier(id.get(id.size() - 1).getDigit() + val));

        return id;
    }

    //this function performs: prefix(q, depth) - subVal;
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