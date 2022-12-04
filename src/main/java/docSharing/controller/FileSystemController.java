package docSharing.controller;

import docSharing.DTO.FS.AddINodeDTO;
import docSharing.DTO.FS.INodeDTO;
import docSharing.DTO.FS.MoveINodeDTO;
import docSharing.DTO.FS.RenameINodeDTO;
import docSharing.Utils.Validation;
import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.*;
import docSharing.response.Response;
import docSharing.service.FileSystemService;
import docSharing.service.PermissionService;
import docSharing.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Response<INode>> addInode(@RequestBody AddINodeDTO addINodeDTO) {
        logger.info("start addInode function");
        logger.debug("addInode function parameters: userId:{}, name:{}, type:{}, parentId:{}", addINodeDTO.userId, addINodeDTO.name, addINodeDTO.type, addINodeDTO.parentId);
        Validation.nullCheck(addINodeDTO);
        Validation.nullCheck(addINodeDTO.name);
        Validation.nullCheck(addINodeDTO.type);
        Validation.nullCheck(addINodeDTO.parentId);
        Validation.nullCheck(addINodeDTO.userId);
        logger.info("In addInode adding type:{}", addINodeDTO.type);

        logger.info("find the owner");
        User owner = userService.getById(addINodeDTO.userId);

        INode inode;
        try {
            inode = fsService.addInode(addINodeDTO, owner);
            if (addINodeDTO.type == INodeType.FILE) {
                permissionService.setPermission(new Permission(owner, (Document) inode, UserRole.EDITOR));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(inode));
    }

    /**
     * Renames an inode
     *
     * @param renameINodeDTO contains: id - inode id
     *                       name - inode name
     * @return renamed inode
     */
    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<Response<INode>> rename(@RequestBody RenameINodeDTO renameINodeDTO) {
        logger.info("start rename function");
        logger.debug("rename function parameters: name:{}, id:{}", renameINodeDTO.name, renameINodeDTO.id);
        Validation.nullCheck(renameINodeDTO);
        Validation.nullCheck(renameINodeDTO.name);
        Validation.nullCheck(renameINodeDTO.id);

        INode renamedInode;
        try {
            renamedInode = fsService.renameInode(renameINodeDTO.id, renameINodeDTO.name);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(renamedInode));
    }

    /**
     * Returns all inodes that are direct descendants of an inode
     *
     * @param inodeDTO contains: id - inode id
     * @return a list of inodes
     */
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<Response<List<INode>>> getChildren(@RequestBody INodeDTO inodeDTO) {
        logger.info("start getChildren function");
        logger.debug("getChildren function parameters: id:%{}", inodeDTO.id);
        Validation.nullCheck(inodeDTO);
        Validation.nullCheck(inodeDTO.id);

        List<INode> inodesInLevel;
        try {
            inodesInLevel = fsService.getAllChildrenInodes(inodeDTO.id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(inodesInLevel));
    }

    /**
     * Moves an inode to another inode of type DIR
     *
     * @param moveINodeDTO contains: sourceId - id of an inode that is going to be moved
     *                     targetId - id of an inode that is the new parent
     * @return inode with a new parent
     */
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public ResponseEntity<Response<INode>> move(@RequestBody MoveINodeDTO moveINodeDTO) {
        logger.info("start move function");
        logger.debug("move function parameters: userId:{}, sourceId:{}, targetId:{}", moveINodeDTO.userId, moveINodeDTO.sourceId, moveINodeDTO.targetId);
        Validation.nullCheck(moveINodeDTO);
        Validation.nullCheck(moveINodeDTO.sourceId);
        Validation.nullCheck(moveINodeDTO.targetId);

        INode movedInode;
        try {
            movedInode = fsService.move(moveINodeDTO.sourceId, moveINodeDTO.targetId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(movedInode));
    }

    /**
     * Deletes an inode and all of its descendants
     *
     * @param inodeDTO contains: id - inode id
     * @return list of inodes deleted
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Response<List<INode>>> delete(@RequestBody INodeDTO inodeDTO) {
        logger.info("start delete function");
        logger.debug("delete function parameters: id:{}", inodeDTO.id);
        Validation.nullCheck(inodeDTO);
        Validation.nullCheck(inodeDTO.id);

        List<INode> deletedInode;
        try {
            deletedInode = fsService.removeById(inodeDTO.id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(deletedInode));
    }

    /**
     * @param fileWithDataDTO contains: parentInodeId - id of parent node
     *                        userId - id of owner user
     *                        file
     * @return a new document identical to the uploaded file
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<Response<INode>> uploadFile(@ModelAttribute MoveINodeDTO.FileWithDataDTO fileWithDataDTO) {
        logger.info("start uploadFile function");
        logger.debug("uploadFile function parameters: userId:{}, parentId:{}, filename:{}", fileWithDataDTO.getUserId(), fileWithDataDTO.getParentInodeId(), fileWithDataDTO.getFile().getOriginalFilename());
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
            return ResponseEntity.badRequest().body(Response.failure("file type is not supported"));
        }

        logger.info("find the owner");
        User owner = userService.getById(userId);

        Document importedDoc;
        try {
            logger.info("");
            importedDoc = fsService.uploadFile(file, parentId, owner);
            permissionService.setPermission(new Permission(owner, importedDoc, UserRole.EDITOR));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(importedDoc));

    }
}
