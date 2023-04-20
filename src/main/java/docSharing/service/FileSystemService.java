package docSharing.service;

import com.google.gson.internal.Streams;
import docSharing.DTO.FS.INodeDTO;
import docSharing.entities.*;
import docSharing.enums.INodeType;
import docSharing.enums.UserRole;
import docSharing.exceptions.INodeNameExistsException;
import docSharing.exceptions.INodeNotFoundException;
import docSharing.exceptions.IllegalOperationException;
import docSharing.repository.FileSystemRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileSystemService {

    @Autowired
    private FileSystemRepository fsRepository;


    private static Logger logger = LogManager.getLogger(FileSystemService.class.getName());


    public INode fetchINodeById(Long id) {
        return fsRepository.findById(id).orElseThrow(() -> new INodeNotFoundException("INode not found with id " + id));
    }

    /**
     * Returns all children of an inode of type directory
     *
     * @param id the parent inode id
     * @return A List of children inodes
     */
    public List<INode> getAllChildrenInodes(Long id) {
        INode parent = fetchINodeById(id);
        if (parent.getType() != INodeType.DIR) {
            throw new IllegalOperationException("INode must be a directory");
        }

        return parent.getChildren().values().stream().collect(Collectors.toList());
    }

    /**
     * add a new inode of type DIR or FILE to the filesystem.
     *
     * @param addInode include: owner id - owner of the inode,
     *                 parent id - added inode's parent,
     *                 name - name of inode
     *                 type - type of inode (DIR/FILE)
     * @return added inode
     */
    public INode addInode(INodeDTO addInode, User owner) {
        if (inodeNameExistsInDir(addInode.parentId, addInode.type, addInode.name)) {
            throw new INodeNameExistsException(String.format("Can not add %s, Name %s already exists in this directory.",
                    addInode.type == INodeType.DIR ? "directory" : "file", addInode.name));
        }

        INode parent = fetchINodeById(addInode.parentId);
        if (!isDir(parent)) {
            throw new IllegalOperationException("Destination to add must be a directory");
        }

        INode newInode;
        switch (addInode.type) {
            case DIR:
                newInode = INode.createNewDirectory(addInode.name, parent, owner);
                break;
            case FILE:
                newInode = Document.createNewEmptyDocument(addInode.name, parent, owner);
                break;
            default:
                throw new IllegalOperationException("Illegal Inode type");
        }
        parent.getChildren().put(addInode.name, newInode);
        fsRepository.save(parent);

        return parent.getChildren().get(addInode.name);
    }


    /**
     * Sets inode with targetInodeId to be the parent of inode with inodeId.
     *
     * @param sourceId - id of the inode to move
     * @param targetId - id of the new parent inode
     * @return the moved inode
     */
    public INode move(Long sourceId, Long targetId) {
        INode sourceInode = fetchINodeById(sourceId);
        INode targetInode = fetchINodeById(targetId);

        if (!isDir(targetInode)) {
            throw new IllegalOperationException("Destination of move must be a directory");
        }

        INodeType sourceType = sourceInode.getType();
        if (inodeNameExistsInDir(targetId, sourceType, sourceInode.getName())) {
            throw new IllegalOperationException(String.format("Can not move %s. the name \"%s\" already exists in target directory.",
                    sourceType == INodeType.DIR ? "directory" : "file", sourceInode.getName()));
        }

        if (!isHierarchicallyLegalMove(sourceInode, targetInode)) {
            throw new IllegalOperationException("Illegal move");

        }

        sourceInode.setParent(targetInode);

        return fsRepository.save(sourceInode);
    }


    //check if source node is an ancestor of target node
    private boolean isHierarchicallyLegalMove(INode sourceInode, INode targetInode) {
        INode current = targetInode;
        while (null != current.getParent()) {
            if (current.equals(sourceInode)) {
                return false;
            }
            current = current.getParent();
        }

        return true;
    }

    /**
     * Checks if an inode is of type DIR
     *
     * @param inode
     * @return true of false
     */
    public boolean isDir(INode inode) {
        return inode.getType().equals(INodeType.DIR);
    }

    /**
     * Removes an inode and all its descendants
     *
     * @param id - inode id
     * @return list of inodes removed
     */
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

        return fsRepository.removeById(id);
    }

    /**
     * Sets a new name to an inode
     *
     * @param id      - inode id
     * @param newName - new name
     * @return the renamed inode
     */
    public INode renameInode(Long id, String newName) {
        INode inode = fetchINodeById(id);
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

    /**
     * Checks if there is an inode with inodeId in DB
     *
     * @param inodeId
     * @return true or false
     */
    public boolean isExist(Long inodeId) {
        return fsRepository.existsById(inodeId);
    }

//    /**
//     * Creates a new inode of type FILE (document)
//     *
//     * @param file     - uploaded file
//     * @param parentId - parent node id under which the created document will be assigned
//     * @param owner    - owner User
//     * @return a new Document inode created from the uploaded .txt file
//     */
//    public Document uploadFile(MultipartFile file, Long parentId, User owner) throws IllegalArgumentException {
//        String nameWithExtension = FilenameUtils.removeExtension(file.getOriginalFilename());
//        String content = null;
//        try {
//            content = new String(file.getBytes());
//        } catch (IOException e) {
//            logger.error("Can not parse file content: %{}", file.getOriginalFilename());
//            throw new IllegalArgumentException("Can not parse file content");
//        }
//
//        INode parent = fsRepository.findById(parentId).get();
//        if (!isDir(parent)) {
//            throw new IllegalArgumentException("Files can be imported only to directory");
//        }
//        if (isFileNameExistsInDir(parentId, nameWithExtension)) {
//            throw new IllegalArgumentException(String.format("File name %s already exist in this directory", FilenameUtils.removeExtension(nameWithExtension)));
//        }
//
//        Document newDoc = Document.createNewImportedDocument(nameWithExtension, content, parent, owner);
//        Document savedDoc = fsRepository.save(newDoc);
//
//        return newDoc;
//    }
//
//    /**
//     * Checks if there already is an inode of same type (DIR/FILE) with the same name in a specific directory.
//     *
//     * @param parentId - the id of the directory
//     * @param type     - type of inode
//     * @param name     - inode name
//     * @return true or false
//     */

    public boolean inodeNameExistsInDir(Long parentId, INodeType type, String name) {
        INode parent = fetchINodeById(parentId);
        boolean isNameExists;
        switch (type) {
            case DIR:
                isNameExists = isDirNameExistsInDir(parent, name);
                break;
            case FILE:
                isNameExists = isFileNameExistsInDir(parent, name);
                break;
            default:
                throw new IllegalArgumentException("Inode type not supported");
        }

        return isNameExists;
    }


    /**
     * Checks if a file name already exists in a specific directory.
     *
     * @param parent
     * @param fileName name of file
     * @return true or false
     */
    public boolean isFileNameExistsInDir(INode parent, String fileName) {
        INode file = parent.getChildren().get(fileName);
        if (null == file || file.getType() == INodeType.DIR) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if a directory name already exists in a specific directory.
     *
     * @param parent
     * @param dirName name of directory
     * @return true or false
     */
    private boolean isDirNameExistsInDir(INode parent, String dirName) {
        INode dir = parent.getChildren().get(dirName);
        if (null == dir || dir.getType() == INodeType.FILE) {
            return false;
        } else {
            return true;
        }
    }

    public UserRole changeUserRole(INode inode, User user, UserRole userRole) {

        if (userRole == UserRole.NON) {
            inode.getRoles().remove(user);
        } else {
            inode.getRoles().put(user, userRole);
        }
        fsRepository.save(inode);

        return userRole;
    }
}