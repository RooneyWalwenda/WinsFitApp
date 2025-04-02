package appointment;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    private LocalDate date;
    private int dayNumber;

    @ElementCollection
    private List<String> exercises;

    public WorkoutPlan() {}

    public WorkoutPlan(Visitor visitor, LocalDate date, int dayNumber, List<String> exercises) {
        this.visitor = visitor;
        this.date = date;
        this.dayNumber = dayNumber;
        this.exercises = exercises;
    }

    public Long getId() { return id; }
    public Visitor getVisitor() { return visitor; }
    public LocalDate getDate() { return date; }
    public int getDayNumber() { return dayNumber; }
    public List<String> getExercises() { return exercises; }

    public void setId(Long id) { this.id = id; }
    public void setVisitor(Visitor visitor) { this.visitor = visitor; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }
    public void setExercises(List<String> exercises) { this.exercises = exercises; }
}
