package docSharing.service;

import docSharing.DTO.ReturnDocumentMessage;
import docSharing.entities.Document;
import docSharing.entities.UserRole;
import docSharing.repository.DocRepository;
import docSharing.test.ManipulatedText;
import docSharing.test.OnlineUser;
import docSharing.test.UpdateType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.print.Doc;
import java.util.*;

@Service
public class DocService {
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private PermissionService permissionService;

    static Map<Long, String> docContentByDocId = new HashMap<>();
    static Map<Long, List<String>> viewingUser = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(DocService.class.getName());

    public DocService() {
        logger.info("init Doc Service instance");
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


        docContentByDocId.put(docId, updatedDocText);
    }


//    /**
//     * @param documentId doument id
//     * @return the document content from the repository
//     */
//    public String getDocument(Long documentId) {
//        logger.info("start of getDocument function");
//        boolean isDocument = docRepository.findById(documentId).isPresent();
//
//        if (!isDocument) {
//            logger.error("there is no document with this id");
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
//        }
//
//        Document document = docRepository.findById(documentId).get();
//        String content = document.getContent();
//
//        if (!docContentByDocId.containsKey(documentId)) {
//            docContentByDocId.put(documentId, content);
//        }
//
//        logger.info("the content of the document is " + content);
//        return content;
//
//
//    }
//
//    /**
//     * @param map documents id by content hashMap.
//     */
//    public void saveChangesToDB(Map<Long, String> map) {
//        logger.info("start saveChangesToDB function");
//        for (Map.Entry<Long, String> entry : map.entrySet()) {
//            saveOneDocContentToDB(entry.getKey(), entry.getValue());
//        }
//    }

//    public boolean changeUserRollInDoc(Long docId, Long ownerId, String changeToEmail, UserRole userRole) {
//        logger.info("start changeUserRollInDoc function");
//        //TODO: i want these functions
////        authService.checkIfUser(changeToEmail);
////        authService.idOfUserByEmail(changeToEmail);
//        if (!permissionService.checkIfOwner(ownerId)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you are not the owner");
//        }
//        permissionService.changeUserRollInDoc(docId, 0L, userRole);
//        return true;
//    }

//    public List<String> addUserToViewingUsers(Long docId, String userName) {
//        logger.info("start addUser To ViewingUsers function");
//        if (viewingUser.containsKey(docId)) {
//            viewingUser.get(docId).add(userName);
//        } else {
//            List<String> list = new ArrayList<>();
//            list.add(userName);
//            viewingUser.put(docId, list);
//        }
//        System.out.println(viewingUser.get(docId));
//        return viewingUser.get(docId);
//
//    }


//    /**
//     * @param docId           document id
//     * @param documentContent document new content
//     */
//    public void saveOneDocContentToDB(Long docId, String documentContent) {
//        logger.info("start saveOneDocContentToDB function");
//        boolean isDocument = docRepository.findById(docId).isPresent();
//        if (!isDocument) {
//            logger.error("there is no document with this id");
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no document with this id");
//        }
//        Document doc = docRepository.findById(docId).get();
//        doc.setContent(documentContent);
//        docRepository.save(doc);
//
//    }


}
