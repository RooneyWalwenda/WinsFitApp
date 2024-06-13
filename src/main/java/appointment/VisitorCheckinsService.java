package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VisitorCheckinsService {

    @Autowired
    private VisitorCheckinsRepository visitorCheckinsRepository;

    public VisitorCheckins getVisitorCheckinById(int id) {
        Optional<VisitorCheckins> optionalVisitorCheckins = visitorCheckinsRepository.findById(id);
        return optionalVisitorCheckins.orElse(null);
    }

    public VisitorCheckins createVisitorCheckin(VisitorCheckins visitorCheckins) {
        return visitorCheckinsRepository.save(visitorCheckins);
    }

    public VisitorCheckins updateVisitorCheckin(int id, VisitorCheckins newVisitorCheckins) {
        Optional<VisitorCheckins> optionalVisitorCheckins = visitorCheckinsRepository.findById(id);
        if (optionalVisitorCheckins.isPresent()) {
            VisitorCheckins existingVisitorCheckins = optionalVisitorCheckins.get();
            existingVisitorCheckins.setCheckin_time(newVisitorCheckins.getCheckin_time());
            existingVisitorCheckins.setCheckout_time(newVisitorCheckins.getCheckout_time());
            existingVisitorCheckins.setVisitor(newVisitorCheckins.getVisitor());
            return visitorCheckinsRepository.save(existingVisitorCheckins);
        }
        return null;
    }

    public void deleteVisitorCheckin(int id) {
        visitorCheckinsRepository.deleteById(id);
    }
}
