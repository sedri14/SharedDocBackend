package docSharing.documentUserAccess;

import docSharing.entities.Document;
import docSharing.fileSystem.INode;
import docSharing.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRepository extends JpaRepository<DocumentUserAccess, Long> {
    Optional<DocumentUserAccess> findByDocumentAndUser(Document document, User user);
    List<DocumentUserAccess> findByUser(User user);
    List<DocumentUserAccess> findByInode(INode inode);
}
