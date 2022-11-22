package docSharing.service;

import docSharing.DTO.ReturnDocumentMessage;
import docSharing.entities.Document;
import docSharing.entities.UserRole;
import docSharing.repository.DocRepository;
import docSharing.test.ManipulatedText;
import docSharing.test.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocService {
    //we need a table of user permissions for the documents contains
    /*
     * DocumentId
     * UserId
     * access permission
     * */

    /*we need another table for document permission
     * DocumentId
     * Visibility permission "public, private"
     * ownerId
     * */
    @Autowired
    private DocRepository docRepository;

    //init document content hashMap()
    //init document viewing user hashMap().
    static Map<Long, String> docContentByDocId = new HashMap<>();
    static Map<Long, List<String>> viewingUser = new HashMap<>();

//    private TimerTask updateAllActiveDocsToDB() {
//    System.out.println("iam here");
////        for (Map.Entry<Long, String> document : docContentByDocId.entrySet()) {
////            Long key = document.getKey();
////            String content = document.getValue();
////            saveContentToDB(key, content);
////        }
//        return null;
//    }


    public DocService() {
        docContentByDocId.put(6L, "");
//        Timer timer = new Timer();
//        timer.schedule(updateAllActiveDocsToDB(), 0, 5000);

    }

    //buffer.
    public static boolean checkIfUserHasAccesToDoc(int DocumentId, int UserId) {
        //search the document from the repo
        //check if the file is public // if yes-> return the true // else No -> check if the user can access it if yes return true. else return false.
        return true;
    }

    public static Document getDocument(int DocumentId, int UserId) {
        //search the document from the repo
        //return the document, add it to the document content if not exists there.
        return null;
    }

    public static boolean insertChangesToDoc() {
        return true;
    }

    public static List<String> getViewingUsers() {
        //return viewingUser list
        return null;
    }

    public static void insertViewingUser(String userName) {
        //insert to the viewingUsers list.
    }

    public static void changeUserRollInDoc(String ownerUser, String user, UserRole userRole) {

        //check if user is the owner of the document
        // update permission table for the user

        //refresh the page->

    }

    public static List<Integer> getEditingUsersId(int DocumentId) {
        //go over permission table and group document id, select readWrite permission -> return userIds.

        return null;
    }


    public static ReturnDocumentMessage sendUpdatedText(Long docId, ManipulatedText text) {
        System.out.println("the client sent" + text);
        if (text.getType() == UpdateType.APPEND) {
            addTextToDoc(docId, text);
        } else if (text.getType() == UpdateType.DELETE) {
            deleteTextFromDoc(docId, text);
        } else if (text.getType() == UpdateType.DELETE_RANGE) {
            deleteRangeTextFromDoc(docId, text);
        } else if (text.getType() == UpdateType.APPEND_RANGE) {
            addRangeTextToDoc(docId, text);
        }
        return new ReturnDocumentMessage(text.getUser(), docContentByDocId.get(docId), text.getStartPosition(), text.getEndPosition(), text.getType());

    }


//    public static ReturnDocumentMessage sendUpdatedText(Long docId, ManipulatedText text) {
//        System.out.println(text);
//
//        if (text.getType() == UpdateType.APPEND) { //DONE
//            addTextToDoc(docId, text);
//        } else if (text.getType() == UpdateType.APPEND_RANGE) { //Not Working
//            addRangeTextToDoc(docId, text);
//        } else if (text.getType() == UpdateType.DELETE) { //DONE
//            if (docContentByDocId.get(docId).length() == 0) {
//                return new ReturnDocumentMessage(text.getUser(), "");
//            }
//            deleteTextFromDoc(docId, text);
//        } else if (text.getType() == UpdateType.DELETE_RANGE) {//DONE
//            if (docContentByDocId.get(docId).length() == 0) {
//                return new ReturnDocumentMessage(text.getUser(), "");
//            }
//            deleteRangeTextFromDoc(docId, text);
//        }
//        System.out.println(docContentByDocId.get(docId));
//
//        return new ReturnDocumentMessage(text.getUser(), docContentByDocId.get(docId));
//    }

    private static void addTextToDoc(Long docId, ManipulatedText text) {
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition()) + text.getContent() + docText.substring(text.getStartPosition());
        docContentByDocId.put(docId, updatedDocText);

    }

    private static void deleteTextFromDoc(Long docId, ManipulatedText text) {
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition()) + docText.substring(text.getStartPosition() + 1);
        System.out.println(updatedDocText);
        docContentByDocId.put(docId, updatedDocText);
    }

    private static void addRangeTextToDoc(Long docId, ManipulatedText text) {
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + text.getContent() + docText.substring(text.getEndPosition() + 1);

        System.out.println(updatedDocText);

        docContentByDocId.put(docId, updatedDocText);
    }

    private static void deleteRangeTextFromDoc(Long docId, ManipulatedText text) {
        String docText = docContentByDocId.get(docId);
        String updatedDocText = docText.substring(0, text.getStartPosition() + 1) + docText.substring(text.getEndPosition() + 1);

        System.out.println(updatedDocText);

        docContentByDocId.put(docId, updatedDocText);
    }

    public void saveContentToDB(Long docId, String documentContent) {
        Document doc = docRepository.findById(docId).get();
        doc.setContent(documentContent);
        docRepository.save(doc);
    }

    private static void updateDocContentByDocId(Long docId, String documentContent) {
        docContentByDocId.put(docId, documentContent);
    }
}
