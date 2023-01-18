package docSharing.controllers;

import docSharing.DTO.FS.PermissionDTO;
import docSharing.DTO.Doc.UpdateDocContentRes;
import docSharing.Utils.LogUtils;
import docSharing.Utils.Validation;
import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
import docSharing.response.PermissionResponse;
import docSharing.response.Response;
import docSharing.response.TokenError;
import docSharing.service.*;
import docSharing.DTO.Doc.ChangeRoleDTO;
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
import java.util.Objects;
import java.util.Optional;


@RequestMapping("/doc")
@CrossOrigin
@RestController
public class DocController {
    @Autowired
    DocService docService;
    @Autowired
    UserService userService;
    @Autowired
    private PermissionService permissionService;
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
    @MessageMapping("/update/{docId}")
    @SendTo("/topic/updates/{docId}")
    public UpdateDocContentRes sendUpdatedText(@DestinationVariable Long docId, ManipulatedTextDTO manipulatedTextDTO) {

        logger.info("start sendUpdatedText function");
        logger.info("validate docId param");
        Validation.nullCheck(docId);
        logger.info("validate manipulatedTextDTO param");
        Validation.nullCheck(manipulatedTextDTO);

        UpdateDocContentRes updateDocContentRes = docService.UpdateDocContent(docId, manipulatedTextDTO);
        logUtils.addToLog(docId, manipulatedTextDTO);

        return updateDocContentRes;
    }


    /**
     * @param docId  document Id
     * @param token  token of logged in user
     * @param userId user id
     * @return document response
     */
    @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
    public ResponseEntity<Response<Document>> getDocument(@PathVariable Long docId, @RequestHeader String token, @RequestHeader Long userId) {
        logger.info("start getDocument function");
        logger.info("validate docId param");

        if (!authService.isValidToken(userId, token)) {
            return ResponseEntity.badRequest().body(Response.failure(TokenError.INVALID_TOKEN.toString()));
        }
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


    /**
     * @param permissionDTO usersId  and DocId
     * @param token         token of logged in user
     * @param userId        user id
     * @return response Entity of the userRole
     */
    @RequestMapping(value = "getPerm", method = RequestMethod.POST)
    public ResponseEntity<Response<PermissionResponse>> getPermission(@RequestBody PermissionDTO permissionDTO, @RequestHeader String token, @RequestHeader Long userId) {

        logger.info("start getPerm Function");

        if (!authService.isValidToken(userId, token)) {
            return ResponseEntity.badRequest().body(Response.failure(TokenError.INVALID_TOKEN.toString()));
        }

        logger.info("validate permission param");
        Validation.nullCheck(permissionDTO);
        Validation.nullCheck(permissionDTO.docId);
        Validation.nullCheck(permissionDTO.userId);

        logger.info("find the user and the document object according to their id");
        Document doc = docService.findDocById(permissionDTO.docId);
        User user = userService.getById(permissionDTO.userId);

        logger.info("get the permission");
        Optional<Permission> optionalPer = permissionService.getPermission(user, doc);
        if (!optionalPer.isPresent()) {
            return ResponseEntity.badRequest().body(Response.failure("You have no Access to this file"));
        }
        UserRole userRole = optionalPer.get().getUserRole();

        return ResponseEntity.ok(Response.success(new PermissionResponse(userRole)));
    }


    /**
     * @param docId         document Id
     * @param changeRoleDTO Param to change the role of user
     * @param token         token of logged in user
     * @param userId        user id
     * @return if the change is done or note
     */
    @RequestMapping(value = "changeUserRoll/{docId}", method = RequestMethod.POST)
    public ResponseEntity<Response<PermissionResponse>> changeUserRole(@PathVariable Long docId, @RequestBody ChangeRoleDTO changeRoleDTO, @RequestHeader String token, @RequestHeader Long userId) {

        logger.info("start changeUserRollInDoc function");

        if (!authService.isValidToken(userId, token)) {
            return ResponseEntity.badRequest().body(Response.failure(TokenError.INVALID_TOKEN.toString()));
        }

        logger.info("validate docId param");
        Validation.nullCheck(docId);
        logger.info("validate ChangeRoleDTO param");
        Validation.nullCheck(changeRoleDTO);
//        Validation.nullCheck(changeRoleDTO.userRole);
        Validation.nullCheck(changeRoleDTO.ownerId);
        Validation.nullCheck(changeRoleDTO.email);
        Validation.nullCheck(changeRoleDTO.isDelete);

        if (!Objects.equals(docService.getOwner(docId), changeRoleDTO.ownerId)) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); //should return PermissionResponse
        }

        logger.info("find the user and the document object according to their id");
        Document doc = docService.findDocById(docId);
        User user = userService.findByEmail(changeRoleDTO.email);
        Validation.nullCheck(user);
        UserRole userRole;
        try {
            userRole = permissionService.changeRole(doc, user, changeRoleDTO.userRole, changeRoleDTO.isDelete);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(new PermissionResponse(userRole)));
    }

}