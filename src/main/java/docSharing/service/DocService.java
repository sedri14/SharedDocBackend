package docSharing.service;

import docSharing.test.ManipulatedText;
import docSharing.test.UpdateType;
import org.springframework.stereotype.Service;

@Service
public class DocService {

    public static String res = "";

    public static ManipulatedText sendUpdatedText(ManipulatedText text) {
        System.out.println(text);

        if (text.getType() == UpdateType.APPEND) {

//            res = res.substring(0, text.getStartPosition())
//                    + text.getContent()Ï
//                    + res.substring(text.getStartPosition());

        } else if (text.getType() == UpdateType.APPEND_RANGE) {

        } else if (text.getType() == UpdateType.DELETE) {

        } else if (text.getType() == UpdateType.DELETE_RANGE) {

        }
        //weÏ should add the updated content to the document itself; before the return.
        //update it in the database.
        //if it's append send the text to append and the indexes
        //if it's delete send the indexes and delete them from the client browser.
        System.out.println();
        return new ManipulatedText(text.getUser(), text.getType(), text.getContent(), text.getStartPosition(), text.getEndPosition());
    }


    /*
    Asaf part:
        public void sendPlainMessage(JoinMessage message)

           public UpdateMessage sendPlainMessage(UpdateMessage message)
     */


    //create new document

    //change user permmision (Permission ,User)

    //insert viewer to viewer list
    // insert editor to editor list

    //boolean isEditor(User user)
    //boolean isViewer(User user)

    // updateUsersList


}
