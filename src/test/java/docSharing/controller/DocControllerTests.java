package docSharing.controller;


import docSharing.DTO.FS.AddINodeDTO;
import docSharing.DTO.FS.PermissionDTO;
import docSharing.entities.*;
import docSharing.repository.*;
import docSharing.response.PermissionResponse;
import docSharing.response.Response;
import docSharing.service.DocService;
import docSharing.service.LogService;
import docSharing.DTO.Doc.ChangeRoleDTO;
import docSharing.DTO.Doc.ManipulatedTextDTO;
import docSharing.DTO.Doc.CurrentViewingUserDTO;
import docSharing.DTO.Doc.UpdateTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

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
    Long rootId;

    @BeforeEach
    void setup() {
        permissionRepository.deleteAll();
        logRepository.deleteAll();
        docRepository.deleteAll();
        fileSystemRepository.deleteAll();
        INode root = new INode("root", INodeType.DIR, LocalDateTime.now(), null, null);
        fileSystemRepository.save(root);
        INode foundRoot = fileSystemRepository.findByName("root");
        rootId = foundRoot.getId();
    }

//    @Test
//    void saveLog() {
//        logService.saveOneLogToDB("khader", 2L, 13L);
//    }
//
    @Test
    void getDocument_GetContentExistsDoc_Works() throws InterruptedException {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile8", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        String content = "k";
        docController.getDocument(doc.getId());
        docController.sendUpdatedText(doc.getId(), new ManipulatedTextDTO(2L, UpdateTypeDTO.APPEND, content, 0, 0));
        Thread.sleep(6000);
        ResponseEntity<Document> document = docController.getDocument(doc.getId());
        assertEquals(content, document.getBody().getContent(), "the content isn't equal");

    }

    @Test
    void getDocument_GetContentNotExistDoc_Exception() {
        Long docId = 1000L;
        boolean isFound = docRepository.findById(docId).isPresent();
        assertFalse(isFound);
        assertThrows(Exception.class, () -> {
            docController.getDocument(docId);
        }, "the document doesn't exist");
    }

    @Test
    void sendUpdatedText_ProvideRightParam_Works() throws InterruptedException {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile6", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        String content = "k";
        docController.getDocument(doc.getId());
        docController.sendUpdatedText(doc.getId(), new ManipulatedTextDTO(2L, UpdateTypeDTO.APPEND, content, 0, 0));
        Thread.sleep(6000);
        ResponseEntity<Document> document = docController.getDocument(doc.getId());
        assertEquals(content, document.getBody().getContent(), "the content isn't equal");

    }

    @Test
    void sendUpdatedText_ProvideWrongParam_Exception() throws InterruptedException {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile9", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        docController.getDocument(doc.getId());

        assertThrows(Exception.class, () -> {
            docController.sendUpdatedText(doc.getId(), null);
        });

    }

    @Test
    void sendNewUserJoinMessage_ProvideRightParam_Works() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile10", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        List<String> currentViewingUsers = new ArrayList<>();
        currentViewingUsers.add("khader");
        docController.sendNewUserJoinMessage(doc.getId(), new CurrentViewingUserDTO("khader"));
        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));

    }

    @Test
    void sendNewUserJoinMessage_ProvideWrongParam_Exception() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile11", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.sendNewUserJoinMessage(doc.getId(), null);
        });
    }

    @Test
    void removeUserFromViewingUsers_ProvideRightParam_Works() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile12", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        List<String> currentViewingUsers = new ArrayList<>();
        currentViewingUsers.add("khader");
        docController.sendNewUserJoinMessage(doc.getId(), new CurrentViewingUserDTO("khader"));
        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
        currentViewingUsers.remove("khader");
        docController.removeUserFromViewingUsers(doc.getId(), new CurrentViewingUserDTO("khader"));
        assertEquals(currentViewingUsers, docService.getCurrentViewingUserList(doc.getId()));
    }

    @Test
    void removeUserFromViewingUsers_ProvideWrongParam_Exception() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile13", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.removeUserFromViewingUsers(doc.getId(), null);
        });
    }

    @Test
    void getPermission_ProvideRightParam_Works() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile16", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        Response<PermissionResponse> permission = docController.getPermission(new PermissionDTO(2L, doc.getId())).getBody();
        UserRole role = permission.getData().getUserRole();
        assertEquals(UserRole.EDITOR, role);
    }

    @Test
    void getPermission_ProvideWrongParam_Exception() {

        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile17", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.getPermission(new PermissionDTO(null, doc.getId()));
        });
    }

    @Test
    void changeUserRollInDoc_ProvideRightParam_Works() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile19", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        docController.changeUserRole(doc.getId(), new ChangeRoleDTO(2L, "abc@gmail.com", UserRole.EDITOR));
        Response<PermissionResponse> permission = docController.getPermission(new PermissionDTO(2L, doc.getId())).getBody();
        UserRole role = permission.getData().getUserRole();
        assertEquals(UserRole.EDITOR, role);
    }

    @Test
    void changeUserRollInDoc_ProvideWrongParam_Exception() {
        ResponseEntity<INode> newFile = fileSystemController.addInode(new AddINodeDTO(2L, rootId, "khaderFile5", INodeType.FILE));
        Document doc = (Document) newFile.getBody();
        assertThrows(Exception.class, () -> {
            docController.changeUserRole(doc.getId(), null);
        });
    }

}
