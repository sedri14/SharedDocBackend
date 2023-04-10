package docSharing.controllers;

import docSharing.DTO.Doc.UpdateDocContentRes;
import docSharing.Utils.LogUtils;
import docSharing.Utils.Validation;
import docSharing.entities.Document;
import docSharing.response.Response;
import docSharing.service.*;
import docSharing.DTO.Doc.CurrentViewingUserDTO;
import docSharing.DTO.Doc.ManipulatedTextDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/doc")
@CrossOrigin
@RestController
public class DocController {

    @Autowired
    FileSystemService fsService;
    @Autowired
    DocService docService;
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    LogUtils logUtils;

    private static final Logger logger = LogManager.getLogger(DocController.class.getName());

    /**
     * @param docId              document id
     * @param manipulatedTextDTO the change in the text
     * @return the changed content to all subscribed users
     */
//    @MessageMapping("/update/{docId}")
//    @SendTo("/topic/updates/{docId}")
//    public UpdateDocContentRes sendUpdatedText(@DestinationVariable Long docId, ManipulatedTextDTO manipulatedTextDTO) {
//
//        logger.info("start sendUpdatedText function");
//        logger.info("validate docId param");
//        Validation.nullCheck(docId);
//        logger.info("validate manipulatedTextDTO param");
//        Validation.nullCheck(manipulatedTextDTO);
//
//        UpdateDocContentRes updateDocContentRes = docService.UpdateDocContent(docId, manipulatedTextDTO);
//        logUtils.addToLog(docId, manipulatedTextDTO);
//
//        return updateDocContentRes;
//    }


    /**
     * @param docId  document Id
     * @param token  token of logged in user
     * @return document response
     */
    @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
    public ResponseEntity<Response<Document>> getDocument(@PathVariable Long docId, @RequestHeader String token) {
        logger.info("start getDocument function");
        logger.info("validate docId param");

        Validation.nullCheck(docId);

        Document document;
        try {
            document = docService.getDocument(docId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(document));
    }


    /**
     * @param docId document Id
     * @param user  Current Viewing User userName
     * @return the list of all the current viewing user to the document
     */
    @MessageMapping("/join/{docId}")
    @SendTo("/topic/usersJoin/{docId}")
    public List<String> sendNewUserJoinMessage(@DestinationVariable Long docId, CurrentViewingUserDTO user) {

        logger.info("start sendNewUserJoinMessage function");


        logger.info("validate docId param");
        Validation.nullCheck(docId);
        logger.info("validate User param");
        Validation.nullCheck(user);

        return docService.addUserToViewingUsers(docId, user.userName);
    }


    /**
     * @param docId document Id
     * @param user  Current Viewing User userName
     * @return the list of all the current viewing user to the document
     */
    @MessageMapping("/userDisconnect/{docId}")
    @SendTo("/topic/userDisconnect/{docId}")
    public List<String> removeUserFromViewingUsers(@DestinationVariable Long docId, CurrentViewingUserDTO user) {

        logger.info("start sendNewUserJoinMessage function");
        logger.info("validate docId param");
        Validation.nullCheck(docId);
        logger.info("validate User param");
        Validation.nullCheck(user);
        Validation.nullCheck(user.userName);

        return docService.removeUserFromViewingUsers(docId, user.userName);

    }


}