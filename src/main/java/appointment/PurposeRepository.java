package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import appointment.Purpose;//unused since it is an import from the same package

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, Integer> {
}