package docSharing.fileSystem;

import docSharing.requestObjects.ChangeRoleDTO;
import docSharing.requestObjects.FS.MoveINodeDTO;
import docSharing.entities.*;
import docSharing.exceptions.MissingControllerParameterException;
import docSharing.responseObjects.INodeDataResponse;
import docSharing.responseObjects.PathItem;
import docSharing.responseObjects.SharedRoleResponse;
import docSharing.service.SharedRoleService;
import docSharing.user.UserService;
import docSharing.user.User;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.Objects.isNull;
import static org.hibernate.internal.util.StringHelper.isBlank;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/fs")
public class FileSystemController {

    private final FileSystemService fsService;
    private final UserService userService;
    private final SharedRoleService sharedRoleService;
    private static final Logger logger = LogManager.getLogger(FileSystemController.class.getName());
    private static final ModelMapper modelMapper = new ModelMapper();

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INodeResponse> addInode(@RequestBody addINodeRequest inodeRequest, @RequestAttribute User user) {
        logger.info("adding a new inode {} {} by user {}", inodeRequest.getType(), inodeRequest.getName(), user.getEmail());

        if (isBlank(inodeRequest.getName())) {
            throw new MissingControllerParameterException("name");
        }
        if (isNull(inodeRequest.getType())) {
            throw new MissingControllerParameterException("inode type");
        }
        if (isNull(inodeRequest.getParentId())) {
            throw new MissingControllerParameterException("parent id");
        }
        INode inode = fsService.addInode(inodeRequest, user);

        return ResponseEntity.ok(modelMapper.map(inode, INodeResponse.class));
    }

    /**
     * Renames an inode
     *
     * @param inodeRequestAdd contains: id - inode id
     *                 name - inode name
     * @return renamed inode
     */
    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INode> rename(@RequestBody addINodeRequest inodeRequestAdd, @RequestAttribute INode inode) {
        logger.info("start rename inode function");
        logger.debug("rename function parameters: name:{}, id:{}", inodeRequestAdd.getName(), inodeRequestAdd.getParentId());
        if (isBlank(inodeRequestAdd.getName())) {
            throw new MissingControllerParameterException("name");
        }
        if (isNull(inodeRequestAdd.getParentId())) {
            throw new MissingControllerParameterException("id");
        }

        return ResponseEntity.ok(fsService.renameInode(inode, inodeRequestAdd.getName()));
    }

    @RequestMapping(value = "/level/{inodeId}", method = RequestMethod.GET)
    public ResponseEntity<INodeDataResponse> getChildren(@PathVariable Long inodeId, @RequestAttribute INode inode) {
        logger.info("start getChildren function");
        logger.debug("getChildren function parameters: id:%{}", inodeId);

        List<INode> inodes = fsService.getAllChildrenInodes(inode);
        List<INodeResponse> responseINodesList = inodes.stream()
                .map(INodeResponse::fromINode)
                .collect(Collectors.toList());

        List<PathItem> path = fsService.getInodePath(inode);
        INodeDataResponse childrenWithPath = INodeDataResponse.getChildrenWithPath(path, responseINodesList);

        return ResponseEntity.ok(childrenWithPath);
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
    public ResponseEntity<SharedRoleResponse> changeUserRole(@PathVariable Long inodeId, @RequestBody ChangeRoleDTO changeRoleDTO, @RequestAttribute INode inode) {

        logger.info("start changeUserRollInDoc function");

        if (isNull(changeRoleDTO)) {
            throw new MissingControllerParameterException("http request body");
        }
        if (isBlank(changeRoleDTO.email)) {
            throw new MissingControllerParameterException("email");
        }
        if (!changeRoleDTO.isDeleteRole) {
            if (isNull(changeRoleDTO.userRole)) {
                throw new MissingControllerParameterException("user role");
            }
        }

        User user = userService.fetchUserByEmail(changeRoleDTO.email);
        if (changeRoleDTO.isDeleteRole) {
            SharedRole deletedRole = sharedRoleService.deleteRole(inode, user);
            return ResponseEntity.ok(SharedRoleResponse.fromSharedRole(deletedRole));
        }

        SharedRole sharedRole = sharedRoleService.changeUserRole(inode, user, changeRoleDTO.userRole);

        return ResponseEntity.ok(SharedRoleResponse.fromSharedRole(sharedRole));
    }
}
