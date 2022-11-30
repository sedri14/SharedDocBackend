package docSharing.controller;


import docSharing.DTO.AddINodeDTO;
import docSharing.DTO.PermissionDTO;
import docSharing.UserDTO.UserDTO;
import docSharing.entities.*;
import docSharing.repository.*;
import docSharing.response.IdTokenPair;
import docSharing.response.Response;
import docSharing.service.DocService;
import docSharing.service.LogService;
import docSharing.service.PermissionService;
import docSharing.service.UserService;
import docSharing.test.ChnageRole;
import docSharing.test.ManipulatedText;
import docSharing.test.OnlineUser;
import docSharing.test.UpdateType;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.print.Doc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class DocControllerTests {
    @Autowired
    DocController docController;
    @Autowired
    DocRepository docRepository;
    @Autowired
    AuthController authController;
    @Autowired
    FileSystemController fileSystemController;
    @Autowired
    DocService docService;

    @Autowired
    LogService logService;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    FileSystemRepository fileSystemRepository;

    @BeforeEach
    void setup() {
//        permissionRepository.deleteAll();
//        logRepository.deleteAll();
//        docRepository.deleteAll();
//        fileSystemRepository.deleteAll();
//        fileSystemRepository.save(new INode("root", INodeType.DIR, LocalDateTime.now(), null, null));
    }

//    @Test
//    void saveLog() {
//        logService.saveOneLogToDB("khader", 2L, 13L);
//    }

    @Test
    void getDocument_GetContentExistsDoc_Works() throws InterruptedException {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        String content = "k";
        docController.getDocument(doc.getId());
        docController.sendUpdatedText(doc.getId(), new ManipulatedText("1L", UpdateType.APPEND, content, 0, 0));
        Thread.sleep(6000);
        ResponseEntity<Document> document = docController.getDocument(doc.getId());
        assertEquals(content, document.getBody().getContent(), "the content isn't equal");

    }

    @Test
    void getDocument_GetContentNotExistDoc_Exception() {
        Long docId = 1000L;
        boolean isFound = docRepository.findById(docId).isPresent();
        assertEquals(false, isFound);
        assertThrows(Exception.class, () -> {
            docController.getDocument(docId);
        }, "the document doesn't exist");
    }

    @Test
    void sendUpdatedText_ProvideRightParam_Works() throws InterruptedException {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        String content = "k";
        docController.getDocument(doc.getId());
        docController.sendUpdatedText(doc.getId(), new ManipulatedText("1L", UpdateType.APPEND, content, 0, 0));
        Thread.sleep(6000);
        ResponseEntity<Document> document = docController.getDocument(doc.getId());
        assertEquals(content, document.getBody().getContent(), "the content isn't equal");

    }

    @Test
    void sendUpdatedText_ProvideWrongParam_Exception() throws InterruptedException {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile7", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        String content = "k";
        docController.getDocument(doc.getId());

        assertThrows(Exception.class, () -> {
            docController.sendUpdatedText(doc.getId(), null);
        });

    }

    @Test
    void sendNewUserJoinMessage_ProvideRightParam_Works() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        List<String> currentViewingUsers = new ArrayList<>();
        currentViewingUsers.add("khader");
        docController.sendNewUserJoinMessage(doc.getId(), new OnlineUser("khader"));
        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));

    }

    @Test
    void sendNewUserJoinMessage_ProvideWrongParam_Exception() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.sendNewUserJoinMessage(doc.getId(), null);
        });
    }

    @Test
    void removeUserFromViewingUsers_ProvideRightParam_Works() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        List<String> currentViewingUsers = new ArrayList<>();
        currentViewingUsers.add("khader");
        docController.sendNewUserJoinMessage(doc.getId(), new OnlineUser("khader"));
        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
        currentViewingUsers.remove("khader");
        docController.removeUserFromViewingUsers(doc.getId(), new OnlineUser("khader"));
        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
    }

    @Test
    void removeUserFromViewingUsers_ProvideWrongParam_Exception() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.removeUserFromViewingUsers(doc.getId(), null);
        });
    }

    @Test
    void getPermission_ProvideRightParam_Works() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertEquals(UserRole.EDITOR, docController.getPermission(new PermissionDTO(2L, doc.getId(), null)));
    }

    @Test
    void getPermission_ProvideWrongParam_Exception() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.getPermission(new PermissionDTO(null, doc.getId(), null));
        });
    }

    @Test
    void changeUserRollInDoc_ProvideRightParam_Works() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile5", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        docController.changeUserRollInDoc(doc.getId(), new ChnageRole(2L, "khaderzatari@gmail.com", UserRole.EDITOR));
        assertEquals(UserRole.EDITOR, docController.getPermission(new PermissionDTO(2L, doc.getId(), null)));
    }

    @Test
    void changeUserRollInDoc_ProvideWrongParam_Exception() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, 1L, "khaderFile5", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.changeUserRollInDoc(doc.getId(), new ChnageRole(2L, "khaderzatari@gmail.com", UserRole.EDITOR));
        });
    }

}
