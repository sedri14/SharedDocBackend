package docSharing.repository;

import docSharing.DTO.AddINodeDTO;
import docSharing.entities.INode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FileSystemRepository extends JpaRepository<INode, Long> {

   List<INode> findByParentId(Long parentId);

    @Transactional
    List<INode> removeById(Long id);


}

