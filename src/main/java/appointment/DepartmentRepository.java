package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}


/*@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    // Add custom query methods if needed
}*/
