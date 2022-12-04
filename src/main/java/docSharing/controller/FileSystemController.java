package docSharing.controller;

import docSharing.DTO.FS.AddINodeDTO;
import docSharing.DTO.FS.INodeDTO;
import docSharing.DTO.FS.MoveINodeDTO;
import docSharing.DTO.FS.RenameINodeDTO;
import docSharing.Utils.Validation;
import docSharing.entities.INode;
import docSharing.entities.*;
import docSharing.service.FileSystemService;
import docSharing.service.PermissionService;
import docSharing.service.UserService;
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
    @Autowired
    UserService userService;
    @Autowired
    PermissionService permissionService;

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
        Validation.nullCheck(addINodeDTO);
        Validation.nullCheck(addINodeDTO.name);
        Validation.nullCheck(addINodeDTO.type);
        Validation.nullCheck(addINodeDTO.parentId);
        Validation.nullCheck(addINodeDTO.userId);


        logger.info("In addInode adding %s", addINodeDTO.type);

        logger.info("find the owner");
        User owner = userService.getById(addINodeDTO.userId);

        INode iNode = fsService.addInode(addINodeDTO, owner);

        if (addINodeDTO.type == INodeType.FILE) {
            permissionService.setPermission(new Permission(owner, (Document) iNode, UserRole.EDITOR));
        }

        return ResponseEntity.ok(iNode);
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
        Validation.nullCheck(renameINodeDTO);
        Validation.nullCheck(renameINodeDTO.name);
        Validation.nullCheck(renameINodeDTO.id);

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
        Validation.nullCheck(inodeDTO);
        Validation.nullCheck(inodeDTO.id);

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
        Validation.nullCheck(moveINodeDTO);
        Validation.nullCheck(moveINodeDTO.sourceId);
        Validation.nullCheck(moveINodeDTO.targetId);

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
        Validation.nullCheck(inodeDTO);
        Validation.nullCheck(inodeDTO.id);

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
        Validation.nullCheck(fileWithDataDTO);
        Validation.nullCheck(fileWithDataDTO.getParentInodeId());
        Validation.nullCheck(fileWithDataDTO.getUserId());
        Validation.nullCheck(fileWithDataDTO.getFile());

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

        logger.info("find the owner");
        User owner = userService.getById(userId);

        Document doc = fsService.uploadFile(FilenameUtils.removeExtension(file.getOriginalFilename()), content, parentId, owner);
        permissionService.setPermission(new Permission(owner, doc, UserRole.EDITOR));
        return ResponseEntity.ok(doc);
    }

}
