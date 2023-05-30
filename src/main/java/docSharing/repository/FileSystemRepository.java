package docSharing.repository;

import docSharing.entities.INode;
import docSharing.enums.INodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FileSystemRepository extends JpaRepository<INode, Long> {

    //    @Transactional
//    INode removeById(Long id);
    @Transactional
    default Optional<INode> removeById(Long id) {
        Optional<INode> entity = findById(id);
        entity.ifPresent(this::delete);
        return entity;
    }

}

