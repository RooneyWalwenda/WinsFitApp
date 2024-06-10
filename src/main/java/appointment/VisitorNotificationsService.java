package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorNotificationsService {

    @Autowired
    private VisitorNotificationsRepository visitorNotificationsRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private EmailService emailService;

    public List<VisitorNotifications> getAllVisitorNotifications() {
        return visitorNotificationsRepository.findAll();
    }

    public VisitorNotifications getVisitorNotificationById(int id) {
        Optional<VisitorNotifications> optionalVisitorNotification = visitorNotificationsRepository.findById(id);
        return optionalVisitorNotification.orElse(null);
    }

    public VisitorNotifications createVisitorNotification(VisitorNotifications visitorNotification) {
        // Retrieve or save the associated Visitor entity
        Visitor visitor = visitorNotification.getVisitor();
        if (visitor.getVisitorid() == 0) { // Visitor is new and hasn't been saved
            visitor = visitorRepository.save(visitor);
        }

        // Set the Visitor entity on the VisitorNotifications entity
        visitorNotification.setVisitor(visitor);

        // Save the VisitorNotifications entity
        VisitorNotifications savedNotification = visitorNotificationsRepository.save(visitorNotification);

        // Send notification
        sendNotification(savedNotification);

        return savedNotification;
    }

    public VisitorNotifications updateVisitorNotification(int id, VisitorNotifications newVisitorNotification) {
        Optional<VisitorNotifications> optionalVisitorNotification = visitorNotificationsRepository.findById(id);
        if (optionalVisitorNotification.isPresent()) {
            VisitorNotifications existingVisitorNotification = optionalVisitorNotification.get();
            existingVisitorNotification.setNotification_type(newVisitorNotification.getNotification_type());
            existingVisitorNotification.setTimestamp(newVisitorNotification.getTimestamp());
            existingVisitorNotification.setNotification_content(newVisitorNotification.getNotification_content());

            // Retrieve or save the associated Visitor entity
            Visitor visitor = newVisitorNotification.getVisitor();
            if (visitor.getVisitorid() == 0) { // Visitor is new and hasn't been saved
                visitor = visitorRepository.save(visitor);
            }
            existingVisitorNotification.setVisitor(visitor);

            VisitorNotifications updatedNotification = visitorNotificationsRepository.save(existingVisitorNotification);
            sendNotification(updatedNotification);
            return updatedNotification;
        }
        return null;
    }

    public void deleteVisitorNotification(int id) {
        visitorNotificationsRepository.deleteById(id);
    }

    private void sendNotification(VisitorNotifications notification) {
        Visitor visitor = notification.getVisitor();
        String content = notification.getNotification_content();

        switch (notification.getNotification_type()) {
            case "SMS":
                smsService.sendSms(visitor.getPhone_number(), content);
                break;
            case "EMAIL":
                emailService.sendEmail(visitor.getEmail(), "Notification", content);
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification type: " + notification.getNotification_type());
        }
    }
}
