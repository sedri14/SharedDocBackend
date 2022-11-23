package docSharing.service;

import docSharing.DTO.AddINodeDTO;
import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.INodeType;
import docSharing.entities.User;
import docSharing.repository.FileSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FileSystemService {

    @Autowired
    private FileSystemRepository fsRepository;

    @Autowired
    private UserService userService;

    public List<INode> getInodesInLevel(Long id) {
        //validation

        return fsRepository.findByParentId(id);
    }

    public List<INode> findAll(){
        return fsRepository.findAll();
    }

    public INode getById(Long id) {

        INode inode = null;
        try {
            inode = fsRepository.getReferenceById(id);
        } catch (EntityNotFoundException e){
            throw new IllegalArgumentException("INode does not exist");
        }

        return inode;
    }

    public INode addInode(AddINodeDTO addInode) {
        //validation

        //DTO -> Entity
        //TODO: extract this to another function, maybe use Factory c'tors in inode creations
        User owner = userService.getById(addInode.userId);
        INode inode;
        switch (addInode.type) {
            case DIR:
                inode = new INode(addInode.name, INodeType.DIR, LocalDate.now(), null, fsRepository.findById(addInode.parentId).get());
                break;
            case FILE:
                inode = new Document(addInode.name, INodeType.FILE, LocalDate.now(), null, fsRepository.findById(addInode.parentId).get(), owner, LocalDate.now(), "");
                break;
            default:
                throw new IllegalArgumentException("Illegal Inode type");
        }

        return fsRepository.save(inode);
    }


    public INode move(Long inodeId, Long targetInodeId) {
        INode inodeToMove = fsRepository.getReferenceById(inodeId);
        INode targetINode = fsRepository.getReferenceById(targetInodeId);
        if (inodeToMove == null || targetINode == null) {
            throw new RuntimeException("Inode not found");
        }
        inodeToMove.setParent(targetINode);

        return fsRepository.save(inodeToMove);
    }

    public boolean isDir(Long parentId) {
        INode inode = fsRepository.findById(parentId).get();

        return inode.getType() == INodeType.DIR;
    }


    public List<INode> removeById(Long id) {
        return fsRepository.removeById(id);
    }

    public INode renameInode(Long id, String name) {
        INode inode = fsRepository.findById(id).get();
        inode.setName(name);

        return fsRepository.save(inode);
    }

    public boolean isExist(Long inodeId) {
        return fsRepository.existsById(inodeId);
    }

    //TODO: this doesn't work
    public boolean isHierarchicallyLegalMove(Long sourceId, Long targetId) {
        INode iNode = fsRepository.findById(sourceId).get();
        return true;

    }

    public INode upload(Long inodeId) {
        //Stringify txt file

        return null;

    }
}