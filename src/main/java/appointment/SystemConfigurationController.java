package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system_configuration")
public class SystemConfigurationController {

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @GetMapping
    public List<SystemConfiguration> getAllSystemConfigurations() {
        return systemConfigurationService.getAllSystemConfigurations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemConfiguration> getSystemConfigurationById(@PathVariable int id) {
        SystemConfiguration systemConfiguration = systemConfigurationService.getSystemConfigurationById(id);
        if (systemConfiguration != null) {
            return ResponseEntity.ok(systemConfiguration);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public SystemConfiguration createSystemConfiguration(@RequestBody SystemConfiguration systemConfiguration) {
        return systemConfigurationService.createSystemConfiguration(systemConfiguration);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SystemConfiguration> updateSystemConfiguration(@PathVariable int id, @RequestBody SystemConfiguration newSystemConfiguration) {
        SystemConfiguration updatedSystemConfiguration = systemConfigurationService.updateSystemConfiguration(id, newSystemConfiguration);
        if (updatedSystemConfiguration != null) {
            return ResponseEntity.ok(updatedSystemConfiguration);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSystemConfiguration(@PathVariable int id) {
        systemConfigurationService.deleteSystemConfiguration(id);
        return ResponseEntity.noContent().build();
    }
}
