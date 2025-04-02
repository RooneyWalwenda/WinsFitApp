package appointment;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final VisitorRepository visitorRepository;

    public WorkoutPlanService(ExerciseRepository exerciseRepository, WorkoutPlanRepository workoutPlanRepository, VisitorRepository visitorRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.visitorRepository = visitorRepository;
    }

    public List<WorkoutPlan> generateWorkoutPlan(Integer visitorId) {
        Optional<Visitor> visitorOptional = visitorRepository.findById(visitorId);
        if (visitorOptional.isEmpty()) {
            throw new RuntimeException("Visitor not found");
        }

        Visitor visitor = visitorOptional.get();

        // Filter exercises based on visitor details
        List<Exercise> filteredExercises = exerciseRepository.findAll().stream()
                .filter(exercise -> matchExercise(visitor, exercise))
                .collect(Collectors.toList());

        if (filteredExercises.isEmpty()) {
            throw new RuntimeException("No suitable exercises found for visitor");
        }

        List<WorkoutPlan> workoutPlans = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        int totalDays = 14;
        int daysPerWeek = 5;
        int exercisesPerDay = 6;

        // Schedule workouts for 14 days (5 days per week)
        int workoutDays = 0;
        for (int i = 0; i < totalDays; i++) {
            if (workoutDays >= daysPerWeek) {
                // Skip to next week (2-day rest)
                workoutDays = 0;
                i += 2;
                if (i >= totalDays) break;
            }

            List<String> selectedExercises = pickRandomExercises(filteredExercises, exercisesPerDay);
            WorkoutPlan plan = new WorkoutPlan(visitor, startDate.plusDays(i), i + 1, selectedExercises);
            workoutPlans.add(plan);

            workoutDays++;
        }

        return workoutPlanRepository.saveAll(workoutPlans);
    }

    private boolean matchExercise(Visitor visitor, Exercise exercise) {
        // Match exercises based on visitor's age, experience, weight, and goal
        boolean matchesExperience = visitor.getExperience_level().equalsIgnoreCase(exercise.getDifficulty());
        boolean matchesGoal = visitor.getFitness_goal().equalsIgnoreCase(exercise.getTarget());
        return matchesExperience || matchesGoal;
    }

    private List<String> pickRandomExercises(List<Exercise> exercises, int count) {
        Collections.shuffle(exercises);
        return exercises.stream().limit(count).map(Exercise::getName).collect(Collectors.toList());
    }

    public List<WorkoutPlan> getWorkoutPlan(Long visitorId) {
        return workoutPlanRepository.findByVisitor_Visitorid(visitorId);

    }
}
