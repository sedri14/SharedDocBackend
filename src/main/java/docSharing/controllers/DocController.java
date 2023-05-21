package docSharing.controllers;

import docSharing.CRDT.Char;
import docSharing.DTO.UpdateTextDTO;
import docSharing.DTO.User.UserDTO;
import docSharing.entities.Document;
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
    public List<Char> sendUpdatedText(@DestinationVariable Long docId, @RequestBody UpdateTextDTO updateTextDTO) {
        logger.info("char <<{}>> is been added to doc {}", updateTextDTO.ch, docId);
        Document document = docService.fetchDocumentById(docId);
        docService.addCharBetween(updateTextDTO.p, updateTextDTO.q, document, updateTextDTO.ch, updateTextDTO.siteId);

        return docService.getRawText(document.getContent());
    }


    @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long docId) {
        logger.info("get document {}", docId);
        Document document = docService.fetchDocumentById(docId);
        List<Char> rawText = docService.getRawText(document.getContent());

        return ResponseEntity.ok(DocumentResponse.fromDocument(document, rawText));
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