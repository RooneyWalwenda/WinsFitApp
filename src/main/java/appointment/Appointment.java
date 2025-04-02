package appointment;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentid;

    @ManyToOne
    @JoinColumn(name = "visitorid")
    private Visitor visitor;

    @ManyToOne
    @JoinColumn(name = "institutionid")
    private Institution institution;


    @ManyToOne
    @JoinColumn(name = "userid", nullable = false) // Stores the general user (admin, super admin, etc.)
    private Users user;

    private LocalDate date;
    private Time time;
    private String appointmentstatus;
    private String department;

    @Column(name = "passcode", unique = true)
    private String passcode;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;


    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    // New field to specify meeting type (VIRTUAL or PHYSICAL)
    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType = MeetingType.PHYSICAL;

    // New field to store virtual meeting link
    @Column(name = "video_meeting_link")
    private String videoMeetingLink;

    // Getters and Setters for new fields
    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    public String getVideoMeetingLink() {
        return videoMeetingLink;
    }
    // ✅ Add Getters and Setters
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
    public void setVideoMeetingLink(String videoMeetingLink) {
        this.videoMeetingLink = videoMeetingLink;
    }

    // Existing Getters and Setters
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
