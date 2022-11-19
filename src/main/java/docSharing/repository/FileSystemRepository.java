package docSharing.repository;

import docSharing.entities.abstracts.INode;
import net.bytebuddy.utility.FileSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileSystemRepository extends JpaRepository<INode,Long> {

}
