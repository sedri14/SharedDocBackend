package docSharing.controller;

import docSharing.DTO.FS.AddINodeDTO;
import docSharing.DTO.FS.INodeDTO;
import docSharing.DTO.FS.MoveINodeDTO;
import docSharing.DTO.FS.RenameINodeDTO;
import docSharing.entities.INode;
import docSharing.service.FileSystemService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;

    private static Logger logger = LogManager.getLogger(FileSystemController.class.getName());

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
        logger.info("start addInode function");
        logger.debug("addInode function parameters: userId:%d, name:%s, type:%s, parentId:%d", addINodeDTO.userId, addINodeDTO.name, addINodeDTO.type, addINodeDTO.parentId);
        if (addINodeDTO == null || addINodeDTO.name == null || addINodeDTO.type == null || addINodeDTO.parentId == null || addINodeDTO.userId == null) {
            logger.error("parameters missing");
            throw new IllegalArgumentException("Request unavailable");
        }
        logger.info("In addInode adding %s", addINodeDTO.type);

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
        logger.info("start rename function");
        logger.debug("rename function parameters: name:%s, id:%d", renameINodeDTO.name, renameINodeDTO.id);
        if (renameINodeDTO == null || renameINodeDTO.name == null || renameINodeDTO.id == null) {
            logger.error("parameters missing");
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
        logger.info("start getChildren function");
        logger.debug("getChildren function parameters: id:%d", inodeDTO.id);
        if (inodeDTO == null || inodeDTO.id == null) {
            logger.error("parameters missing");
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
        logger.info("start move function");
        logger.debug("move function parameters: userId:%d, sourceId:%d, targetId:%d", moveINodeDTO.userId, moveINodeDTO.sourceId, moveINodeDTO.targetId);
        if (moveINodeDTO == null || moveINodeDTO.sourceId == null || moveINodeDTO.targetId == null) {
            logger.error("parameters missing");
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
        logger.info("start delete function");
        logger.debug("delete function parameters: id:%d", inodeDTO.id);
        if (inodeDTO == null || inodeDTO.id == null) {
            logger.error("parameters missing");
            throw new IllegalArgumentException("Request unavailable");
        }

        return ResponseEntity.ok(fsService.removeById(inodeDTO.id));
    }

    /**
     * @param fileWithDataDTO contains: parentInodeId - id of parent node
     *                        userId - id of owner user
     *                        file
     * @return a new document identical to the uploaded file
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<INode> uploadFile(@ModelAttribute MoveINodeDTO.FileWithDataDTO fileWithDataDTO) {
        logger.info("start uploadFile function");
        logger.debug("uploadFile function parameters: userId:%d, parentId:%d, filename:%s", fileWithDataDTO.getUserId(), fileWithDataDTO.getParentInodeId(), fileWithDataDTO.getFile().getOriginalFilename());
        System.out.println(fileWithDataDTO);
        if (fileWithDataDTO == null || fileWithDataDTO.getParentInodeId() == null || fileWithDataDTO.getUserId() == null || fileWithDataDTO.getFile() == null) {
            logger.error("parameters missing");
            throw new IllegalArgumentException("Request unavailable");
        }

        Long parentId = fileWithDataDTO.getParentInodeId();
        Long userId = fileWithDataDTO.getUserId();
        MultipartFile file = fileWithDataDTO.getFile();

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!fileExtension.equals("txt")) {
            logger.error("file type is not supported");
            throw new IllegalArgumentException("File type is not supported");
        }

        String content = null;
        try {
            content = new String(file.getBytes());
        } catch (IOException e) {
            logger.error("Can not parse file content: %s", file.getOriginalFilename());
            throw new IllegalArgumentException("Can not parse file content");
        }

        return ResponseEntity.ok(fsService.uploadFile(FilenameUtils.removeExtension(file.getOriginalFilename()), content, parentId, userId));
    }

}
