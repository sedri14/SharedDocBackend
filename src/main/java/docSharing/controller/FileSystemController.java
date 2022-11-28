package docSharing.controller;

import docSharing.DTO.*;
import docSharing.entities.INode;
import docSharing.service.AuthService;
import docSharing.service.FileSystemService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upload.FileWithData;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;

    /**
     * Adds an inode
     *
     * @param addINodeDTO - contains: userId - id of owner user
     *                    parentId - id of parent inode
     *                    name - inode name
     *                    type - type of inode (DIR/FILE)
     * @return a new inode
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INode> addInode(@RequestBody AddINodeDTO addINodeDTO) {
        if (addINodeDTO == null) {
            throw new IllegalArgumentException("Request unavailable");
        }
        return ResponseEntity.ok(fsService.addInode(addINodeDTO));
    }

    /**
     * Renames an inode
     *
     * @param renameINodeDTO contains: id - inode id
     *                       name - inode name
     * @return renamed inode
     */
    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INode> rename(@RequestBody RenameINodeDTO renameINodeDTO) {
        if (renameINodeDTO == null || renameINodeDTO.name == null || renameINodeDTO.id == null) {
            throw new IllegalArgumentException("Request unavailable");
        }

        return ResponseEntity.ok(fsService.renameInode(renameINodeDTO.id, renameINodeDTO.name));
    }

    /**
     * Returns all inodes that are direct descendants of an inode
     *
     * @param inodeDTO contains: id - inode id
     * @return a list of inodes
     */
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<List<INode>> getChildren(@RequestBody INodeDTO inodeDTO) {
        if (inodeDTO == null || inodeDTO.id == null) {
            throw new IllegalArgumentException("Request unavailable");
        }

        return ResponseEntity.ok(fsService.getInodesInLevel(inodeDTO.id));
    }

    /**
     * Moves an inode to another inode of type DIR
     *
     * @param moveINodeDTO contains: sourceId - id of an inode that is going to be moved
     *                     targetId - id of an inode that is the new parent
     * @return inode with a new parent
     */
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public ResponseEntity<INode> move(@RequestBody MoveINodeDTO moveINodeDTO) {

        if (moveINodeDTO == null || moveINodeDTO.sourceId == null || moveINodeDTO.targetId == null) {
            throw new IllegalArgumentException("Request unavailable");
        }

        return ResponseEntity.ok(fsService.move(moveINodeDTO.sourceId, moveINodeDTO.targetId));
    }

    /**
     * Deletes an inode and all of its descendants
     *
     * @param inodeDTO contains: id - inode id
     * @return list of inodes deleted
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<List<INode>> delete(@RequestBody INodeDTO inodeDTO) {
        if (inodeDTO == null || inodeDTO.id == null) {
            throw new IllegalArgumentException("Request unavailable");
        }

        return ResponseEntity.ok(fsService.removeById(inodeDTO.id));
    }

    /**
     * @param fileWithData contains: parentInodeId - id of parent node
     *                     userId - id of owner user
     *                     file
     * @return a new document identical to the uploaded file
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<INode> uploadFile(@ModelAttribute FileWithData fileWithData) {
        System.out.println(fileWithData);
        if (fileWithData == null || fileWithData.getParentInodeId() == null || fileWithData.getUserId() == null || fileWithData.getFile() == null) {
            throw new IllegalArgumentException("Request unavailable");
        }

        Long parentId = fileWithData.getParentInodeId();
        Long userId = fileWithData.getUserId();
        MultipartFile file = fileWithData.getFile();

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!fileExtension.equals("txt")) {
            throw new IllegalArgumentException("File type is not supported");
        }

        String content = null;
        try {
            content = new String(file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not parse file content");
        }

        return ResponseEntity.ok(fsService.uploadFile(FilenameUtils.removeExtension(file.getOriginalFilename()), content, parentId, userId));
    }

}
