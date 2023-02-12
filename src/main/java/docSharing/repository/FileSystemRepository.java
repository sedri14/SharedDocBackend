package docSharing.repository;

import docSharing.entities.INode;
import docSharing.enums.INodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface FileSystemRepository extends JpaRepository<INode, Long> {

    //List<INode> findByParentId(Long parentId);

    @Transactional
    Integer removeById(Long id);

    //Set<INode> findByParentIdAndTypeEquals(Long parentId, INodeType type);

    //INode findByName(String name);


}

