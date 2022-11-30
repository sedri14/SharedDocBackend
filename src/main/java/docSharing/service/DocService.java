package docSharing.service;

import docSharing.DTO.ReturnDocumentMessage;
import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
import docSharing.repository.DocRepository;
import docSharing.test.ManipulatedText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DocService {
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    static Map<Long, String> docContentByDocId = new HashMap<>();
    static Map<Long, List<String>> viewingUser = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(DocService.class.getName());

    public DocService() {
        logger.info("init Doc Service instance");
        logger.info("get the document from the DB");

        Runnable saveContentToDBRunnable = new Runnable() {
            public void run() {
                saveAllChangesToDB(docContentByDocId);
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(saveContentToDBRunnable, 0, 3, TimeUnit.SECONDS);

    }


    /**
     * @param docId           document id
     * @param manipulatedText the updated text
     * @return updated document
     */
    public ReturnDocumentMessage sendUpdatedText(Long docId, ManipulatedText manipulatedText) {
        logger.info("start sendUpdatedText function");
        logger.info("the client sent" + manipulatedText);
        switch (manipulatedText.getType()) {
            case APPEND:
                addTextToDoc(docId, manipulatedText);
                break;
            case DELETE:
                deleteTextFromDoc(docId, manipulatedText);
                break;
            case DELETE_RANGE:
                deleteRangeTextFromDoc(docId, manipulatedText);
                break;
            case APPEND_RANGE:
                addRangeTextToDoc(docId, manipulatedText);
                break;
        }
        ReturnDocumentMessage returnDocumentMessage = new ReturnDocumentMessage(manipulatedText.getUser(), docContentByDocId.get(docId), manipulatedText.getStartPosition(), manipulatedText.getEndPosition(), manipulatedText.getType());
        logService.addToLog(docId, manipulatedText);
        logger.info("all subscribed users gets" + returnDocumentMessage);

        return returnDocumentMessage;

    }

    /**
     * @param docId
     * @param text
     */
    private static void addTextToDoc(Long docId, ManipulatedText text) {
        logger.info("start addTextToDoc function");
        String docText = docContentByDocId.get(docId);
        logger.info("doc id is" + docId);
        logger.info("ManipulatedText " + text);
        String updatedDocText = docText.substring(0, text.getStartPosition()) + text.getContent() + docText.substring(text.getStartPosition());
        docContentByDocId.put(docId, updatedDocText);

    }

    /**
     * @param docId
     * @param text
     */
    private static void deleteTextFromDoc(Long docId, ManipulatedText text) {
        logger.info("start deleteTextFromDoc");
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition()) + docText.substring(text.getStartPosition() + 1);
        //get the deleted char from the content and set it instead of null//
        String deletedChar = docText.substring(text.getStartPosition(), text.getStartPosition() + 1);
        logger.info("i deleted " + deletedChar);
        text.setContent(deletedChar);
        docContentByDocId.put(docId, updatedDocText);
    }

    /**
     * @param docId
     * @param text
     */
    private static void addRangeTextToDoc(Long docId, ManipulatedText text) {
        logger.info("start addRangeTextToDoc");
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + text.getContent() + docText.substring(text.getEndPosition() + 1);


        docContentByDocId.put(docId, updatedDocText);
    }

    /**
     * @param docId document id
     * @param text  the chagne
     */
    private static void deleteRangeTextFromDoc(Long docId, ManipulatedText text) {
        logger.info("start deleteRangeTextFromDoc function");
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + docText.substring(text.getEndPosition() + 1);
        String deletedChars = docText.substring(text.getStartPosition() + 1, text.getEndPosition() + 1);
        logger.info("i deleted " + deletedChars);
        text.setContent(deletedChars);

        docContentByDocId.put(docId, updatedDocText);
    }


    /**
     * @param map documents id by content hashMap.
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
        boolean isDocument = docRepository.findById(docId).isPresent();
        if (!isDocument) {
            logger.error("there is no document with this id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
        }
        Document doc = docRepository.findById(docId).get();
        doc.setContent(documentContent);
        docRepository.save(doc);
        logger.info("document is saved");

    }

    /**
     * @param documentId doument id
     * @return the document content from the repository
     */
    public Document getDocument(Long documentId) {
        logger.info("start of getDocument function");
        boolean isDocument = docRepository.findById(documentId).isPresent();

        if (!isDocument) {
            logger.error("there is no document with this id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
        }

        Document document = docRepository.findById(documentId).get();
        String content = document.getContent();

        if (!docContentByDocId.containsKey(documentId)) {
            docContentByDocId.put(documentId, content);
        }
        logger.info("the content in the hashmap is" + docContentByDocId.get(documentId));

        logger.info("the content of the document is " + content);
        return document;


    }

    public List<String> addUserToViewingUsers(Long docId, String userName) {
        logger.info("start addUser To ViewingUsers function");
        if (viewingUser.containsKey(docId)) {
            viewingUser.get(docId).add(userName);
        } else {
            List<String> list = new ArrayList<>();
            list.add(userName);
            viewingUser.put(docId, list);
        }
        logger.info("all viewing users are");
        return viewingUser.get(docId);

    }

    public List<String> removeUserFromViewingUsers(Long docId, String userName) {
        logger.info("start removeUserFromViewingUsers function");
        if (viewingUser.containsKey(docId)) {
            System.out.println(userName);
            viewingUser.get(docId).remove(userName);

        }
        logger.info("all viewing users are");
        return viewingUser.get(docId);

    }


    public boolean editRole(Long docId, Long ownerId, String changeToEmail, UserRole userRole, boolean isDelete) {
        logger.info("start editRole function");
        if (getOwner(docId) != ownerId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you are not the owner");
        }

        Document doc = docRepository.findById(docId).get();
        User user = userService.findByEmail(changeToEmail);

        if (isDelete && permissionService.isExist(doc, user)) {
            permissionService.delete(doc, user);
        } else {
            if (permissionService.isExist(doc, user)) {
                permissionService.updatePermission(doc, user, userRole);
            } else {
                permissionService.addPermission(doc, user, userRole);
            }
        }

        return true;
    }


    public Permission setPermission(Long userId, Long docId, UserRole userRole) {
        User user = userService.getById(userId);
        Document doc = docRepository.findById(docId).get();

        Permission p = null;
        switch (userRole) {
            case EDITOR:
                p = Permission.newEditorPermission(user, doc);
                break;
            case VIEWER:
                p = Permission.newViewerPermission(user, doc);
                break;
            default:
                throw new RuntimeException("Role not supported.");
        }
        permissionService.setPermission(p);

        return p;
    }

    public Optional<Permission> getPermission(Long userId, Long docId) {
        User user = userService.getById(userId);
        Document doc = docRepository.findById(docId).get();

        return permissionService.getPermission(user, doc);
    }

    public Long getOwner(Long docId) {

        boolean isDocument = docRepository.findById(docId).isPresent();
        if (!isDocument) {
            logger.error("there is no document with this id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
        }
        Document doc = docRepository.findById(docId).get();
        return doc.getOwner().getId();

    }

}