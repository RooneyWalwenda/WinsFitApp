package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitor_notifications")
public class VisitorNotificationsController {

    @Autowired
    private VisitorNotificationsService visitorNotificationsService;

    @GetMapping
    public List<VisitorNotifications> getAllVisitorNotifications() {
        return visitorNotificationsService.getAllVisitorNotifications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorNotifications> getVisitorNotificationById(@PathVariable int id) {
        VisitorNotifications visitorNotification = visitorNotificationsService.getVisitorNotificationById(id);
        if (visitorNotification != null) {
            return ResponseEntity.ok(visitorNotification);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VisitorNotifications> createVisitorNotification(@RequestBody VisitorNotifications visitorNotification) {
        // Ensure that the Visitor object is set
        if (visitorNotification.getVisitor() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Here you may need to fetch the Visitor object from the database
        // if it's not already set properly in the visitorNotification

        VisitorNotifications createdNotification = visitorNotificationsService.createVisitorNotification(visitorNotification);
        if (createdNotification != null) {
            return ResponseEntity.ok(createdNotification);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorNotifications> updateVisitorNotification(@PathVariable int id, @RequestBody VisitorNotifications newVisitorNotification) {
        VisitorNotifications updatedVisitorNotification = visitorNotificationsService.updateVisitorNotification(id, newVisitorNotification);
        if (updatedVisitorNotification != null) {
            return ResponseEntity.ok(updatedVisitorNotification);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitorNotification(@PathVariable int id) {
        visitorNotificationsService.deleteVisitorNotification(id);
        return ResponseEntity.noContent().build();
    }
}
