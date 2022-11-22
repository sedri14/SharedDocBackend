package docSharing.repository;

import docSharing.DTO.AddINodeDTO;
import docSharing.entities.INode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileSystemRepository extends JpaRepository<INode, Long> {


    @Query(nativeQuery = true,
            value = "select *, 0 AS clazz_ " +
                    "from fs_inodes " +
                    "where parent_id =:parentId")
    List<INode> retrieveInodesInLevel(@Param("parentId") Long parentId);


//    @Query(nativeQuery = true,
//            value = "")
//    List<INode> delete(@Param("id") Long id);
}

