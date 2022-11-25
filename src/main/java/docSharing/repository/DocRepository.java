package docSharing.repository;

import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DocRepository extends JpaRepository<Document, Long> {

}
