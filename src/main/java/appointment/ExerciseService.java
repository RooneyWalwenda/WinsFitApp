package appointment;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private static final String EXERCISE_FOLDER = "src/main/resources/static/Images/ExerciseVideos";
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public List<Exercise> getExercisesForVisitor(String gender, String experienceLevel) {
        return exerciseRepository.findByGenderAndExperienceLevel(gender, experienceLevel);
    }

    public List<Exercise> searchExercises(String gender, String experienceLevel, String keyword) {
        return exerciseRepository.findByGenderAndExperienceLevelAndNameContainingIgnoreCase(
                gender, experienceLevel, keyword);
    }

    public Map<String, List<Exercise>> assignExercises(String gender, String experienceLevel) {
        List<Exercise> matchingExercises = exerciseRepository.findByGenderAndExperienceLevel(gender, experienceLevel);

        if (matchingExercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for gender: " + gender +
                    " and experience level: " + experienceLevel);
        }

        Map<String, List<Exercise>> weeklyPlan = new LinkedHashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        // Shuffle the exercises
        Collections.shuffle(matchingExercises);

        // Use modulo to cycle through exercises
        for (int i = 0; i < days.length; i++) {
            List<Exercise> dailyExercises = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                int exerciseIndex = (i * 5 + j) % matchingExercises.size();
                Exercise exercise = matchingExercises.get(exerciseIndex);

                // Ensure filePath is properly formatted
                if (exercise.getFilePath() != null && !exercise.getFilePath().startsWith("/")) {
                    exercise.setFilePath("/" + exercise.getFilePath());
                }

                dailyExercises.add(exercise);
            }
            weeklyPlan.put(days[i], dailyExercises);
        }

        return weeklyPlan;
    }
    public List<Map<String, Object>> validateAllVideos() {
        List<Exercise> exercises = exerciseRepository.findAll();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Exercise exercise : exercises) {
            // Remove /static prefix if present for file existence check
            String path = exercise.getFilePath().startsWith("/static")
                    ? exercise.getFilePath().substring(7)
                    : exercise.getFilePath();

            String videoPath = "src/main/resources/static" + path;
            File file = new File(videoPath);

            Map<String, Object> result = new HashMap<>();
            result.put("id", exercise.getId());
            result.put("name", exercise.getName());
            result.put("filePath", exercise.getFilePath());
            result.put("exists", file.exists());

            results.add(result);
        }

        return results;
    }

    @PostConstruct
    public void storeExerciseVideosInDatabase() {
        File folder = new File(EXERCISE_FOLDER);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Exercise video directory not found!");
            return;
        }

        List<String> existingExerciseNames = exerciseRepository.findAll()
                .stream().map(Exercise::getName).toList();

        List<File> videoFiles = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> file.getName().toLowerCase().endsWith(".mp4"))
                .collect(Collectors.toList());

        for (File file : videoFiles) {
            String name = file.getName().replace(".mp4", "");
            if (!existingExerciseNames.contains(name)) {
                String gender = name.toLowerCase().contains("female") ? "female" : "male";
                String experienceLevel = name.toLowerCase().contains("advanced") ? "advanced" :
                        (name.toLowerCase().contains("intermediate") ? "intermediate" : "beginner");

                // Store path without /static prefix for consistency
                String relativePath = "/Images/ExerciseVideos/" + file.getName();
                Exercise exercise = new Exercise(name, gender, experienceLevel, relativePath);
                saveExercise(exercise);
            }
        }
    }
}