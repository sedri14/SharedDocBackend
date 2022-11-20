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

    public List<INode> findAll(){
        return fsRepository.findAll();
    }

//    public INode getById(Long id) {
//
//        INode inode = null;
//        try {
//            inode = fsRepository.getReferenceById(id);
//        } catch (EntityNotFoundException e){
//            throw new IllegalArgumentException("INode does not exist");
//        }
//        return inode;
//    }

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


}
