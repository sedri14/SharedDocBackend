package docSharing.repository;

import docSharing.entities.INode;
import docSharing.entities.SharedRole;
import docSharing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedRoleRepository extends JpaRepository<SharedRole, Long> {
    Optional<SharedRole> findByInodeAndUser(INode inode, User user);
    List<SharedRole> findByUser(User user);

    List<SharedRole> findByInode(INode inode);
}
