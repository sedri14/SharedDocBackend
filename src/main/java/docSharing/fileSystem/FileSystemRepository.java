package docSharing.fileSystem;

import docSharing.exceptions.INodeNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface FileSystemRepository extends JpaRepository<INode, Long> {

    @Transactional
    default INode removeById(Long id) {
        Optional<INode> entityOptional = findById(id);
        if (entityOptional.isPresent()) {
            INode entity = entityOptional.get();
            delete(entity);
            return entity;
        } else {
            throw new INodeNotFoundException("Can not delete inode with ID: " + id);
        }
    }
}

