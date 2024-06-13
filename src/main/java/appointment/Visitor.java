package appointment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
@Table(name = "visitors")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int visitorid;
    private String visitorname;
    private String company;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    private String phone_number;
    private Date checkin_time;
    private Date checkout_time;
    private String visit_status;
    @NotNull(message = "Password cannot be null")
    
    private String password;
    private boolean isDefaultPassword;

    public boolean isDefaultPassword() {
		return isDefaultPassword;
	}

	public void setDefaultPassword(boolean isDefaultPassword) {
		this.isDefaultPassword = isDefaultPassword;
	}

	// Getters and Setters
    public int getVisitorid() {
        return visitorid;
    }

    public void setVisitorid(int visitorid) {
        this.visitorid = visitorid;
    }

    public String getVisitorname() {
        return visitorname;
    }

    public void setVisitorname(String visitorname) {
        this.visitorname = visitorname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Date getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(Date checkin_time) {
        this.checkin_time = checkin_time;
    }

    public Date getCheckout_time() {
        return checkout_time;
    }

    public void setCheckout_time(Date checkout_time) {
        this.checkout_time = checkout_time;
    }

    public String getVisit_status() {
        return visit_status;
    }

    public void setVisit_status(String visit_status) {
        this.visit_status = visit_status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
