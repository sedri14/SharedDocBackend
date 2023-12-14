package docSharing.controllers;

import docSharing.CRDT.CharItem;
import docSharing.auth.AuthService;
import docSharing.requestObjects.UpdateTextDTO;
import docSharing.auth.RegisterRequest;
import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.SharedRole;
import docSharing.user.User;
import docSharing.user.UserRole;
import docSharing.responseObjects.CharItemResponse;
import docSharing.responseObjects.DocumentWithUserRoleResponse;
import docSharing.responseObjects.SharedRoleResponse;
import docSharing.service.*;
import docSharing.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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
    SharedRoleService sharedRoleService;

    private static final Logger logger = LogManager.getLogger(DocController.class.getName());

    @MessageMapping("/update/{docId}")
    @SendTo("/topic/updates/{docId}")
    public List<CharItemResponse> sendUpdatedText(@DestinationVariable Long docId, @RequestBody UpdateTextDTO updateTextDTO) {
        logger.info("char <<{}>> is been added to doc {}", updateTextDTO.ch, docId);
        Document document = docService.getCachedDocument(docId);
        int siteId = docService.getSiteId(docId, updateTextDTO.email);
        docService.addCharBetween(updateTextDTO.p, updateTextDTO.q, document, updateTextDTO.ch, siteId);
        List<CharItem> rawText = docService.getRawText(document.getContent());

        return rawText.stream().map(CharItemResponse::fromCharItem).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getDoc/{docId}", method = RequestMethod.GET)
    public ResponseEntity<DocumentWithUserRoleResponse> getDocument(@PathVariable Long docId, @RequestAttribute UserRole userRole) {
        logger.info("get document {}...", docId);
        Document document = docService.getCachedDocument(docId);
        List<CharItem> rawText = docService.getRawText(document.getContent());

        return ResponseEntity.ok(DocumentWithUserRoleResponse.fromDocument(document, userRole));
    }

    @MessageMapping("/join/{docId}")
    @SendTo("/topic/usersJoin/{docId}")
    public List<String> addUserToConnectedUsers(@DestinationVariable Long docId, RegisterRequest registerRequest) {
        logger.info("User {} is connecting to doc: {}", registerRequest.getEmail(), docId);
        User user = userService.fetchUserByEmail(registerRequest.getEmail());
        return docService.addUserToDocConnectedUsers(docId, user, registerRequest.getEmail());
    }

    @MessageMapping("/disconnect/{docId}")
    @SendTo("/topic/usersDisconnect/{docId}")
    public List<String> removeUserFromConnectedUsers(@DestinationVariable Long docId, RegisterRequest registerRequest) {
        logger.info("User {} disconnected from doc: {}", registerRequest.getEmail(), docId);
        return docService.removeUserFromDocConnectedUsers(docId, registerRequest.getEmail());
    }

    @RequestMapping(value = "roles/{docId}", method = RequestMethod.GET)
    public ResponseEntity<List<SharedRoleResponse>> getDocumentRoles(@PathVariable Long docId, @RequestAttribute INode inode) {
        logger.info("get roles for document {}...", docId);
        Document doc = (Document)inode;
        List<SharedRole> sharedRoles = sharedRoleService.getAllUsersWithPermission(doc);
        List<SharedRoleResponse> peopleWithAccess = sharedRoles.stream().map(item -> new SharedRoleResponse(item.getUser().getEmail(), item.getUser().getName(), item.getRole())).collect(Collectors.toList());
        //Add owner to list
        peopleWithAccess.add(0, new SharedRoleResponse(doc.getOwner().getEmail(), doc.getOwner().getName(), UserRole.OWNER));

        return ResponseEntity.ok(peopleWithAccess);
    }
}