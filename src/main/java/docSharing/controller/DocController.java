package docSharing.controller;

import docSharing.DTO.ReturnDocumentMessage;
import docSharing.service.DocService;
import docSharing.test.OnlineUser;
import docSharing.test.ManipulatedText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DocController {
    @Autowired
    DocService docService;

    @MessageMapping("/join")
    @SendTo("/topic/usersJoin")
    public List<String> sendNewUserJoinMessage(OnlineUser user) {
        Long docId = 6L;
        //add userName to the document list viewing users.
        return docService.addUserToviewingUsers(docId, user.getUserName());
    }


    //    @MessageMapping("/update/{docId}")
//    @SendTo("/topic/updates/{docId}") @DestinationVariable Long docId
    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public ReturnDocumentMessage sendUpdatedText(ManipulatedText text) {
        Long docId = 6L;
        System.out.println(docId);
        return docService.sendUpdatedText(docId, text);
    }


//methods:
    //create new document

    //change user permmision (Permission ,User)

    //insert viewer to viewer list
    // insert editor to editor list

    //boolean isEditor(User user)
    //boolean isViewer(User user)

    // updateUsersList


    //export document


}