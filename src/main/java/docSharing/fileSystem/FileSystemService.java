package docSharing.fileSystem;

import docSharing.enums.INodeType;
import docSharing.exceptions.INodeNameExistsException;
import docSharing.exceptions.INodeNotFoundException;
import docSharing.exceptions.IllegalOperationException;
import docSharing.user.User;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FileSystemService {

    private final FileSystemRepository fsRepository;

    private static final Logger logger = LogManager.getLogger(FileSystemService.class.getName());

    /**
     * Adds a new INode (document or directory) to the file system.
     *
     * @param inodeRequest The request object containing information about the new INode.
     * @param owner        The user who owns the newly created INode.
     * @return The newly created INode.
     * @throws IllegalOperationException If the destination to add the INode is not a directory.
     * @throws INodeNameExistsException  If an INode with the same name already exists in the destination directory.
     */
    INode addInode(addINodeRequest inodeRequest, User owner) {
        INode parent = fetchINodeById(inodeRequest.getParentId());
        if (!isDirectory(parent)) {
            throw new IllegalOperationException("Destination to add must be a directory");
        }

        if (inodeNameExistsInDir(inodeRequest.getParentId(), inodeRequest.getType(), inodeRequest.getName())) {
            throw new INodeNameExistsException(String.format("Can not add %s, Name %s already exists in this directory.",
                    inodeRequest.getType() == INodeType.DIR ? "directory" : "file", inodeRequest.getName()));
        }

        INode newINode = switch (inodeRequest.getType()) {
            case DIR -> INodeFactory.createNewDirectory(inodeRequest.getName(), parent, owner);
            case FILE -> INodeFactory.createNewEmptyDocument(inodeRequest.getName(), parent, owner);
        };
        parent.getChildren().put(inodeRequest.getName(), newINode);
        fsRepository.save(parent);

        return parent.getChildren().get(inodeRequest.getName());
    }

    /**
     * Retrieves all children nodes of a given parent node.
     *
     * @param parent The parent node.
     * @return A List of INode objects.
     * @throws IllegalOperationException Thrown if the provided parent node is not of type INodeType.DIR (directory).
     */

    List<INode> getAllChildren(INode parent) {
        if (parent.getType() != INodeType.DIR) {
            throw new IllegalOperationException("INode must be a directory");
        }

        return new ArrayList<>(parent.getChildren().values());
    }

    /**
     * Retrieves the path from the root directory to the specified INode, represented as a list of breadcrumbs.
     *
     * @param inode The INode for which to retrieve the path.
     * @return A List of BreadCrumb objects representing the path from the root directory to the specified INode.
     *         If the provided INode is the root directory or null, an empty list is returned.
     */
    List<BreadCrumb> getPath(INode inode) {
        List<BreadCrumb> path = new ArrayList<>();

        // root directory
        if (null == inode.getParent()) return path;

        INode root = inode.getOwner().getRootDirectory();
        INode current = inode.getParent();
        while (!Objects.equals(current.getId(), root.getId())) {
            path.add(BreadCrumb.builder()
                    .id(current.getId())
                    .name(current.getName())
                    .build());
            current = current.getParent();
        }
        Collections.reverse(path);

        return path;
    }


    public INode fetchINodeById(Long id) {
        return fsRepository.findById(id).orElseThrow(() -> new INodeNotFoundException("INode not found with id " + id));
    }

    public INode removeById(Long id) {
        INode inode;
        try {
            inode = fetchINodeById(id);
        } catch (INodeNotFoundException ex) {
            throw new IllegalOperationException("can not delete non existing inode");
        }

        //protect root node
        if (null == inode.getParent()) {
            throw new IllegalOperationException("can not remove root directory");
        }

        //delete from parent map
        INode parent = inode.getParent();
        parent.getChildren().remove(inode.getName());
        fsRepository.save(parent);

        return fsRepository.removeById(id).get();
    }

    public INode renameInode(INode inode, String newName) {
        Long parentId = inode.getParent().getId();
        INode parent = inode.getParent();
        INodeType inodeType = inode.getType();

        if (inodeNameExistsInDir(parentId, inodeType, newName)) {
            throw new IllegalOperationException(String.format("%s name \"%s\" already exists in this directory", inodeType == INodeType.DIR ? "directory" : "file", newName));
        }
        //reinsert child entry with new name to parent map
        INode removedInode = parent.getChildren().remove(inode.getName());
        parent.getChildren().put(newName, removedInode);
        inode.setName(newName);
        fsRepository.save(parent);

        return parent.getChildren().get(newName);
    }

    public List<INode> getRootDirectory(User user) {
        return new ArrayList<>(user.getRootDirectory().getChildren().values());
    }

    /* Helper Methods */

    private boolean isDirectory(INode inode) {
        return inode.getType().equals(INodeType.DIR);
    }

    private boolean inodeNameExistsInDir(Long parentId, INodeType type, String name) {
        INode parent = fetchINodeById(parentId);

        return switch (type) {
            case DIR -> isDirNameExistsInDir(parent, name);
            case FILE -> isDocumentNameExistsInDir(parent, name);
        };
    }

    private boolean isDocumentNameExistsInDir(INode parent, String docName) {
        INode doc = parent.getChildren().get(docName);
        return !(null == doc || doc.getType() == INodeType.DIR);
    }

    private boolean isDirNameExistsInDir(INode parent, String dirName) {
        INode dir = parent.getChildren().get(dirName);
        return !(null == dir || dir.getType() == INodeType.FILE);
    }
}