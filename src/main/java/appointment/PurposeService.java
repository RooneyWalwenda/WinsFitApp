
package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurposeService {

    @Autowired
    private PurposeRepository purposeRepository;

    public List<Purpose> getAllPurposes() {
        return purposeRepository.findAll();
    }

    public Purpose getPurposeById(int id) {
        Optional<Purpose> optionalPurpose = purposeRepository.findById(id);
        return optionalPurpose.orElse(null);
    }

    public Purpose createPurpose(Purpose purpose) {
        return purposeRepository.save(purpose);
    }

    public Purpose updatePurpose(int id, Purpose newPurpose) {
        Optional<Purpose> optionalPurpose = purposeRepository.findById(id);
        if (optionalPurpose.isPresent()) {
            Purpose existingPurpose = optionalPurpose.get();
            existingPurpose.setPurpose_name(newPurpose.getPurpose_name());
            return purposeRepository.save(existingPurpose);
        }
        return null;
    }

    public void deletePurpose(int id) {
        purposeRepository.deleteById(id);
    }
}
