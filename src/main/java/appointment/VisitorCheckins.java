package appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "visitor_checkins")
public class VisitorCheckins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int checkin_id;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    private Timestamp checkin_time;
    private Timestamp checkout_time;

    // Getters and Setters
    public int getCheckin_id() {
        return checkin_id;
    }

    public void setCheckin_id(int checkin_id) {
        this.checkin_id = checkin_id;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Timestamp getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(Timestamp checkin_time) {
        this.checkin_time = checkin_time;
    }

    public Timestamp getCheckout_time() {
        return checkout_time;
    }

    public void setCheckout_time(Timestamp checkout_time) {
        this.checkout_time = checkout_time;
    }
}
