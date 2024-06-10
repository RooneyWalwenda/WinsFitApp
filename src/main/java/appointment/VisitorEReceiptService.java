package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorEReceiptService {

    @Autowired
    private VisitorEReceiptRepository visitorEReceiptRepository;

    public List<VisitorEReceipt> getAllVisitorEReceipts() {
        return visitorEReceiptRepository.findAll();
    }

    public VisitorEReceipt getVisitorEReceiptById(int id) {
        Optional<VisitorEReceipt> optionalVisitorEReceipt = visitorEReceiptRepository.findById(id);
        return optionalVisitorEReceipt.orElse(null);
    }

    public VisitorEReceipt createVisitorEReceipt(VisitorEReceipt visitorEReceipt) {
        return visitorEReceiptRepository.save(visitorEReceipt);
    }

    public VisitorEReceipt updateVisitorEReceipt(int id, VisitorEReceipt newVisitorEReceipt) {
        Optional<VisitorEReceipt> optionalVisitorEReceipt = visitorEReceiptRepository.findById(id);
        if (optionalVisitorEReceipt.isPresent()) {
            VisitorEReceipt existingVisitorEReceipt = optionalVisitorEReceipt.get();
            existingVisitorEReceipt.setReport_type(newVisitorEReceipt.getReport_type());
            existingVisitorEReceipt.setReport_data(newVisitorEReceipt.getReport_data());
            existingVisitorEReceipt.setTimestamp(newVisitorEReceipt.getTimestamp());
            return visitorEReceiptRepository.save(existingVisitorEReceipt);
        }
        return null;
    }

    public void deleteVisitorEReceipt(int id) {
        visitorEReceiptRepository.deleteById(id);
    }
}

