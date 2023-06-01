package docSharing.controllers;

import docSharing.DTO.ChangeRoleDTO;
import docSharing.DTO.FS.INodeDTO;
import docSharing.DTO.FS.MoveINodeDTO;
import docSharing.entities.INode;
import docSharing.entities.*;
import docSharing.enums.INodeType;
import docSharing.enums.UserRole;
import docSharing.exceptions.MissingControllerParameterException;
import docSharing.response.DocumentResponse;
import docSharing.response.INodeResponse;
import docSharing.service.DocService;
import docSharing.service.FileSystemService;
import docSharing.service.SharedRoleService;
import docSharing.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    DocService docService;

    @Autowired
    SharedRoleService sharedRoleService;

    private static Logger logger = LogManager.getLogger(FileSystemController.class.getName());

    /**
     * Adds an inode
     *
     * @param inodeDTO - contains: userId - id of owner user
     *                 parentId - id of parent inode
     *                 name - inode name
     *                 type - type of inode (DIR/FILE)
     * @return a new inode
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INodeResponse> addInode(@RequestBody INodeDTO inodeDTO, @RequestAttribute User user) {
        logger.info("adding a new inode to by user {}", user.getEmail());
        logger.debug("addInode function parameters: name:{}, type:{}, parentId:{}", inodeDTO.name, inodeDTO.type, inodeDTO.parentId);
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

        if (inode.getType() == INodeType.FILE) {
            Document document = (Document) inode;
            return ResponseEntity.ok(DocumentResponse.fromDocument(document));
        }
        return ResponseEntity.ok(INodeResponse.fromINode(inode));
    }

    /**
     * Renames an inode
     *
     * @param inodeDTO contains: id - inode id
     *                 name - inode name
     * @return renamed inode
     */
    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INode> rename(@RequestBody INodeDTO inodeDTO) {
        logger.info("start rename inode function");
        logger.debug("rename function parameters: name:{}, id:{}", inodeDTO.name, inodeDTO.parentId);
        if (isBlank(inodeDTO.name)) {
            throw new MissingControllerParameterException("name");
        }
        if (isNull(inodeDTO.parentId)) {
            throw new MissingControllerParameterException("id");
        }

        return ResponseEntity.ok(fsService.renameInode(inodeDTO.parentId, inodeDTO.name));
    }

    @RequestMapping(value = "/level/{inodeId}", method = RequestMethod.GET)
    public ResponseEntity<List<INode>> getChildren(@PathVariable Long inodeId) {
        logger.info("start getChildren function");
        logger.debug("getChildren function parameters: id:%{}", inodeId);

        return ResponseEntity.ok(fsService.getAllChildrenInodes(inodeId));
    }

    @RequestMapping(value = "/root", method = RequestMethod.GET)
    public ResponseEntity<List<INodeResponse>> getRoot(@RequestAttribute User user) {
        logger.info("start getRoot function");
        List<INode> inodes = fsService.getRootDirectory(user);
        List<INodeResponse> responseINodesList = inodes.stream()
                .map(INodeResponse::fromINode)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseINodesList);
    }

    @RequestMapping(value = "/shared-with-me", method = RequestMethod.GET)
    public ResponseEntity<List<INodeResponse>> getSharedWithMe(@RequestAttribute User user) {
        logger.info("start getSharedWithMe function");
        List<INode> sharedWithMe = sharedRoleService.getAllSharedFilesWithUser(user);
        List<INodeResponse> responseINodesList = sharedWithMe.stream()
                .map(INodeResponse::fromINode)
                .collect(Collectors.toList());


        return ResponseEntity.ok(responseINodesList);
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
    public ResponseEntity<SharedRole> changeUserRole(@PathVariable Long inodeId, @RequestBody ChangeRoleDTO changeRoleDTO) {

        logger.info("start changeUserRollInDoc function");

        if (isNull(changeRoleDTO)) {
            throw new MissingControllerParameterException("http request body");
        }
        if (isBlank(changeRoleDTO.email)) {
            throw new MissingControllerParameterException("email");
        }
        if (isNull(changeRoleDTO.userRole)) {
            throw new MissingControllerParameterException("user role");
        }

        INode inode = fsService.fetchINodeById(inodeId);
        User user = userService.findByEmail(changeRoleDTO.email);
        if (changeRoleDTO.isDeleteRole) {
            return ResponseEntity.ok(sharedRoleService.deleteRole(inode, user));
        }

        return ResponseEntity.ok(sharedRoleService.changeUserRole(inode, user, changeRoleDTO.userRole));
    }
}
