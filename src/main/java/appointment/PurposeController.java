
package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purposes")
public class PurposeController {

    @Autowired
    private PurposeService purposeService;

    @GetMapping
    public List<Purpose> getAllPurposes() {
        return purposeService.getAllPurposes();
    }

    @GetMapping("/{id}")
    public Purpose getPurposeById(@PathVariable int id) {
        return purposeService.getPurposeById(id);
    }

    @PostMapping
    public Purpose createPurpose(@RequestBody Purpose purpose) {
        return purposeService.createPurpose(purpose);
    }

    @PutMapping("/{id}")
    public Purpose updatePurpose(@PathVariable int id, @RequestBody Purpose purpose) {
        return purposeService.updatePurpose(id, purpose);
    }

    @DeleteMapping("/{id}")
    public void deletePurpose(@PathVariable int id) {
        purposeService.deletePurpose(id);
    }
}
