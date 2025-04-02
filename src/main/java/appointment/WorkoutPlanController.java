package appointment;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }
//generate workout plans
    @PostMapping("/generate")
    public List<WorkoutPlan> generateWorkoutPlan(@RequestBody Map<String, Integer> request) {
        Integer visitorId = request.get("visitorId");
        if (visitorId == null) {
            throw new RuntimeException("visitorId is required");
        }
        return workoutPlanService.generateWorkoutPlan(visitorId);
    }


    // Retrieve workout plans for a specific visitor
    @GetMapping("/visitor/{visitorId}")
    public List<WorkoutPlan> getWorkoutPlan(@PathVariable Long visitorId) {
        return workoutPlanService.getWorkoutPlan(visitorId);
    }
}
