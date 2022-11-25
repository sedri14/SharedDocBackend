package docSharing.repository;

import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

//    public UserRole findByDoc_idAndUser_id(Long docId, Long userId);

//    int deleteDocument(Document document);
//    public Permission findBydoc_id(Long docId);
//
//    public Permission findPermissionByDocIdAndUserId(Long doc_id, Long user_id);

}
