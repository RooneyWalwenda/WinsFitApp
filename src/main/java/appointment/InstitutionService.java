package appointment;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InstitutionService.class);

    @Autowired
    private InstitutionRepository institutionRepository;

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Optional<Institution> getInstitutionById(Long id) {
        return institutionRepository.findById(id);
    }

    public Institution createInstitution(Institution institution) {
        try {
            // Set the created date before saving the institution
            institution.setCreatedDate(new Date());
            
            // Save the institution to the repository
            Institution createdInstitution = institutionRepository.save(institution);
            
            // Log success message after saving
            logger.info("Institution Created successfully: {}", createdInstitution.getName());
            
            // Return the created institution
            return createdInstitution;
            
        } catch (Exception e) {
            // Log error if there's an exception
            logger.error("Failed to create institution: {}", e.getMessage());
            
            // Rethrow the exception to handle it at a higher level
            throw e;
        }
    }


    public Institution updateInstitution(Long id, Institution institutionDetails) {
        try {
            Institution institution = institutionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Institution not found"));

            institution.setName(institutionDetails.getName());
            institution.setAddress(institutionDetails.getAddress());
            institution.setContactNumber(institutionDetails.getContactNumber());
            institution.setEmail(institutionDetails.getEmail());
            institution.setWebsite(institutionDetails.getWebsite());
            institution.setRegistrationNumber(institutionDetails.getRegistrationNumber());
            institution.setUpdatedDate(new Date());

            Institution updatedInstitution = institutionRepository.save(institution);
            logger.info("Institution updated successfully: {}", institution.getName());
            return updatedInstitution;
        } catch (Exception e) {
            logger.error("Failed to update institution: {}", e.getMessage());
            throw e;
        }
    }

    public void deleteInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found"));
        institutionRepository.delete(institution);
    }
}
