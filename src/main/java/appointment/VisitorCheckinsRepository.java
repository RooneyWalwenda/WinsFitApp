package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorCheckinsRepository extends JpaRepository<VisitorCheckins, Integer> {
}
