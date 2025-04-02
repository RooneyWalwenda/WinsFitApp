package appointment;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.sql.Date;

@Entity
@Table(name = "visitors", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone_number")


})
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int visitorid;

    @NotBlank(message = "Visitor name cannot be empty")
    private String visitorname;

    private String company;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    private String phone_number;
    private Date checkin_time;
    private Date checkout_time;
    private String visit_status;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    private boolean isDefaultPassword;

    // Required fields with proper constraints
    @NotNull(message = "Date of birth cannot be null")
    private Date dob;

    @NotBlank(message = "Gender is required")
    private String gender;


    @NotNull(message = "Height is required")
    @Positive(message = "Height must be a positive number")
    private Double height;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be a positive number")
    private Double weight;

    private Double body_fat; // Optional
    private Double bmi; // Calculated

    private String medical_conditions;
    private String injuries;
    private String medications;
    private Double blood_pressure; // Optional

    @NotBlank(message = "Activity level is required")
    private String activity_level;

    @NotBlank(message = "Experience level is required")
    private String experience_level;

    private Integer daily_steps; // Optional

    @NotBlank(message = "Workout type is required")
    private String workout_type;

    @NotBlank(message = "Fitness goal is required")
    private String fitness_goal;

    @NotNull(message = "Target weight is required")
    @Positive(message = "Target weight must be a positive number")
    private Double target_weight;

    @NotBlank(message = "Workout time is required")
    private String workout_time;

    private String department;
    @Column(name = "workout_streak", nullable = false)
    private int workout_streak = 0; // Default 0-day streak
    // Getters and Setters
    public int getWorkout_streak() {
        return workout_streak;
    }
    public void setWorkout_streak(int workout_streak) {
        this.workout_streak = workout_streak;
    }



    public boolean isDefaultPassword() {
        return isDefaultPassword;
    }

    public void setDefaultPassword(boolean isDefaultPassword) {
        this.isDefaultPassword = isDefaultPassword;
    }

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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
        calculateBmi();
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
        calculateBmi();
    }

    public Double getBody_fat() {
        return body_fat;
    }

    public void setBody_fat(Double body_fat) {
        this.body_fat = body_fat;
    }

    public Double getBmi() {
        return bmi;
    }

    private void calculateBmi() {
        if (height != null && weight != null && height > 0 && weight > 0) {
            this.bmi = weight / (height * height);
        }
    }

    public String getMedical_conditions() {
        return medical_conditions;
    }

    public void setMedical_conditions(String medical_conditions) {
        this.medical_conditions = medical_conditions;
    }

    public String getInjuries() {
        return injuries;
    }

    public void setInjuries(String injuries) {
        this.injuries = injuries;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public Double getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(Double blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public String getActivity_level() {
        return activity_level;
    }

    public void setActivity_level(String activity_level) {
        this.activity_level = activity_level;
    }

    public String getExperience_level() {
        return experience_level;
    }

    public void setExperience_level(String experience_level) {
        this.experience_level = experience_level;
    }

    public Integer getDaily_steps() {
        return daily_steps;
    }

    public void setDaily_steps(Integer daily_steps) {
        this.daily_steps = daily_steps;
    }

    public String getWorkout_type() {
        return workout_type;
    }

    public void setWorkout_type(String workout_type) {
        this.workout_type = workout_type;
    }

    public String getFitness_goal() {
        return fitness_goal;
    }

    public void setFitness_goal(String fitness_goal) {
        this.fitness_goal = fitness_goal;
    }

    public Double getTarget_weight() {
        return target_weight;
    }

    public void setTarget_weight(Double target_weight) {
        this.target_weight = target_weight;
    }

    public String getWorkout_time() {
        return workout_time;
    }

    public void setWorkout_time(String workout_time) {
        this.workout_time = workout_time;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
