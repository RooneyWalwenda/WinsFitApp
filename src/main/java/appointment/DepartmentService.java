package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    public List<Department> getDepartmentsByInstitutionId(Long institutionId) {
        List<Department> departments = departmentRepository.findByInstitutionId(institutionId);
        logger.info("Departments retrieved for Institution ID {}: {}", institutionId, departments);
        return departments;
    }
    

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(int id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        return optionalDepartment.orElse(null);
    }

    public Department createDepartment(Department department, Long institutionId) {
        Optional<Institution> optionalInstitution = institutionRepository.findById(institutionId);
        if (optionalInstitution.isPresent()) {
            department.setInstitution(optionalInstitution.get());
            return departmentRepository.save(department);
        }
        throw new IllegalArgumentException("Institution not found");
    }

    public Department updateDepartment(int id, Department newDepartment) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isPresent()) {
            Department existingDepartment = optionalDepartment.get();
            existingDepartment.setDepartmentname(newDepartment.getDepartmentname());
            return departmentRepository.save(existingDepartment);
        }
        return null;
    }

    public void deleteDepartment(int id) {
        departmentRepository.deleteById(id);
    }
}
