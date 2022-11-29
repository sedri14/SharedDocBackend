package docSharing.repository;

import docSharing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

//shoudl we add @Repository here?
public interface AuthRepository extends JpaRepository<User, Long> {



}
