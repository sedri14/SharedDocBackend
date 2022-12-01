package docSharing.controller;

import docSharing.DTO.FS.AddINodeDTO;
import docSharing.DTO.FS.INodeDTO;
import docSharing.entities.INode;
import docSharing.entities.INodeType;
import docSharing.repository.DocRepository;
import docSharing.repository.FileSystemRepository;
import docSharing.repository.LogRepository;
import docSharing.repository.PermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileSystemControllerTests {

    @Autowired
    FileSystemController fileSystemController;
    @Autowired
    AuthController authController;
    @Autowired
    FileSystemRepository fileSystemRepository;
    @Autowired
    DocRepository docRepository;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    LogRepository logRepository;

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

    @Test
    void addInode_ProvideRightParam_Works() {
        AddINodeDTO node = new AddINodeDTO(2L, rootId, "khaderFile11", INodeType.FILE);
        ResponseEntity<INode> newFile = fileSystemController.addInode(node);
        Long fileId = newFile.getBody().getId();
        boolean isFileExists = fileSystemRepository.findById(fileId).isPresent();

        assertTrue(isFileExists);

    }

    @Test
    void addInode_ProvideWrongParam_Exception() {

        assertThrows(Exception.class, () -> {
            fileSystemController.addInode(null);
        });

    }

    @Test
    void getChildren_ProvideRightParam_Works() {
        List<String> filesName = new ArrayList<>();
        filesName.add("file1");
        filesName.add("file2");

        List<INode> files = new ArrayList<>();
        for (String fileName : filesName) {
            AddINodeDTO node = new AddINodeDTO(2L, rootId, fileName, INodeType.DIR);
            ResponseEntity<INode> newFile = fileSystemController.addInode(node);
            files.add(newFile.getBody());
        }
        Long rootId = 1L;
        INodeDTO root = new INodeDTO(rootId);

        List<INode> children = fileSystemController.getChildren(root).getBody();


        assertEquals(files.size(), children.size());


    }

    @Test
    void getChildren_ProvideWrongParam_Exception() {
        assertThrows(Exception.class, () -> {
            fileSystemController.getChildren(null);
        });
    }

    @Test
    void uploadFile_ProvideRightParam_Works() {
        Long rootId = 1L;
//        fileSystemController.uploadFile(new FileWithData(rootId, userId, new)).getBody()
    }

    @Test
    void uploadFile_ProvideWrongParam_Exception() {

        assertThrows(Exception.class, () -> {
            fileSystemController.uploadFile(null).getBody();
        });

    }
}
