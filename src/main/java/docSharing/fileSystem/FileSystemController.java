package docSharing.fileSystem;

import docSharing.documentUserAccess.AccessRequest;
import docSharing.entities.Document;
import docSharing.exceptions.MissingControllerParameterException;
import docSharing.documentUserAccess.AccessResponse;
import docSharing.documentUserAccess.AccessService;
import docSharing.user.UserRole;
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
    private final AccessService accessService;
    private static final Logger logger = LogManager.getLogger(FileSystemController.class.getName());
    private static final ModelMapper modelMapper = new ModelMapper();

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INodeResponse> addInode(@RequestBody AddINodeRequest request, @RequestAttribute User user) {
        logger.info("adding a new inode {} {} by user {}", request.getType(), request.getName(), user.getEmail());

        if (isBlank(request.getName())) {
            throw new MissingControllerParameterException("name");
        }
        if (isNull(request.getType())) {
            throw new MissingControllerParameterException("inode type");
        }
        if (isNull(request.getParentId())) {
            throw new MissingControllerParameterException("parent id");
        }
        INode inode = fsService.addInode(request, user);

        return ResponseEntity.ok(modelMapper.map(inode, INodeResponse.class));
    }

    @RequestMapping(value = "/children/{inodeId}", method = RequestMethod.GET)
    public ResponseEntity<ChildrenDataResponse> getChildren(@PathVariable Long inodeId, @RequestAttribute INode inode) {
        logger.info("getting all children of inode {}", inodeId);

        List<INode> inodes = fsService.getAllChildren(inode);
        List<INodeResponse> responseINodesList = inodes.stream()
                .map(item -> modelMapper.map(item, INodeResponse.class))
                .collect(Collectors.toList());
        List<BreadCrumb> path = fsService.getPath(inode);

        return ResponseEntity.ok(ChildrenDataResponse.builder()
                .children(responseINodesList)
                .path(path)
                .build());
    }

    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INodeResponse> rename(@RequestBody  RenameINodeRequest request, @RequestAttribute INode inode) {
        logger.info("rename inode {} to new name {}", inode.getName(), request.getName());

        if (isBlank(request.getName())) {
            throw new MissingControllerParameterException("name");
        }
        INode renamedInode = fsService.rename(inode, request.getName());

        return ResponseEntity.ok(modelMapper.map(renamedInode, INodeResponse.class));
    }

    @RequestMapping(value = "/shared-with-me", method = RequestMethod.GET)
    public ResponseEntity<List<INodeResponse>> getSharedWithMe(@RequestAttribute User user) {
        logger.info("getting all the documents that are shared with user {}", user.getName());

        List<INode> sharedWithMe = accessService.getAllSharedDocumentsWithUser(user);
        List<INodeResponse> responseINodesList = sharedWithMe.stream()
                .map(i -> modelMapper.map(i, INodeResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseINodesList);
    }

    @RequestMapping(value = "/updateAccess", method = RequestMethod.POST)
    public ResponseEntity<AccessResponse> updateUserRole(@RequestBody AccessRequest request, @RequestAttribute Document document, @RequestAttribute User user) {
        logger.info("update user {} with new role {} for document {}", request.getEmail(), request.getUserRole(), document.getId());

        if (isBlank(request.getEmail())) {
            throw new MissingControllerParameterException("email");
        }
        if (!request.isDeleteRole()) {
            if (isNull(request.getUserRole())) {
                throw new MissingControllerParameterException("user role");
            }
        }

        if (request.isDeleteRole()) {
            accessService.deleteRole(document, user);
            return ResponseEntity.ok(AccessResponse.builder()
                    .email(request.getEmail())
                    .role(UserRole.NON)
                    .build());
        }

        var newAccess = accessService.changeUserRole(document, user, request.getUserRole());

        return ResponseEntity.ok(AccessResponse.builder()
                .email(newAccess.getUser().getEmail())
                .role(newAccess.getRole())
                .build());
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
}
