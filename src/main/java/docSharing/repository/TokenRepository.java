package docSharing.repository;

import docSharing.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TokenRepository extends JpaRepository<VerificationToken, Long> {

    public VerificationToken findByToken(String token);
    public VerificationToken findByUser(String token);

    public List<VerificationToken> findAll();

    @Transactional
    @Modifying
    @Query("delete from VerificationToken v where v.token = ?1")
    public void deleteByToken(String token);

}
