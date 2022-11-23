package docSharing.repository;

import docSharing.DTO.AddINodeDTO;
import docSharing.entities.INode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileSystemRepository extends JpaRepository<INode, Long> {


//    @Query(nativeQuery = true,
//            value = "select *, 0 AS clazz_ " +
//                    "from fs_inodes " +
//                    "where parent_id =:parentId")
//    List<INode> retrieveInodesInLevel(@Param("parentId") Long parentId);

   List<INode> findByParentId(Long parentId);


    @Query(nativeQuery = true,
            value = "with recursive cte(id, parent_id) as (" +
                    "    select id, parent_id from fs_inodes where id=5" +
                    "    union all" +
                    "    select t.id, t.parent_id from fs_inodes t inner join cte c on c.id = t.parent_id\n" +
                    ")" +
                    "delete from fs_inodes where id in (select id from cte)")
    List<INode> delete(@Param("id") Long id);


}

