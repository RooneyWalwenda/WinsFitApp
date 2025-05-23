package appointment;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class VisitorNotificationsService {
	
	 private static final Logger logger = LoggerFactory.getLogger(VisitorNotificationsService.class);

    @Autowired
    private VisitorNotificationsRepository visitorNotificationsRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    

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

        // Check if the notification type is EMAIL
        if ("EMAIL".equals(notification.getNotification_type())) {
            try {
                emailService.sendHtmlEmail(visitor.getEmail(), "Notification", content);
            } catch (MessagingException e) {
                logger.error("Failed to send notification email to: {}", visitor.getEmail(), e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported notification type: " + notification.getNotification_type());
        }
    }




}
