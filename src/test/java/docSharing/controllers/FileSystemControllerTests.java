//package docSharing.controllers;
//
//import docSharing.DTO.FS.INodeDTO;
//import docSharing.DTO.User.UserDTO;
//import docSharing.entities.INode;
//import docSharing.enums.INodeType;
//import docSharing.repository.DocRepository;
//import docSharing.repository.FileSystemRepository;
//import docSharing.repository.LogRepository;
//import docSharing.repository.PermissionRepository;
//import docSharing.response.LoginObject;
//import docSharing.response.Response;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class FileSystemControllerTests {
//
//    @Autowired
//    FileSystemController fileSystemController;
//    @Autowired
//    AuthController authController;
//    @Autowired
//    FileSystemRepository fileSystemRepository;
//    @Autowired
//    DocRepository docRepository;
//    @Autowired
//    PermissionRepository permissionRepository;
//    @Autowired
//    LogRepository logRepository;
//
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
//        user = authController.login(new UserDTO("khaderzatari@gmail.com", "aA123456"));
//        token = user.getBody().getData().getToken();
//        userId = user.getBody().getData().getId();
//
//    }
//
//    @Test
//    void addInode_ProvideRightParam_Works() {
//        INodeDTO node = new INodeDTO(userId, rootId, "khaderFile11", INodeType.FILE);
//        ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(node, token, userId);
//        Long fileId = newFile.getBody().getData().getId();
//        boolean isFileExists = fileSystemRepository.findById(fileId).isPresent();
//
//        assertTrue(isFileExists);
//
//    }
//
//    @Test
//    void addInode_ProvideWrongParam_Exception() {
//
//        assertThrows(Exception.class, () -> {
//            fileSystemController.addInode(null, token, userId);
//        });
//
//    }
//
//    @Test
//    void getChildren_ProvideRightParam_Works() {
//        List<String> filesName = new ArrayList<>();
//        filesName.add("file1");
//        filesName.add("file2");
//
//        List<INode> files = new ArrayList<>();
//        for (String fileName : filesName) {
//            INodeDTO node = new INodeDTO(userId, rootId, fileName, INodeType.DIR);
//            ResponseEntity<Response<INode>> newFile = fileSystemController.addInode(node, token, userId);
//            files.add(newFile.getBody().getData());
//        }
//
//        INodeDTO root = new INodeDTO(rootId);
//
//        Response<List<INode>> children = fileSystemController.getChildren(root, token, userId).getBody();
//
//
//        assertEquals(files.size(), children.getData().size());
//
//
//    }
//
//    @Test
//    void getChildren_ProvideWrongParam_Exception() {
//        assertThrows(Exception.class, () -> {
//            fileSystemController.getChildren(null, token, userId);
//        });
//    }
//
////    @Test
////    void uploadFile_ProvideRightParam_Works() {
////        Long rootId = 1L;
//////        fileSystemController.uploadFile(new FileWithData(rootId, userId, new)).getBody()
////    }
//
//    @Test
//    void uploadFile_ProvideWrongParam_Exception() {
//
//        assertThrows(Exception.class, () -> {
//            fileSystemController.uploadFile(null, token, userId).getBody();
//        });
//
//    }
//}
