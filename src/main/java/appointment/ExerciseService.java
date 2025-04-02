package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    @Value("${exercisedb.api.key}")
    private String exerciseApiKey;

    private final ExerciseRepository exerciseRepository;
    private final RestTemplate restTemplate;

    private static final int MAX_EXERCISES = 150; // Limit to 150 exercises in the database
    private static final int PAGE_LIMIT = 50; // API fetch limit per page

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository, RestTemplate restTemplate) {
        this.exerciseRepository = exerciseRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Constructs the API URL with dynamic pagination support.
     */
    private String getExerciseDbUrl(int page) {
        return String.format("https://exercisedb.p.rapidapi.com/exercises?limit=%d&page=%d", PAGE_LIMIT, page);
    }

    /**
     * Fetches exercises from API and stores them in the database.
     */
    public void fetchAndStoreExercises() {
        long currentCount = exerciseRepository.count();

        if (currentCount >= MAX_EXERCISES) {
            System.out.println("Database already contains " + MAX_EXERCISES + " exercises. No new exercises needed.");
            return;
        }

        System.out.println("Fetching exercises from API...");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", exerciseApiKey);
        headers.set("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        int page = 1; // Start fetching from page 1
        boolean hasMorePages = true;
        boolean fetchedNewExercises = false;

        Set<String> existingGifUrls = exerciseRepository.findAll()
                .stream()
                .map(Exercise::getGifUrl)
                .collect(Collectors.toSet());

        while (currentCount < MAX_EXERCISES && hasMorePages) {
            String url = getExerciseDbUrl(page);
            ResponseEntity<List> response;

            try {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            } catch (Exception e) {
                System.err.println("Failed to fetch exercises from API: " + e.getMessage());
                return;
            }

            List<Map<String, Object>> exercises = response.getBody();
            if (exercises == null || exercises.isEmpty()) {
                System.out.println("No more exercises available from API.");
                break;
            }

            List<Exercise> newExercises = exercises.stream()
                    .map(ex -> {
                        String gifUrl = (String) ex.get("gifUrl");

                        if (existingGifUrls.contains(gifUrl)) {
                            System.out.println("Skipping existing exercise: " + gifUrl);
                            return null;
                        }

                        return new Exercise(
                                (String) ex.get("name"),
                                gifUrl,
                                (String) ex.get("target"),
                                (String) ex.get("bodyPart"),
                                (String) ex.get("equipment"),
                                (String) ex.get("difficulty"),
                                (List<String>) ex.get("secondaryMuscles"),
                                (List<String>) ex.get("instructions")
                        );
                    })
                    .filter(Objects::nonNull)  // Remove null values (duplicates)
                    .limit(MAX_EXERCISES - currentCount) // Ensure max limit is not exceeded
                    .collect(Collectors.toList());

            if (!newExercises.isEmpty()) {
                exerciseRepository.saveAll(newExercises);
                fetchedNewExercises = true;
                currentCount = exerciseRepository.count(); // Update count after saving
            }

            System.out.println("Fetched page " + page + " with " + exercises.size() + " items.");
            System.out.println("Added " + newExercises.size() + " new exercises. Total now: " + currentCount);

            // If API returned less than PAGE_LIMIT, it means we're at the last page
            hasMorePages = (exercises.size() == PAGE_LIMIT);
            page++; // Move to the next page
        }

        if (!fetchedNewExercises) {
            System.out.println("No new exercises were added. All fetched exercises already exist.");
        }
    }

    /**
     * Fetches a random set of exercises.
     */
    public List<Exercise> getRandomExercises() {
        return exerciseRepository.findAll()
                .stream()
                .limit(6)
                .collect(Collectors.toList());
    }
}
