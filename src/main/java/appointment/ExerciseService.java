package appointment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private static final String EXERCISE_FOLDER = "classpath:/static/Images/ExerciseVideos";

    private final ExerciseRepository exerciseRepository;

    @Autowired
    private ResourceLoader resourceLoader;

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

        Collections.shuffle(matchingExercises);

        for (int i = 0; i < days.length; i++) {
            List<Exercise> dailyExercises = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                int exerciseIndex = (i * 5 + j) % matchingExercises.size();
                Exercise exercise = matchingExercises.get(exerciseIndex);

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
        try {
            Resource resource = resourceLoader.getResource(EXERCISE_FOLDER);
            URL url = resource.getURL();

            if (url.getProtocol().equals("jar")) {
                processVideosFromJar(url);
            } else {
                File folder = resource.getFile();

                if (!folder.exists() || !folder.isDirectory()) {
                    System.out.println("Exercise video directory not found at: " + folder.getAbsolutePath());
                    return;
                }

                processVideosFromFolder(folder);
            }
        } catch (IOException e) {
            System.out.println("Error accessing exercise videos: " + e.getMessage());
        }
    }

    private void processVideosFromJar(URL jarUrl) throws IOException {
        String jarPath = jarUrl.getPath().substring(0, jarUrl.getPath().indexOf("!"));
        JarFile jarFile = new JarFile(URLDecoder.decode(jarPath.replace("file:", ""), "UTF-8"));
        Enumeration<JarEntry> entries = jarFile.entries();

        List<String> existingExerciseNames = exerciseRepository.findAll()
                .stream().map(Exercise::getName).toList();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.startsWith("BOOT-INF/classes/static/Images/ExerciseVideos/") &&
                    name.toLowerCase().endsWith(".mp4")) {

                String fileName = name.substring(name.lastIndexOf('/') + 1);
                String exerciseName = fileName.replace(".mp4", "");

                if (!existingExerciseNames.contains(exerciseName)) {
                    String gender = exerciseName.toLowerCase().contains("female") ? "female" : "male";
                    String experienceLevel = exerciseName.toLowerCase().contains("advanced") ? "advanced" :
                            (exerciseName.toLowerCase().contains("intermediate") ? "intermediate" : "beginner");

                    String relativePath = "/static/Images/ExerciseVideos/" + fileName;
                    Exercise exercise = new Exercise(exerciseName, gender, experienceLevel, relativePath);
                    saveExercise(exercise);
                }
            }
        }

        jarFile.close();
    }

    private void processVideosFromFolder(File folder) {
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

                String relativePath = "/static/Images/ExerciseVideos/" + file.getName();
                Exercise exercise = new Exercise(name, gender, experienceLevel, relativePath);
                saveExercise(exercise);
            }
        }
    }
}
