package appointment;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentid;

    @ManyToOne
    @JoinColumn(name = "visitorid")
    private Visitor visitor;

    private Date date;
    private Time time;
    private String appointmentstatus;
    private String department;
   
    @Column(name = "passcode", unique = true)
    private String passcode;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate; // New field

    // Getters and Setters
    public int getAppointmentid() {
        return appointmentid;
    }

    public void setAppointmentid(int appointmentid) {
        this.appointmentid = appointmentid;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getAppointmentstatus() {
        return appointmentstatus;
    }

    public void setAppointmentstatus(String appointmentstatus) {
        this.appointmentstatus = appointmentstatus;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }
}