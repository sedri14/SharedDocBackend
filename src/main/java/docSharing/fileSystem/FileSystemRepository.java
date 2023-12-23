package docSharing.fileSystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface FileSystemRepository extends JpaRepository<INode, Long> {

    @Transactional
    default Optional<INode> removeById(Long id) {
        Optional<INode> entity = findById(id);
        entity.ifPresent(this::delete);
        return entity;
    }

}

