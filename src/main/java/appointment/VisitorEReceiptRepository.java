package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorEReceiptRepository extends JpaRepository<VisitorEReceipt, Integer> {
}
