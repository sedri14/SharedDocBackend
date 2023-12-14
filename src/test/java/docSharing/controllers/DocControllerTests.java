//package docSharing.controllers;
//
//
//import docSharing.DTO.FS.INodeDTO;
//import docSharing.DTO.FS.PermissionDTO;
//import docSharing.DTO.User.UserDTO;
//import docSharing.entities.*;
//import docSharing.enums.INodeType;
//import docSharing.user.UserRole;
//import docSharing.repository.*;
//import docSharing.response.LoginObject;
//import docSharing.response.PermissionResponse;
//import docSharing.response.Response;
//import docSharing.service.DocService;
//import docSharing.DTO.ChangeRoleDTO;
//import docSharing.DTO.Doc.ManipulatedTextDTO;
//import docSharing.DTO.Doc.CurrentViewingUserDTO;
//import docSharing.DTO.Doc.UpdateTypeDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class DocControllerTests {
//    @Autowired
//    DocController docController;
//    @Autowired
//    DocRepository docRepository;
//    @Autowired
//    AuthController authController;
//    @Autowired
//    FileSystemController fileSystemController;
//    @Autowired
//    DocService docService;
//
//    @Autowired
//    PermissionRepository permissionRepository;
//    @Autowired
//    LogRepository logRepository;
//    @Autowired
//    FileSystemRepository fileSystemRepository;
//    Long rootId;
//    ResponseEntity<Response<LoginObject>> user;
//    String token;
//
//    Long userId;
//
//    @BeforeEach
//    void setup() {
//        permissionRepository.deleteAll();
//        logRepository.deleteAll();
//        docRepository.deleteAll();
//        fileSystemRepository.deleteAll();
//        INode root = new INode("root", INodeType.DIR, LocalDateTime.now(), null, null);
//        fileSystemRepository.save(root);
//        INode foundRoot = fileSystemRepository.findByName("root");
//        rootId = foundRoot.getId();
//
//        user = authController.login(new UserDTO("khaderzatari@gmail.com", "aA123456"));
//        token = user.getBody().getData().getToken();
//        userId = user.getBody().getData().getId();
//    }
//
//    //    @Test
////    void saveLog() {
////        logService.saveOneLogToDB("khader", userId, 13L);
////    }
////
//    @Test
//    void getDocument_GetContentExistsDoc_Works() throws InterruptedException {
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile8", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        String content = "k";
//        docController.getDocument(doc.getId(), token, userId);
//        docController.sendUpdatedText(doc.getId(), new ManipulatedTextDTO(userId, UpdateTypeDTO.APPEND, content, 0, 0));
//        Thread.sleep(6000);
//        ResponseEntity<Response<Document>> document = docController.getDocument(doc.getId(), token, userId);
//        assertEquals(content, document.getBody().getData().getContent(), "the content isn't equal");
//
//    }
//
//    @Test
//    void getDocument_GetContentNotExistDoc_BadRequest() {
//        Long docId = 1000L;
//        boolean isFound = docRepository.findById(docId).isPresent();
//        assertFalse(isFound);
//        assertEquals(HttpStatus.BAD_REQUEST, docController.getDocument(docId, token, userId).getStatusCode(), "the document doesn't exist");
//    }
//
//    @Test
//    void sendUpdatedText_ProvideRightParam_Works() throws InterruptedException {
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile6", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        String content = "k";
//        docController.getDocument(doc.getId(), token, userId);
//        docController.sendUpdatedText(doc.getId(), new ManipulatedTextDTO(userId, UpdateTypeDTO.APPEND, content, 0, 0));
//        Thread.sleep(6000);
//        ResponseEntity<Response<Document>> document = docController.getDocument(doc.getId(), token, userId);
//        assertEquals(content, document.getBody().getData().getContent(), "the content isn't equal");
//
//    }
//
//    @Test
//    void sendUpdatedText_ProvideWrongParam_Exception() throws InterruptedException {
//
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile9", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        docController.getDocument(doc.getId(), token, userId);
//
//        assertThrows(Exception.class, () -> {
//            docController.sendUpdatedText(doc.getId(), null);
//        });
//
//    }
//
//    @Test
//    void sendNewUserJoinMessage_ProvideRightParam_Works() {
//
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile10", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        List<String> currentViewingUsers = new ArrayList<>();
//        currentViewingUsers.add("khader");
//        docController.sendNewUserJoinMessage(doc.getId(), new CurrentViewingUserDTO("khader"));
//        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
//
//    }
//
//    @Test
//    void sendNewUserJoinMessage_ProvideWrongParam_Exception() {
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile11", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        assertThrows(Exception.class, () -> {
//            docController.sendNewUserJoinMessage(doc.getId(), null);
//        });
//    }
//
//    @Test
//    void removeUserFromViewingUsers_ProvideRightParam_Works() {
//
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile12", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        List<String> currentViewingUsers = new ArrayList<>();
//        currentViewingUsers.add("khader");
//        docController.sendNewUserJoinMessage(doc.getId(), new CurrentViewingUserDTO("khader"));
//        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
//        currentViewingUsers.remove("khader");
//        docController.removeUserFromViewingUsers(doc.getId(), new CurrentViewingUserDTO("khader"));
//        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
//    }
//
//    @Test
//    void removeUserFromViewingUsers_ProvideWrongParam_Exception() {
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile13", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        assertThrows(Exception.class, () -> {
//            docController.removeUserFromViewingUsers(doc.getId(), null);
//        });
//    }
//
//    @Test
//    void getPermission_ProvideRightParam_Works() {
//
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile16", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        Response<PermissionResponse> permission = docController.getPermission(new PermissionDTO(userId, doc.getId()), token, userId).getBody();
//        UserRole role = permission.getData().getUserRole();
//        assertEquals(UserRole.EDITOR, role);
//    }
//
//    @Test
//    void getPermission_ProvideWrongParam_Exception() {
//
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile17", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        assertThrows(Exception.class, () -> {
//            docController.getPermission(new PermissionDTO(null, doc.getId()), token, userId);
//        });
//    }
//
//    @Test
//    void changeUserRollInDoc_ProvideRightParam_Works() {
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile19", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        docController.changeUserRole(doc.getId(), new ChangeRoleDTO(userId, "abc@gmail.com", UserRole.EDITOR), token, userId);
//        Response<PermissionResponse> permission = docController.getPermission(new PermissionDTO(userId, doc.getId()), token, userId).getBody();
//        UserRole role = permission.getData().getUserRole();
//        assertEquals(UserRole.EDITOR, role);
//    }
//
//    @Test
//    void changeUserRollInDoc_ProvideWrongParam_Exception() {
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(new INodeDTO(userId, rootId, "khaderFile5", INodeType.FILE), token, userId);
//        Document doc = (Document) newFile.getBody().getData();
//        assertThrows(Exception.class, () -> {
//            docController.changeUserRole(doc.getId(), null, token, userId);
//        });
//    }
//
//}
