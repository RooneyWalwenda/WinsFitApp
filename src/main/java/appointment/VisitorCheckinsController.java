package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitor_checkins")
public class VisitorCheckinsController {

    @Autowired
    private VisitorCheckinsService visitorCheckinsService;

    @GetMapping
    public List<VisitorCheckins> getAllVisitorCheckins() {
        return visitorCheckinsService.getAllVisitorCheckins();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorCheckins> getVisitorCheckinById(@PathVariable int id) {
        VisitorCheckins visitorCheckin = visitorCheckinsService.getVisitorCheckinById(id);
        if (visitorCheckin != null) {
            return ResponseEntity.ok(visitorCheckin);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public VisitorCheckins createVisitorCheckin(@RequestBody VisitorCheckins visitorCheckin) {
        return visitorCheckinsService.createVisitorCheckin(visitorCheckin);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorCheckins> updateVisitorCheckin(@PathVariable int id, @RequestBody VisitorCheckins newVisitorCheckin) {
        VisitorCheckins updatedVisitorCheckin = visitorCheckinsService.updateVisitorCheckin(id, newVisitorCheckin);
        if (updatedVisitorCheckin != null) {
            return ResponseEntity.ok(updatedVisitorCheckin);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitorCheckin(@PathVariable int id) {
        visitorCheckinsService.deleteVisitorCheckin(id);
        return ResponseEntity.noContent().build();
    }
}
