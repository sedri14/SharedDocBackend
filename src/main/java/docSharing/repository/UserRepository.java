package docSharing.repository;

import docSharing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        public  User findByEmail(String email);
        public User findByName(String name);
        public  User findByNameAndPassword(String name, String password);



}
