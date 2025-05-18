package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByDepartmentAndDate(String department, LocalDate date);
    long countByDepartmentAndDateAndTime(String department, LocalDate date, Time time);
    List<Time> findAppointmentTimesByDateAndDepartment(LocalDate date, String department);
    List<Appointment> findByVisitorVisitorid(int visitorid);
    
    boolean existsByPasscode(String passcode);
    Appointment findByPasscode(String passcode);
    
   // List<Appointment> findByDepartmentAndDateAndInstitution(String department, LocalDate date, Institution institution);
    List<Appointment> findByDepartmentAndDateAndInstitution(String department, LocalDate date, Institution institution);
    List<Appointment> findByInstitutionId(Long institutionId);
    List<Appointment> findByInstitutionIdAndDateAndAppointmentstatus(Long institutionId, LocalDate date, String status);

        @Query("SELECT a FROM Appointment a WHERE a.date = :date AND a.time BETWEEN :startTime AND :endTime")
        List<Appointment> findAppointmentsBetween(@Param("date") LocalDate date,
                                                  @Param("startTime") Time startTime,
                                                  @Param("endTime") Time endTime);

    List<Appointment> findByUserUserid(int physioId);
}
