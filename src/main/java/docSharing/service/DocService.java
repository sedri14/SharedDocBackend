package docSharing.service;

import docSharing.DTO.Doc.UpdateDocContentRes;
import docSharing.Utils.Validation;
import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
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
    static Map<Long, List<String>> viewingUsersByDocId = new HashMap<>();
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
        executor.scheduleAtFixedRate(saveContentToDBRunnable, 0, 5, TimeUnit.SECONDS);

    }


    /**
     * @param docId              document id
     * @param manipulatedTextDTO the updated text object
     * @return updated document object
     */
    public UpdateDocContentRes UpdateDocContent(Long docId, ManipulatedTextDTO manipulatedTextDTO) {

        logger.info("start sendUpdatedText function");

        if (!docContentByDocId.containsKey(docId)) {
            logger.info("you should get the document first");
            throw new RuntimeException("you should get the document first");
        }

        logger.info("the client want to update" + manipulatedTextDTO);

        switch (manipulatedTextDTO.getAction()) {
            case APPEND:
                addTextToDoc(docId, manipulatedTextDTO);
                break;
            case DELETE:
                deleteTextFromDoc(docId, manipulatedTextDTO);
                break;
            case DELETE_RANGE:
                deleteRangeTextFromDoc(docId, manipulatedTextDTO);
                break;
            case APPEND_RANGE:
                addRangeTextToDoc(docId, manipulatedTextDTO);
                break;
        }
        UpdateDocContentRes updateDocContentRes = new UpdateDocContentRes(
                manipulatedTextDTO.getUserId()
                , docContentByDocId.get(docId)
                , manipulatedTextDTO.getStartPosition()
                , manipulatedTextDTO.getEndPosition()
                , manipulatedTextDTO.getAction());

        logService.addToLog(docId, manipulatedTextDTO);

        logger.info("all subscribed users gets" + updateDocContentRes);

        return updateDocContentRes;

    }


    /**
     * @param docId document id
     * @param text  the updated text object
     */
    private static void addTextToDoc(Long docId, ManipulatedTextDTO text) {

        logger.info("start addTextToDoc function");
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition()) + text.getContent() + docText.substring(text.getStartPosition());

        docContentByDocId.put(docId, updatedDocText);

    }


    /**
     * @param docId document id
     * @param text  the updated text object
     */
    private static void deleteTextFromDoc(Long docId, ManipulatedTextDTO text) {

        logger.info("start deleteTextFromDoc");

        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition()) + docText.substring(text.getStartPosition() + 1);
        String deletedChar = docText.substring(text.getStartPosition(), text.getStartPosition() + 1);

        text.setContent(deletedChar);

        docContentByDocId.put(docId, updatedDocText);
    }


    /**
     * @param docId document id
     * @param text  the updated text object
     */
    private static void addRangeTextToDoc(Long docId, ManipulatedTextDTO text) {

        logger.info("start addRangeTextToDoc");
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + text.getContent() + docText.substring(text.getEndPosition() + 1);


        docContentByDocId.put(docId, updatedDocText);
    }


    /**
     * @param docId document id
     * @param text  the updated text object
     */
    private static void deleteRangeTextFromDoc(Long docId, ManipulatedTextDTO text) {

        logger.info("start deleteRangeTextFromDoc function");

        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + docText.substring(text.getEndPosition() + 1);
        String deletedChars = docText.substring(text.getStartPosition() + 1, text.getEndPosition() + 1);

        text.setContent(deletedChars);

        docContentByDocId.put(docId, updatedDocText);
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
     * @param docId           document id
     * @param ownerId         owner id of the document
     * @param editRoleToEmail email to the user who we want to change its role in the document
     * @param userRole        the userRole
     * @param isDelete        true if we want to delete the permission to that user
     * @return true if everything is done
     */
    public UserRole editRole(Long docId, Long ownerId, String editRoleToEmail, UserRole userRole, boolean isDelete) {

        logger.info("start editRole function");

        if (!Objects.equals(getOwner(docId), ownerId)) {
            throw new IllegalArgumentException("you are not the owner");
        }

        boolean docIsPresent = docRepository.findById(docId).isPresent();
        if (!docIsPresent) {
            logger.error("there is no document with this id");
            throw new IllegalArgumentException("there is no document with this id");
        }
        Document doc = docRepository.findById(docId).get();
        User user = userService.findByEmail(editRoleToEmail);

        if (user == null) {
            throw new IllegalArgumentException("user does not exist");
        }
        Permission modified;
        if (isDelete && permissionService.isExist(doc, user)) {
            modified = permissionService.delete(doc, user);
            return UserRole.NON;
        } else {
            if (permissionService.isExist(doc, user)) {
                modified = permissionService.updatePermission(doc, user, userRole);
            } else {
                modified = permissionService.addPermission(doc, user, userRole);
            }
        }

        return modified.getUserRole();
    }


    /**
     * @param userId   userId
     * @param docId    docId
     * @param userRole userRole
     */
    public void setPermission(Long userId, Long docId, UserRole userRole) {

        logger.info("start setPermission function");

        Validation.nullCheck(userId);
        Validation.nullCheck(docId);
        Validation.nullCheck(userRole);

        User user = userService.getById(userId);

        boolean docIsPresent = docRepository.findById(docId).isPresent();

        if (!docIsPresent) {
            logger.error("there is no document with this id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
        }

        Document doc = docRepository.findById(docId).get();

        Permission p;
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

    }


    /**
     * @param userId userId
     * @param docId  DocId
     * @return optional of the permission of that user for that docId
     */
    public Optional<Permission> getPermission(Long userId, Long docId) {

        logger.info("start getPermission function");

        User user = userService.getById(userId);

        boolean docIsPresent = docRepository.findById(docId).isPresent();
        if (!docIsPresent) {
            logger.error("there is no document with this id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
        }
        Document doc = docRepository.findById(docId).get();

        return permissionService.getPermission(user, doc);
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

}