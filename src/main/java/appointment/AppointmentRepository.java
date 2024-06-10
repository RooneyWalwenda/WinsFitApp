package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByDepartmentAndDate(String department, Date date);
    long countByDepartmentAndDateAndTime(String department, Date date, Time time);
    List<Time> findAppointmentTimesByDateAndDepartment(Date date, String department);
    List<Appointment> findByVisitorVisitorid(int visitorid);
    
    // Add this method to enable checking for the existence of a passcode
    boolean existsByPasscode(String passcode);
}
