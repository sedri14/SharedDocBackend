package docSharing.service;

import docSharing.DTO.AddINodeDTO;
import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.INodeType;
import docSharing.repository.FileSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FileSystemService {

    @Autowired
    private FileSystemRepository fsRepository;

    //private repo
    public List<INode> getInodesInLevel(Long id) {
        //validation

        return fsRepository.retrieveInodesInLevel(id);
    }

    //TODO: this method should have a returned value
    public INode addInode(AddINodeDTO addInode) {
        //validation

        //DTO -> Entity
        //TODO: extract this to another function
        INode inode = null;
//        switch (addInode.type) {
//            case DIR: inode = new INode(addInode.name, INodeType.DIR, LocalDate.now(),null, addInode.parentId);
//                break;
//            case FILE: inode = new Document(addInode.name, INodeType.FILE,LocalDate.now(),null,);
//                break;
//            default: throw new IllegalArgumentException("Illegal Inode type");
//        }

        return fsRepository.save(inode);
    }
}
