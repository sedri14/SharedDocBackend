package docSharing.controller;

import docSharing.service.DocService;
import docSharing.test.OnlineUser;
import docSharing.test.ManipulatedText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DocController {
    @Autowired
    DocService docService;

    @MessageMapping("/join")
    @SendTo("/topic/usersJoin")
    public OnlineUser sendNewUserJoinMessage(OnlineUser user) {
        //add userName to the document list viewing users.
        return user;
    }


    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public ManipulatedText sendUpdatedText(ManipulatedText text) {
        return docService.sendUpdatedText(text);
    }



//methods:
    //create new document

    //change user permmision (Permission ,User)

    //insert viewer to viewer list
    // insert editor to editor list

    //boolean isEditor(User user)
    //boolean isViewer(User user)

    // updateUsersList


}