package appointment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // Fetch and store exercises from ExerciseDB API
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchExercises() {
        exerciseService.fetchAndStoreExercises();
        return ResponseEntity.ok("Exercises fetched and stored successfully.");
    }

    // Get stored exercises
    @GetMapping("/random")
    public ResponseEntity<List<Exercise>> getRandomExercises() {
        return ResponseEntity.ok(exerciseService.getRandomExercises());
    }
}
