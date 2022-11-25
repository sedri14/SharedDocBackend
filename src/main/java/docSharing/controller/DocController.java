package docSharing.controller;

import docSharing.DTO.PermissionDTO;
import docSharing.DTO.ReturnDocumentMessage;
import docSharing.entities.Permission;
import docSharing.service.DocService;
import docSharing.test.ChnageRole;
import docSharing.test.OnlineUser;
import docSharing.test.ManipulatedText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;


//@Controller
@RequestMapping("/doc")
@RestController
public class DocController {
    @Autowired
    DocService docService;

    private static final Logger logger = LogManager.getLogger(DocController.class.getName());

    /**
     * @param docId           document id
     * @param manipulatedText the change in the text
     * @return the changes to all subscribed users
     */
    @MessageMapping("/update/{docId}")
    @SendTo("/topic/updates/{docId}")
    public ReturnDocumentMessage sendUpdatedText(@DestinationVariable Long docId, ManipulatedText manipulatedText) {

        logger.info("start sendUpdatedText function");
        return docService.sendUpdatedText(docId, manipulatedText);
    }

    /**
     * @param docId document id
     * @return the content of the document
     */
    @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
    public ResponseEntity<String> getDocument(@PathVariable Long docId) {
        logger.info("start getDocument function");
        return ResponseEntity.status(HttpStatus.OK).body(docService.getDocument(docId));
    }


    @MessageMapping("/join/{docId}")
    @SendTo("/topic/usersJoin/{docId}")
    public List<String> sendNewUserJoinMessage(@DestinationVariable Long docId, OnlineUser user) {
        logger.info("start sendNewUserJoinMessage function");

        return docService.addUserToViewingUsers(docId, user.getUserName());
    }

    @RequestMapping(value = "setPerm", method = RequestMethod.POST)
    public ResponseEntity<Permission> setPermission (@RequestBody PermissionDTO permission) {
        logger.info("start setPermission function");
        return ResponseEntity.ok(docService.setPermission(permission.userId, permission.docId, permission.userRole));
    }

    @RequestMapping(value = "getPerm", method = RequestMethod.POST)
    public ResponseEntity<Permission> getPermission (@RequestBody PermissionDTO permission) {

        return ResponseEntity.ok(docService.getPermission(permission.userId, permission.docId));
    }

//    @RequestMapping(value = "/changeUserRoll/{docId}")
//    public ResponseEntity<Boolean> changeUserRollInDoc(@PathVariable Long docId, @RequestBody ChnageRole changeRole) {
//        logger.info("start changeUserRollInDoc function");
//        return ResponseEntity.status(HttpStatus.OK).body(docService.changeUserRollInDoc(docId, changeRole.ownerId, changeRole.email, changeRole.userRole));

//}


//    @RequestMapping(value = "/savecontent/{docId}", method = RequestMethod.POST)
//    public ResponseEntity<Void> test(@PathVariable Long docId) {
//        logger.info("start saveContent function");
//        return ResponseEntity.status(HttpStatus.OK).body(docService.saveOneDocContentToDB(docId, "what the hell is here"));
//
//    }


}