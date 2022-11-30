package docSharing.controller;

import docSharing.DTO.AddINodeDTO;
import docSharing.DTO.INodeDTO;
import docSharing.entities.INode;
import docSharing.entities.INodeType;
import docSharing.repository.DocRepository;
import docSharing.repository.FileSystemRepository;
import docSharing.repository.LogRepository;
import docSharing.repository.PermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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


    @AfterEach
    void setup() {
//        permissionRepository.deleteAll();
//        logRepository.deleteAll();
//        docRepository.deleteAll();
//        fileSystemRepository.deleteAll();
    }

    @Test
    void addInode_ProvideRightParam_Works() {
        AddINodeDTO node = new AddINodeDTO(2L, 1L, "khaderFile11", INodeType.FILE);
        ResponseEntity<INode> newFile = fileSystemController.addInode(node);
        Long fileId = newFile.getBody().getId();
        boolean isFileExists = fileSystemRepository.findById(fileId).isPresent();

        assertEquals(true, isFileExists);

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
            AddINodeDTO node = new AddINodeDTO(2L, 1L, fileName, INodeType.DIR);
            ResponseEntity<INode> newFile = fileSystemController.addInode(node);
            files.add(newFile.getBody());
        }
        Long rootId = 1L;
        INodeDTO root = new INodeDTO(rootId);

        List<INode> children = fileSystemController.getChildren(root).getBody();

        assertEquals(files, children);


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
        Long rootId = 1L;
        assertThrows(Exception.class, () -> {
            fileSystemController.uploadFile(null).getBody();
        });

    }
}
