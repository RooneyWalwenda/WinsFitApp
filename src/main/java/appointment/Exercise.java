package appointment;

import jakarta.persistence.*;
import java.io.File;
import java.util.Objects;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(name = "experience_level", nullable = false)
    private String experienceLevel;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Transient
    private Boolean videoExists;

    // Default constructor required by JPA
    public Exercise() {
    }

    // Constructor for creating new exercises
    public Exercise(String name, String gender, String experienceLevel, String filePath) {
        this.name = name;
        this.gender = gender;
        this.experienceLevel = experienceLevel;
        setFilePath(filePath); // Use setter to ensure consistent path format
    }

    // Constructor with ID for existing exercises
    public Exercise(Long id, String name, String gender, String experienceLevel, String filePath) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.experienceLevel = experienceLevel;
        setFilePath(filePath); // Use setter to ensure consistent path format
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        // Normalize the file path to ensure consistency
        if (filePath != null) {
            // Remove any leading /static prefix if present
            this.filePath = filePath.startsWith("/static")
                    ? filePath.substring(7)
                    : filePath;
            // Ensure path starts with /
            if (!this.filePath.startsWith("/")) {
                this.filePath = "/" + this.filePath;
            }
        } else {
            this.filePath = null;
        }
        // Reset video exists cache when path changes
        this.videoExists = null;
    }

    /**
     * Checks if the video file exists on the filesystem
     * @return true if the video file exists, false otherwise
     */
    public Boolean getVideoExists() {
        if (videoExists == null && filePath != null) {
            try {
                String normalizedPath = filePath.startsWith("/static")
                        ? filePath.substring(7)
                        : filePath;
                String fullPath = "src/main/resources/static" + normalizedPath;
                videoExists = new File(fullPath).exists();
            } catch (Exception e) {
                videoExists = false;
            }
        }
        return videoExists != null ? videoExists : false;
    }

    /**
     * Gets the full URL path for accessing the video
     * @param baseUrl The base URL of the server (e.g., "http://localhost:8080")
     * @return The complete URL to access the video
     */
    public String getVideoUrl(String baseUrl) {
        if (filePath == null) return null;
        return baseUrl + filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(id, exercise.id) &&
                Objects.equals(name, exercise.name) &&
                Objects.equals(gender, exercise.gender) &&
                Objects.equals(experienceLevel, exercise.experienceLevel) &&
                Objects.equals(filePath, exercise.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, experienceLevel, filePath);
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}