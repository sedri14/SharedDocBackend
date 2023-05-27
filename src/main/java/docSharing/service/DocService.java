package docSharing.service;

import docSharing.CRDT.CharItem;
import docSharing.CRDT.Decimal;
import docSharing.CRDT.Identifier;
import docSharing.entities.Document;
import docSharing.exceptions.INodeNotFoundException;
import docSharing.exceptions.IllegalOperationException;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static docSharing.CRDT.CRDTUtils.*;

@Service
public class DocService {

    public static final int MAX_USERS = 5;
    @Autowired
    private DocRepository docRepository;

    static Map<Long, String> docContentByDocId = new HashMap<>();
    static Map<Long, Map<String, Integer>> connectedUsersByDocId = new HashMap<>();
    static Map<Long, List<Integer>> availableSiteIdsByDocId = new HashMap<>();
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

    public List<CharItem> getRawText(List<CharItem> content) {
        return sortByPosition(content);
    }


    public List<String> addUserToDocConnectedUsers(Long docId, String userEmail) {
        logger.info("{} is ADDED to connected users of doc {}", userEmail, docId);
        if (!connectedUsersByDocId.containsKey(docId)) {
            connectedUsersByDocId.put(docId, new HashMap<>());
        }

        Map<String, Integer> connectedUsersMap = connectedUsersByDocId.get(docId);
        if (!connectedUsersMap.containsKey(userEmail)) {
            //Add userEmail key and attach a new unique site id
            int siteId = attachUniqueSiteIdToUser(docId);
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

    private synchronized int attachUniqueSiteIdToUser(Long docId) {
        if (isFirstConnectedUser(docId)) {
            initializeSiteIdPool(docId);
        }
        Random random = new Random();
        List<Integer> siteIdsPool = availableSiteIdsByDocId.get(docId);
        int randIndex = random.nextInt(siteIdsPool.size());

        //swap value of random index with last value
        int lastIndex = (siteIdsPool.size() - 1);
        int temp = siteIdsPool.get(randIndex);
        siteIdsPool.set(randIndex, siteIdsPool.get(lastIndex));
        siteIdsPool.set(lastIndex, temp);

        int siteId = siteIdsPool.remove(lastIndex);

        return siteId;
    }

    private boolean isFirstConnectedUser(Long docId) {
        return connectedUsersByDocId.get(docId).isEmpty();
    }

    private void initializeSiteIdPool(Long docId) {
        availableSiteIdsByDocId.put(docId, new ArrayList<>());
        availableSiteIdsByDocId.get(docId).addAll(IntStream.rangeClosed(1, MAX_USERS)
                .boxed().collect(Collectors.toList()));
    }

    public List<String> removeUserFromDocConnectedUsers(Long docId, String userEmail) {
        logger.info("{} is REMOVED from connected users of doc {}", userEmail, docId);

        if (!connectedUsersByDocId.containsKey(docId)) {
            throw new IllegalOperationException("No one connected to this document");
        }

        Map<String, Integer> connectedUsersMap = connectedUsersByDocId.get(docId);
        if (!connectedUsersMap.containsKey(userEmail)) {
            throw new IllegalOperationException("User was not connected to document");
        }

        int siteId = connectedUsersMap.remove(userEmail);
        returnSiteIdToPool(docId, siteId);
        logger.info("User {} returned site id {}", userEmail, siteId);

        if (connectedUsersMap.isEmpty()) {
            connectedUsersByDocId.remove(docId);
        }
        List<String> connectedUsers = new ArrayList<>(connectedUsersMap.keySet());
        logger.info("{} left - Online users: {}", userEmail, connectedUsers);

        return connectedUsers;
    }

    private synchronized void returnSiteIdToPool(Long docId, int siteId) {
        List<Integer> siteIdsPool = availableSiteIdsByDocId.get(docId);
        siteIdsPool.add(siteId);

        if (siteIdsPool.size() == MAX_USERS) {
            //Nobody is connected to the document
            availableSiteIdsByDocId.remove(docId);
        }
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
            List<Integer> n1 = pos1.size() > 0 ? Decimal.fromIdentifierList(pos1) : new ArrayList<>(List.of(head1.getDigit()));
            List<Integer> n2 = pos2.size() > 0 ? Decimal.fromIdentifierList(pos2) : new ArrayList<>(List.of(2, 5, 6));
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
        CharItem newChar = CharItem.NewPositionedChar(newPos, ch);
        document.getContent().add(newChar);
    }

    public int getSiteId(Long docId, String email) {
        return connectedUsersByDocId.get(docId).get(email);
    }
}