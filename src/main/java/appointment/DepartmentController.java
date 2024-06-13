package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<Department>> getDepartmentsByInstitutionId1(@PathVariable Long institutionId) {
        List<Department> departments = departmentService.getDepartmentsByInstitutionId(institutionId);
        logger.info("Successfully retrieved departments for Institution ID {}: {}", institutionId, departments);
        return ResponseEntity.ok(departments);
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department, @RequestParam Long institutionId) {
        Department createdDepartment = departmentService.createDepartment(department, institutionId);
        return ResponseEntity.ok(createdDepartment);                                                                                                                                                                                                                                                                                                                                                                                                                                
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable int id, @RequestBody Department newDepartment) {
        Department updatedDepartment = departmentService.updateDepartment(id, newDepartment);
        if (updatedDepartment != null) {
            return ResponseEntity.ok(updatedDepartment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable int id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
    
    
}
