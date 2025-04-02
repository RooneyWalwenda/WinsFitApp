package appointment;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String gifUrl;

    @Column(nullable = false)
    private String target; // Target muscle group

    @Column(nullable = false)
    private String bodyPart; // Target body part

    @Column(nullable = false)
    private String equipment; // Equipment used

    @Column(nullable = false)
    private String difficulty; // Difficulty level (beginner, intermediate, advanced)

    @ElementCollection
    private List<String> secondaryMuscles; // Additional muscles worked

    @ElementCollection
    private List<String> instructions; // Step-by-step guide

    // No-args constructor (required by JPA)
    public Exercise() {}

    // Constructor without ID (Recommended for new entities)
    public Exercise(String name, String gifUrl, String target, String bodyPart, String equipment, String difficulty, List<String> secondaryMuscles, List<String> instructions) {
        this.name = name;
        this.gifUrl = gifUrl;
        this.target = target;
        this.bodyPart = bodyPart;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.secondaryMuscles = secondaryMuscles;
        this.instructions = instructions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getBodyPart() { return bodyPart; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<String> getSecondaryMuscles() { return secondaryMuscles; }
    public void setSecondaryMuscles(List<String> secondaryMuscles) { this.secondaryMuscles = secondaryMuscles; }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }
}
