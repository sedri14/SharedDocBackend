package docSharing.service;

import docSharing.DTO.AddINodeDTO;
import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.INodeType;
import docSharing.entities.User;
import docSharing.repository.FileSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return fsRepository.retrieveInodesInLevel(id);
    }

    public INode getById (Long id) {

        Optional<INode> opINode = fsRepository.findById(id);
        if (!opINode.isPresent()) {
            throw new IllegalArgumentException("INode does not exist");
        }

        return opINode.get();
    }

    public INode addInode(AddINodeDTO addInode) {
        //validation

        //DTO -> Entity
        //TODO: extract this to another function, maybe use Factory c'tors in inode creations
        User owner = null; //TODO: use user repo
        INode inode;
        switch (addInode.type) {
            case DIR: inode = new INode(addInode.name, INodeType.DIR, LocalDate.now(),null, getById(addInode.parentId));
                break;
            case FILE: inode = new Document(addInode.name, INodeType.FILE,LocalDate.now(),null, getById(addInode.parentId),owner,LocalDate.now(), "");
                break;
            default: throw new IllegalArgumentException("Illegal Inode type");
        }

        return fsRepository.save(inode);
    }
}
