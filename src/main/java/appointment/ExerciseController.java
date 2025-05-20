package appointment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/plan")
    public ResponseEntity<Map<String, List<Exercise>>> getExercisePlan(
            @RequestParam String gender,
            @RequestParam String experienceLevel) {

        // Validate inputs
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender parameter is required");
        }
        if (experienceLevel == null || experienceLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Experience level parameter is required");
        }

        Map<String, List<Exercise>> weeklyPlan = exerciseService.assignExercises(
                gender.toLowerCase(),
                experienceLevel.toLowerCase()
        );

        return ResponseEntity.ok(weeklyPlan);
    }

    @GetMapping("/{gender}/{experienceLevel}")
    public ResponseEntity<List<Exercise>> getExercisesForVisitor(
            @PathVariable String gender,
            @PathVariable String experienceLevel) {
        List<Exercise> exercises = exerciseService.getExercisesForVisitor(gender, experienceLevel);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/search/{gender}/{experienceLevel}")
    public ResponseEntity<List<Exercise>> searchExercises(
            @PathVariable String gender,
            @PathVariable String experienceLevel,
            @RequestParam String keyword) {
        List<Exercise> exercises = exerciseService.searchExercises(gender, experienceLevel, keyword);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @PostMapping
    public ResponseEntity<Exercise> addExercise(@RequestBody Exercise exercise) {
        return ResponseEntity.ok(exerciseService.saveExercise(exercise));
    }

    @PostMapping("/importVideos")
    public ResponseEntity<String> importVideos() {
        exerciseService.storeExerciseVideosInDatabase();
        return ResponseEntity.ok("Videos imported successfully");
    }

    @GetMapping("/validate-videos")
    public ResponseEntity<List<Map<String, Object>>> validateAllVideos() {
        return ResponseEntity.ok(exerciseService.validateAllVideos());
    }
}