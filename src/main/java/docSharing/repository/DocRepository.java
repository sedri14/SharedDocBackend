package docSharing.repository;

import docSharing.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocRepository extends JpaRepository<Document, Long> {

}
