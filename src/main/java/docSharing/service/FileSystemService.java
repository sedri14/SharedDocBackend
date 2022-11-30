package docSharing.service;

import com.mysql.cj.jdbc.exceptions.OperationNotSupportedException;
import docSharing.DTO.AddINodeDTO;
import docSharing.entities.*;
import docSharing.repository.FileSystemRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileSystemService {

    @Autowired
    private FileSystemRepository fsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DocService docService;

    /**
     * Returns all direct children of an inode
     *
     * @param id the parent inode id
     * @return A List of children inodes
     */
    public List<INode> getInodesInLevel(Long id) {
        //validation

        return fsRepository.findByParentId(id);
    }

//    public List<INode> findAll() {
//        return fsRepository.findAll();
//    }

    /**
     * add a new inode of type DIR or FILE to the filesystem.
     *
     * @param addInode include: owner id - owner of the inode,
     *                 parent id - added inode's parent,
     *                 name - name of inode
     *                 type - type of inode (DIR/FILE)
     * @return added inode
     */
    public INode addInode(AddINodeDTO addInode) {
        if (inodeNameExistsInDir(addInode.parentId, addInode.type, addInode.name)) {
            throw new RuntimeException(String.format("Can not add %s, Name %s already exists in this directory.",
                    addInode.type == INodeType.DIR ? "directory" : "file", addInode.name));
        }

        INode parentInode = fsRepository.findById(addInode.parentId).get();
        if (!isDir(parentInode)) {
            throw new RuntimeException("Destination to add must be a directory");
        }
        User owner = userService.getById(addInode.userId);
        INode newInode;
        switch (addInode.type) {
            case DIR:
                newInode = INode.createNewDirectory(addInode.name, parentInode);
                break;
            case FILE:
                newInode = Document.createNewEmptyDocument(addInode.name, parentInode, owner);
                break;
            default:
                throw new IllegalArgumentException("Illegal Inode type");
        }

        INode savedInode = fsRepository.save(newInode);
        if (addInode.type == INodeType.FILE) {
            Long docId = savedInode.getId();
            docService.setPermission(addInode.userId, docId, UserRole.EDITOR);
        }


        return savedInode;
    }


    /**
     * Sets inode with targetInodeId to be the parent of inode with inodeId.
     *
     * @param sourceId - id of the inode to move
     * @param targetId - id of the new parent inode
     * @return the moved inode
     */
    public INode move(Long sourceId, Long targetId) {
        if (!isDir(targetId)) {
            throw new IllegalArgumentException("Destination of move must be a directory");
        }

        if (!isHierarchicallyLegalMove(sourceId, targetId)) {
            throw new RuntimeException("Can't move an ancestor directory to one of its descendants");
        }

        INode inodeToMove = fsRepository.getReferenceById(sourceId);
        INode targetINode = fsRepository.getReferenceById(targetId);

        if (inodeToMove == null || targetINode == null) {
            throw new RuntimeException("Inode not found");
        }

        INodeType sourceType = inodeToMove.getType();
        if (inodeNameExistsInDir(targetId, sourceType, inodeToMove.getName())) {
            throw new RuntimeException(String.format("Can not move %s. the name \"%s\" already exists in target directory.",
                    sourceType == INodeType.DIR ? "directory" : "file", inodeToMove.getName()));
        }
        inodeToMove.setParent(targetINode);

        return fsRepository.save(inodeToMove);
    }

    /**
     * Checks if an inode is of type DIR
     *
     * @param inode
     * @return true of false
     */
    public boolean isDir(INode inode) {
        return inode.getType() == INodeType.DIR;
    }


    /**
     * Checks if an inode is of type DIR
     *
     * @param inodeId - id of an inode
     * @return true of false
     */
    public boolean isDir(Long inodeId) {
        INode inode = fsRepository.findById(inodeId).get();
        return inode.getType() == INodeType.DIR;
    }

    /**
     * Removes an inode and all its descendants
     *
     * @param id - inode id
     * @return list of inodes removed
     */
    public List<INode> removeById(Long id) {
        return fsRepository.removeById(id);
    }

    /**
     * Sets a new name to an inode
     *
     * @param id   - inode id
     * @param name - new name
     * @return the renamed inode
     */
    public INode renameInode(Long id, String name) {
        INode inode = fsRepository.findById(id).get();
        Long parentId = inode.getParent().getId();

        if (fileNameExistsInDir(parentId, name)) {
            throw new RuntimeException(String.format("File name %s already exist in this directory", name));
        }
        inode.setName(name);

        return fsRepository.save(inode);
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

    /**
     * Checks if an inode with sourceId is a descendant of inode with targetId
     *
     * @param sourceId - inode id
     * @param targetId - inode id
     * @return true or false
     */
    //TODO: this doesn't work
    public boolean isHierarchicallyLegalMove(Long sourceId, Long targetId) {
        INode iNode = fsRepository.findById(sourceId).get();
        return true;

    }

    /**
     * Creates a new inode of type FILE (document)
     *
     * @param nameWithExtension - file name
     * @param content           - content of the file
     * @param parentId          - parent node id under which the created document will be assigned
     * @param userId            - id of the owner user
     * @return a new Document inode created from the uploaded .txt file
     */
    public Document uploadFile(String nameWithExtension, String content, Long parentId, Long userId) {

        INode parent = fsRepository.findById(parentId).get();
        if (!isDir(parent)) {
            throw new RuntimeException("Files can be imported only to directory");
        }
        if (fileNameExistsInDir(parentId, nameWithExtension)) {
            throw new RuntimeException(String.format("File name %s already exist in this directory", FilenameUtils.removeExtension(nameWithExtension)));
        }

        User owner = userService.getById(userId);
        Document newDoc = Document.createNewImportedDocument(nameWithExtension, content, parent, owner);
        Document savedDoc = fsRepository.save(newDoc);
        docService.setPermission(userId, savedDoc.getId(), UserRole.EDITOR);

        return newDoc;
    }

    /**
     * Checks if there already is an inode of same type (DIR/FILE) with the same name in a specific directory.
     *
     * @param parentId - the id of the directory
     * @param type     - type of inode
     * @param name     - inode name
     * @return true or false
     */

    public boolean inodeNameExistsInDir(Long parentId, INodeType type, String name) {
        boolean isNameExists;
        switch (type) {
            case DIR:
                isNameExists = dirNameExistsInDir(parentId, name);
                break;
            case FILE:
                isNameExists = fileNameExistsInDir(parentId, name);
                break;
            default:
                throw new IllegalArgumentException("Inode type not supported");
        }

        return isNameExists;
    }


    /**
     * Checks if a file name already exists in a specific directory.
     *
     * @param parentId id of directory
     * @param fileName name of file
     * @return true or false
     */
    public boolean fileNameExistsInDir(Long parentId, String fileName) {
        Set<INode> allFilesInParentInode = fsRepository.findByParentIdAndTypeEquals(parentId, INodeType.FILE);
        Set<String> names = allFilesInParentInode.stream().map(INode::getName).collect(Collectors.toSet());

        return names.contains(fileName);
    }

    /**
     * Checks if a directory name already exists in a specific directory.
     *
     * @param parentId id of directory
     * @param dirName  name of directory
     * @return true or false
     */
    private boolean dirNameExistsInDir(Long parentId, String dirName) {
        Set<INode> allDirsInParentInode = fsRepository.findByParentIdAndTypeEquals(parentId, INodeType.DIR);
        Set<String> names = allDirsInParentInode.stream().map(INode::getName).collect(Collectors.toSet());

        return names.contains(dirName);
    }
}