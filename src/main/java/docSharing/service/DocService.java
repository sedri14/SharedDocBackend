package docSharing.service;

import docSharing.CRDT.CharItem;
import docSharing.CRDT.Decimal;
import docSharing.CRDT.Identifier;
import docSharing.entities.Document;
import docSharing.entities.User;
import docSharing.exceptions.INodeNotFoundException;
import docSharing.exceptions.IllegalOperationException;
import docSharing.repository.DocRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static docSharing.CRDT.CRDTUtils.*;

@Service
public class DocService {

    public static final int MAX_USERS = 5;
    @Autowired
    private DocRepository docRepository;

    static Map<Long, Document> cachedDocs = new HashMap<>();
    static Map<Long, Map<String, Integer>> connectedUsersByDocId = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(DocService.class.getName());

    public DocService() {
        logger.info("init Doc Service instance");

        Runnable autoSaveTask = () -> {
            //logger.info("start saveChangesToDB function");
            for (Map.Entry<Long, Document> entry : cachedDocs.entrySet()) {
                logger.info("writing to db size: {}", entry.getValue().getContent().size());
                //find, set, save.
                Document doc = fetchDocumentById(entry.getKey());
                doc.setContent(entry.getValue().getContent());
                docRepository.save(doc);
            }
        };

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleAtFixedRate(autoSaveTask, 0, 5, TimeUnit.SECONDS);
    }

    public Document fetchDocumentById(Long id) {
        return docRepository.findById(id).orElseThrow(() -> new INodeNotFoundException("Document not found with id " + id));
    }

    public List<CharItem> getRawText(List<CharItem> content) {
        return sortByPosition(content);
    }


    public synchronized List<String> addUserToDocConnectedUsers(Long docId, User user, String userEmail) {
        logger.info("{} is ADDED to connected users of doc {}", userEmail, docId);
        if (!connectedUsersByDocId.containsKey(docId)) {
            connectedUsersByDocId.put(docId, new HashMap<>());
        }

        Map<String, Integer> connectedUsersMap = connectedUsersByDocId.get(docId);
        if (!connectedUsersMap.containsKey(userEmail)) {
            //Add userEmail key and site id
            int siteId = user.getSiteId();
            connectedUsersMap.put(userEmail, siteId);
            logger.info("User {} site id is {}", userEmail, siteId);
        } else {
            //User already connected and have a unique site id
            throw new IllegalOperationException(String.format("User %s already connected", userEmail));
        }
        List<String> connectedUsers = new ArrayList<>(connectedUsersMap.keySet());
        logger.info("{} joined - Online users: {}", userEmail, connectedUsers);

        return connectedUsers;
    }

    public synchronized List<String> removeUserFromDocConnectedUsers(Long docId, String userEmail) {
        logger.info("{} is REMOVED from connected users of doc {}", userEmail, docId);

        if (!connectedUsersByDocId.containsKey(docId)) {
            throw new IllegalOperationException("No one connected to this document");
        }

        Map<String, Integer> connectedUsersMap = connectedUsersByDocId.get(docId);
        if (!connectedUsersMap.containsKey(userEmail)) {
            throw new IllegalOperationException("User was not connected to document");
        }

        connectedUsersMap.remove(userEmail);

        if (connectedUsersMap.isEmpty()) {
            connectedUsersByDocId.remove(docId);
            flushAndRemoveDocFromCache(docId);   //doc is not cached anymore because no one is connected to it.
        }
        List<String> connectedUsers = new ArrayList<>(connectedUsersMap.keySet());
        logger.info("{} left - Online users: {}", userEmail, connectedUsers);

        return connectedUsers;
    }

    private synchronized void flushAndRemoveDocFromCache(Long docId) {
        logger.info(">>>flush docs to db and remove from cache<<<");
        logger.info("doc content about to be saved: size: {}", cachedDocs.get(docId).getContent().size());
        Document doc = fetchDocumentById(docId);
        doc.setContent(cachedDocs.get(docId).getContent());
        docRepository.save(doc);
        cachedDocs.remove(docId);
        logger.info("after remove, cachedDocs size: {}", cachedDocs.size());
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

    List<Identifier> alloc(List<Identifier> pos1, List<Identifier> pos2, int siteId) {
        Identifier head1 = head(pos1) != null ? head(pos1) : Identifier.create(0, siteId);
        Identifier head2 = head(pos2) != null ? head(pos2) : Identifier.create(Decimal.BASE, siteId);

        if (head1.getDigit() != head2.getDigit()) {
            //Case 1: Head digits are different
            if (pos1.isEmpty() && pos2.isEmpty()) {
                return Decimal.toIdentifierList(List.of(0, 1), pos1, pos2, siteId);

            }
            List<Integer> n1 = Decimal.fromIdentifierList(pos1);
            List<Integer> n2 = Decimal.fromIdentifierList(pos2);
            List<Integer> delta = Decimal.substractGreaterThan(n2, n1);
            List<Integer> next = Decimal.increment(n1, delta);

            return Decimal.toIdentifierList(next, pos1, pos2, siteId);
        } else {
            if (head1.getSiteId() < head2.getSiteId()) {
                //Case 2: Head digits are the same, sites are different
                return cons(head1, alloc(rest(pos1), new ArrayList<>(), siteId));
            } else if (head1.getSiteId() == head2.getSiteId()) {
                //Case 3: Head digits and sites are the same
                return cons(head1, alloc(rest(pos1), rest(pos2), siteId));
            } else {
                throw new RuntimeException("invalid site ordering");
            }
        }
    }

    public void addCharBetween(List<Identifier> pos1, List<Identifier> pos2, Document document, char ch, int siteId) {
        List<Identifier> newPos = alloc(pos1, pos2, siteId);
        //check for a white space char
        if (ch == ' ') {
            ch = 0x00; // ascii value of space
        }
        CharItem newChar = CharItem.NewPositionedChar(newPos, ch);
        document.getContent().add(newChar);
    }

    public int getSiteId(Long docId, String email) {
        return connectedUsersByDocId.get(docId).get(email);
    }

    public Document getCachedDocument(Long docId) {
        Document doc = cachedDocs.get(docId);
        logger.info(null == doc ? " getting doc from db" : "getting doc from cache");
        if (null == doc) {
            doc = fetchDocumentById(docId);
            logger.info("after add to cachedDocs, cachedDocs size: {}", cachedDocs.size());
            cachedDocs.put(docId, doc);
        }

        return doc;
    }
}