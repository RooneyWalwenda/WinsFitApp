package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitor_e_receipt")
public class VisitorEReceiptController {

    @Autowired
    private VisitorEReceiptService visitorEReceiptService;

    @GetMapping
    public List<VisitorEReceipt> getAllVisitorEReceipts() {
        return visitorEReceiptService.getAllVisitorEReceipts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorEReceipt> getVisitorEReceiptById(@PathVariable int id) {
        VisitorEReceipt visitorEReceipt = visitorEReceiptService.getVisitorEReceiptById(id);
        if (visitorEReceipt != null) {
            return ResponseEntity.ok(visitorEReceipt);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public VisitorEReceipt createVisitorEReceipt(@RequestBody VisitorEReceipt visitorEReceipt) {
        return visitorEReceiptService.createVisitorEReceipt(visitorEReceipt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorEReceipt> updateVisitorEReceipt(@PathVariable int id, @RequestBody VisitorEReceipt newVisitorEReceipt) {
        VisitorEReceipt updatedVisitorEReceipt = visitorEReceiptService.updateVisitorEReceipt(id, newVisitorEReceipt);
        if (updatedVisitorEReceipt != null) {
            return ResponseEntity.ok(updatedVisitorEReceipt);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitorEReceipt(@PathVariable int id) {
        visitorEReceiptService.deleteVisitorEReceipt(id);
        return ResponseEntity.noContent().build();
    }
}
