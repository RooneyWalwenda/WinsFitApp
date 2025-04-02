package appointment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {

    Optional<Visitor> findByEmail(String email);
    
    @Modifying
    @Query("UPDATE Visitor v SET v.password = :password WHERE v.email = :email")
    void updatePasswordByEmail(String email, String password);
    Optional<Visitor> findById(Integer id);
}
