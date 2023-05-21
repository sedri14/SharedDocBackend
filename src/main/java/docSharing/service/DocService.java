package docSharing.service;

import docSharing.CRDT.Char;
import docSharing.CRDT.Decimal;
import docSharing.CRDT.Identifier;
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
import java.util.concurrent.TimeUnit;

import static docSharing.CRDT.CRDTUtils.*;

@Service
public class DocService {

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

    public List<Char> getRawText(List<Char> content) {
        return sortByPosition(content);
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
    List<Identifier> alloc(List<Identifier> pos1, List<Identifier> pos2, int siteId) {
        Identifier head1 = head(pos1) != null ? head(pos1) : Identifier.create(0, siteId);
        Identifier head2 = head(pos2) != null ? head(pos2) : Identifier.create(Decimal.BASE, siteId);

        if (head1.getDigit() != head2.getDigit()) {
            //Case 1: Head digits are different
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
        Char newChar = Char.NewPositionedChar(newPos, ch);
        document.getContent().add(newChar);
    }
}