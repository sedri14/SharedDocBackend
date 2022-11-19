package docSharing.repository;

import com.mysql.cj.Query;
import com.mysql.cj.Session;
import docSharing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        public static User findByEmail(String email)
        {
                return findByEmail(email);
        }

        public static boolean isExist (String email)
        {return findByEmail(email)!= null; }


}
