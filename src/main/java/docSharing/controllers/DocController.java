package docSharing.controllers;

import docSharing.CRDT.PositionedChar;
import docSharing.DTO.User.UserDTO;
import docSharing.entities.Document;
import docSharing.exceptions.MissingControllerParameterException;
import docSharing.response.DocumentResponse;
import docSharing.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static java.util.Objects.isNull;

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

    private static final Logger logger = LogManager.getLogger(DocController.class.getName());

    @MessageMapping("/update/{docId}")
    @SendTo("/topic/updates/{docId}")
    public String sendUpdatedText(@DestinationVariable Long docId, String message) {
        logger.info("start sendUpdatedText function");
        if (isNull(docId)) {
            throw new MissingControllerParameterException("document is not available");
        }
        logger.info("doc servive call...");
        //docService.addCharBetween(p,q,crdt,ch); //TODO: video about socket parameters.
        return message;
    }


    @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long docId) {
        logger.info("get document {}", docId);
        Document document = docService.fetchDocumentById(docId);
        List<PositionedChar> documentAsRawText = docService.getDocumentWithRawText(document.getCrdt());

        return ResponseEntity.ok(new DocumentResponse(document.getId(), document.getName(), document.getCreationDate(), document.getLastEdited(), documentAsRawText));
    }

    @MessageMapping("/join/{docId}")
    @SendTo("/topic/usersJoin/{docId}")
    public List<String> addUserToConnectedUsers(@DestinationVariable Long docId, UserDTO userDto) {
        logger.info("User {} is now connected to doc: {}", userDto.email, docId);

        return docService.addUserToDocConnectedUsers(docId, userDto.email);
    }

    @MessageMapping("/disconnect/{docId}")
    @SendTo("/topic/usersDisconnect/{docId}")
    public List<String> removeUserFromConnectedUsers(@DestinationVariable Long docId, UserDTO userDto) {
        logger.info("User {} disconnected from doc: {}", userDto.email, docId);
        return docService.removeUserFromDocConnectedUsers(docId, userDto.email);
    }


}