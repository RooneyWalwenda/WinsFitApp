package appointment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
                "status", "running",
                "service", "Appointment Booking API",
                "endpoints", Map.of(
                        "/api/exercises", "Exercise endpoints",
                        "/media/**", "Static content"
                )
        ));
    }
}
