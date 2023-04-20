package docSharing.controllers;

import docSharing.DTO.ChangeRoleDTO;
import docSharing.DTO.FS.INodeDTO;
import docSharing.DTO.FS.MoveINodeDTO;
import docSharing.entities.INode;
import docSharing.entities.*;
import docSharing.enums.UserRole;
import docSharing.exceptions.MissingControllerParameterException;
import docSharing.service.FileSystemService;
import docSharing.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static java.util.Objects.isNull;
import static org.hibernate.internal.util.StringHelper.isBlank;

@RestController
@CrossOrigin
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;
    @Autowired
    UserService userService;

    private static Logger logger = LogManager.getLogger(FileSystemController.class.getName());

    /**
     * Adds an inode
     *
     * @param inodeDTO - contains: userId - id of owner user
     *                    parentId - id of parent inode
     *                    name - inode name
     *                    type - type of inode (DIR/FILE)
     * @param user
     * @return a new inode
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INode> addInode(@RequestBody INodeDTO inodeDTO, @RequestAttribute User user) {
        logger.info("start addInode function");
        logger.debug("addInode function parameters: name:{}, type:{}, parentId:{}",  inodeDTO.name, inodeDTO.type, inodeDTO.parentId);
        if (isNull(inodeDTO)) {
            throw new MissingControllerParameterException("http request body");
        }
        if (isBlank(inodeDTO.name)) {
            throw new MissingControllerParameterException("name");
        }
        if (isNull(inodeDTO.type)) {
            throw new MissingControllerParameterException("inode type");
        }
        if (isNull(inodeDTO.parentId)) {
            throw new MissingControllerParameterException("parent id");
        }
        INode inode = fsService.addInode(inodeDTO, user);

        return ResponseEntity.ok(inode);
    }

    /**
     * Renames an inode
     *
     * @param inodeDTO contains: id - inode id
     *                       name - inode name
     * @return renamed inode
     */
    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INode> rename(@RequestBody INodeDTO inodeDTO) {
        logger.info("start rename inode function");
        logger.debug("rename function parameters: name:{}, id:{}", inodeDTO.name, inodeDTO.parentId);
        if (isNull(inodeDTO)) {
            throw new MissingControllerParameterException("http request body");
        }
        if (isBlank(inodeDTO.name)) {
            throw new MissingControllerParameterException("name");
        }
        if (isNull(inodeDTO.parentId)) {
            throw new MissingControllerParameterException("id");
        }

        return ResponseEntity.ok(fsService.renameInode(inodeDTO.parentId, inodeDTO.name));
    }

    /**
     * Returns all inodes that are direct descendants of an inode
     *
     * @return a list of inodes
     */
    @RequestMapping(value = "/level/{inodeId}", method = RequestMethod.GET)
    public ResponseEntity<List<INode>> getChildren(@PathVariable Long inodeId) {
        logger.info("start getChildren function");
        logger.debug("getChildren function parameters: id:%{}", inodeId);

        return ResponseEntity.ok(fsService.getAllChildrenInodes(inodeId));
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
        logger.info("start move inode function");
        logger.debug("move function parameters: sourceId:{}, targetId:{}", moveINodeDTO.sourceId, moveINodeDTO.targetId);
        if (isNull(moveINodeDTO)) {
            throw new MissingControllerParameterException("http request body");
        }
        if (isNull(moveINodeDTO.sourceId) || isNull(moveINodeDTO.targetId)) {
            throw new MissingControllerParameterException("source or target inode");
        }

        return ResponseEntity.ok(fsService.move(moveINodeDTO.sourceId, moveINodeDTO.targetId));
    }

    /**
     * Deletes an inode and all of its descendants
     *
     * @return number of inodes deleted
     */
    @RequestMapping(value = "/delete/{inodeId}", method = RequestMethod.DELETE)
    public ResponseEntity<INode> delete(@PathVariable Long inodeId) {
        logger.info("start delete function");
        logger.debug("delete function parameters: id:{}", (inodeId));

        return ResponseEntity.ok(fsService.removeById(inodeId));
    }

    /**
     * @param changeRoleDTO Param to change the role of user
     * @return if the change is done or note
     */
    @RequestMapping(value = "changeUserRole/{inodeId}", method = RequestMethod.POST)
    public ResponseEntity<UserRole> changeUserRole(@PathVariable Long inodeId, @RequestBody ChangeRoleDTO changeRoleDTO) {

        logger.info("start changeUserRollInDoc function");

        if (isNull(changeRoleDTO)){
            throw new MissingControllerParameterException("http request body");
        }
        if (isBlank(changeRoleDTO.email)) {
            throw new MissingControllerParameterException("email");
        }
        if (isNull(changeRoleDTO.userRole)){
            throw new MissingControllerParameterException("user role");
        }

        INode inode = fsService.fetchINodeById(inodeId);
        User user = userService.findByEmail(changeRoleDTO.email);
        UserRole userRole = (true == changeRoleDTO.isDeleteRole) ? UserRole.NON : changeRoleDTO.userRole;

        return ResponseEntity.ok(fsService.changeUserRole(inode, user, userRole));
    }


//    /**
//     * @param fileWithDataDTO contains: parentInodeId - id of parent node
//     *                        userId - id of owner user
//     *                        file
//     * @param token token
//     * @param userId userid
//     * @return a new document identical to the uploaded file
//     */
//    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
//    public ResponseEntity<Response<INode>> uploadFile(@ModelAttribute FileWithDataDTO fileWithDataDTO, @RequestHeader String token, @RequestHeader Long userId) {
//        logger.info("start uploadFile function");
//
//        if (!authService.isValidToken(userId, token)) {
//            return ResponseEntity.badRequest().body(Response.failure(TokenError.INVALID_TOKEN.toString()));
//        }
//        logger.debug("uploadFile function parameters: userId:{}, parentId:{}, filename:{}", fileWithDataDTO.getUserId(), fileWithDataDTO.getParentInodeId(), fileWithDataDTO.getFile().getOriginalFilename());
//        Validation.nullCheck(fileWithDataDTO);
//        Validation.nullCheck(fileWithDataDTO.getParentInodeId());
//        Validation.nullCheck(fileWithDataDTO.getUserId());
//        Validation.nullCheck(fileWithDataDTO.getFile());
//
//        Long parentId = fileWithDataDTO.getParentInodeId();
//        MultipartFile file = fileWithDataDTO.getFile();
//
//        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
//        if (!fileExtension.equals("txt")) {
//            logger.error("file type is not supported");
//            return ResponseEntity.badRequest().body(Response.failure("file type is not supported"));
//        }
//
//        logger.info("find the owner");
//        User owner = userService.getById(fileWithDataDTO.getUserId());
//
//        Document importedDoc;
//        try {
//            logger.info("");
//            importedDoc = fsService.uploadFile(file, parentId, owner);
//            permissionService.setPermission(new Permission(owner, importedDoc, UserRole.EDITOR));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Response.failure(e.getMessage()));
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(Response.success(importedDoc));
//
//    }
}
