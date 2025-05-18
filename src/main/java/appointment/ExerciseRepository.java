package appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByGenderAndExperienceLevel(String gender, String experienceLevel);

    // Additional query to find exercises by gender, experience level, and name containing a keyword
    List<Exercise> findByGenderAndExperienceLevelAndNameContainingIgnoreCase(
            String gender, String experienceLevel, String keyword);
}