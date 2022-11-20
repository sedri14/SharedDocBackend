package docSharing.service;

import docSharing.DTO.ReturnDocumentMessage;
import docSharing.entities.Document;
import docSharing.entities.UserRole;
import docSharing.test.ManipulatedText;
import docSharing.test.UpdateType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static String res = "";
    //init document content hashMap()
    //init document viewing user hashMap().
    static Map<Integer, String> documentContent = new HashMap<>();
    static Map<Integer, List<String>> viewingUser = new HashMap<>();

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

    public static void changeUserRollInDoc( String ownerUser, String user, UserRole userRole) {

        //check if user is the owner of the document
        // update permission table for the user

        //refresh the page->

    }

    public static List<Integer> getEditingUsersId(int DocumentId) {
        //go over permission table and group document id, select readWrite permission -> return userIds.

        return null;
    }


    public static ReturnDocumentMessage sendUpdatedText(ManipulatedText text) {
        System.out.println(text);
        if (text.getType() == UpdateType.APPEND) { //DONE
            res = addTextToDoc(text);
        } else if (text.getType() == UpdateType.APPEND_RANGE) { //Not Working
            res = addRangeTextToDoc(text);
        } else if (text.getType() == UpdateType.DELETE) { //DONE
            if (res.length() == 0) {
                return new ReturnDocumentMessage(text.getUser(), "");
            }
            res = deleteTextFromDoc(text);
        } else if (text.getType() == UpdateType.DELETE_RANGE) {//DONE
            res = deleteRangeTextFromDoc(text);

        }
        System.out.println(res);
        return new ReturnDocumentMessage(text.getUser(), res);
    }

    private static String addTextToDoc(ManipulatedText text) {
        return res.substring(0, text.getStartPosition()) + text.getContent() + res.substring(text.getStartPosition());
    }

    private static String deleteTextFromDoc(ManipulatedText text) {
        return res.substring(0, text.getStartPosition()) + res.substring(text.getStartPosition() + 1);
    }

    private static String addRangeTextToDoc(ManipulatedText text) {
        return "";
    }

    private static String deleteRangeTextFromDoc(ManipulatedText text) {
        return res.substring(0, text.getStartPosition() + 1) + res.substring(text.getEndPosition() + 1);
    }

}
